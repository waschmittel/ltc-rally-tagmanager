package de.flubba.tagmanager.ui.logtable;

import javax.swing.table.AbstractTableModel;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

class LogTableModel extends AbstractTableModel {
    private static final List<Column<?>> COLUMNS = List.of(
            new Column<>("Time", String.class, TableLogMessage::datetime),
            new Column<>("Level", Level.class, TableLogMessage::level),
            new Column<>("Message", String.class, TableLogMessage::message)
    );
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss.SSS").withZone(ZoneId.systemDefault());

    private final ArrayList<TableLogMessage> messages = new ArrayList<>();

    enum Level {
        ERROR, WARN, INFO
    }

    private record TableLogMessage(Level level, String datetime, String message) {}

    public void addLogMessage(Level level, String message) {
        messages.addFirst(new TableLogMessage(level, formatter.format(Instant.now()), message));
    }

    private record Column<T>(String name, Class<T> clazz, Function<TableLogMessage, T> valueSupplier) {
        T getValue(TableLogMessage logMessage) {
            return valueSupplier.apply(logMessage);
        }
    }

    @Override
    public int getRowCount() {
        return messages.size();
    }

    @Override
    public int getColumnCount() {
        return COLUMNS.size();
    }

    @Override
    public String getColumnName(int columnIndex) {
        return COLUMNS.get(columnIndex).name;
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return COLUMNS.get(columnIndex).clazz;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return COLUMNS.get(columnIndex).getValue(messages.get(rowIndex));
    }
}
