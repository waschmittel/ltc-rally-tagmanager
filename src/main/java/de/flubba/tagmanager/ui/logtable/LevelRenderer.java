package de.flubba.tagmanager.ui.logtable;

import lombok.RequiredArgsConstructor;

import javax.swing.table.DefaultTableCellRenderer;
import java.awt.Color;
import java.awt.Font;

import static java.awt.Color.RED;

@RequiredArgsConstructor
class LevelRenderer extends DefaultTableCellRenderer {
    private static final Color DARK_YELLOW = new Color(160, 160, 0);
    private static final Color DARK_GREEN = new Color(0, 160, 0);

    private final Font levelFont;

    @Override
    public void setValue(Object value) {
        if (value instanceof LogTableModel.Level level) {
            setForeground(switch (level) {
                case ERROR -> RED;
                case WARN -> DARK_YELLOW;
                case INFO -> DARK_GREEN;
            });
            setText(level.toString());
            setFont(levelFont);
        } else {
            super.setValue(value);
        }
    }
}
