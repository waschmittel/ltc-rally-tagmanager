package de.flubba.tagmanager.ui.logtable;

import javax.swing.table.DefaultTableCellRenderer;

class CellWithTooltipRenderer extends DefaultTableCellRenderer {
    @Override
    public void setValue(Object value) {
        if (value instanceof String message) {
            setToolTipText(message);
            setText(message);
        }
    }
}
