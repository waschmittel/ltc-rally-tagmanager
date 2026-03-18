package de.flubba.tagmanager.ui;

import oshi.SystemInfo;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.hardware.PowerSource;

import javax.swing.SwingUtilities;
import javax.swing.Timer;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

public class BatteryMonitor {
    private static final int UPDATE_INTERVAL_MS = 5000; // Update every 5 seconds
    private final SystemInfo systemInfo;
    private final HardwareAbstractionLayer hardware;
    private final Timer updateTimer;
    private final List<Consumer<BatteryStatus>> listeners = new CopyOnWriteArrayList<>();
    private BatteryStatus lastStatus;

    public BatteryMonitor() {
        systemInfo = new SystemInfo();
        hardware = systemInfo.getHardware();

        updateTimer = new Timer(UPDATE_INTERVAL_MS, e -> updateBatteryStatus());
        updateBatteryStatus(); // Initial update
        updateTimer.start();
    }

    public void addListener(Consumer<BatteryStatus> listener) {
        listeners.add(listener);
        if (lastStatus != null) {
            listener.accept(lastStatus);
        }
    }

    private void updateBatteryStatus() {
        List<PowerSource> powerSources = hardware.getPowerSources();

        BatteryStatus status;
        if (powerSources.isEmpty()) {
            status = new BatteryStatus(false, 0.0, false, "No battery detected");
        } else {
            PowerSource battery = powerSources.get(0);
            double chargePercent = battery.getRemainingCapacityPercent();
            boolean isCharging = battery.isCharging();
            boolean isPluggedIn = battery.isPowerOnLine();

            String statusText = formatStatus(chargePercent, isCharging, isPluggedIn);
            status = new BatteryStatus(true, chargePercent, isCharging, statusText);
        }

        lastStatus = status;
        notifyListeners(status);
    }

    private String formatStatus(double chargePercent, boolean isCharging, boolean isPluggedIn) {
        if (isCharging) {
            return String.format("%.0f%% (Charging)", chargePercent * 100);
        } else if (isPluggedIn) {
            return String.format("%.0f%% (Plugged In)", chargePercent * 100);
        } else {
            return String.format("%.0f%% (On Battery)", chargePercent * 100);
        }
    }

    private void notifyListeners(BatteryStatus status) {
        SwingUtilities.invokeLater(() -> {
            for (Consumer<BatteryStatus> listener : listeners) {
                listener.accept(status);
            }
        });
    }

    public record BatteryStatus(
            boolean hasBattery,
            double chargePercent,
            boolean isCharging,
            String statusText
    ) {}
}
