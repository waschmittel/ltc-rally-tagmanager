package de.flubba.tagmanager.ui;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static java.awt.Color.GREEN;
import static java.awt.Color.RED;
import static java.awt.Color.YELLOW;

public class LogTable extends JPanel {
    private final LogTableModel logMessages = new LogTableModel();
    private final JTable table = new JTable(logMessages);
    private final JScrollPane scrollPane = new JScrollPane(table);

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss.SSS").withZone(ZoneId.systemDefault());

    public void addMessage(LogMessage.Level level, String message) {
        logMessages.addLogMessage(new LogMessage(level, formatter.format(Instant.now()), message));
        scrollPane.getVerticalScrollBar().setVisible(true); //necessary to make it appear as soon as it's necessary
        repaint();
    }

    public record LogMessage(Level level, String datetime, String message) {
        public enum Level {
            ERROR, WARN, INFO
        }
    }

    public LogTable() {
        table.getColumnModel().getColumn(0).setPreferredWidth(1500);
        table.getColumnModel().getColumn(1).setPreferredWidth(800);
        table.getColumnModel().getColumn(1).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public void setValue(Object value) {
                if (value instanceof LogMessage.Level level) {
                    setForeground(switch (level) {
                        case ERROR -> RED;
                        case WARN -> YELLOW;
                        case INFO -> GREEN;
                    });
                    setText(level.toString());
                } else {
                    super.setValue(value);
                }
            }
        });
        table.getColumnModel().getColumn(2).setPreferredWidth(8000);
        table.getColumnModel().getColumn(2).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public void setValue(Object value) {
                if (value instanceof String message) {
                    setToolTipText(message);
                    setText(message);
                }
            }
        });
        table.setFillsViewportHeight(true);

        scrollPane.setPreferredSize(new Dimension(700, 800));
        setLayout(new GridLayout(1, 1));
        add(scrollPane);
    }

    private static class LogTableModel extends AbstractTableModel {
        private final ArrayList<LogMessage> messages = new ArrayList<>();

        public void addLogMessage(LogMessage logMessage) {
            messages.add(0, logMessage);
        }

        private record Column<T>(String name, Class<T> clazz, Function<LogMessage, T> valueSupplier) {
            T getValue(LogMessage logMessage) {
                return valueSupplier.apply(logMessage);
            }
        }

        private static final List<Column<?>> COLUMNS = List.of(
                new Column<>("Time", String.class, LogMessage::datetime),
                new Column<>("Level", LogMessage.Level.class, LogMessage::level),
                new Column<>("Message", String.class, LogMessage::message)
        );


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
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return false;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            return COLUMNS.get(columnIndex).getValue(messages.get(rowIndex));
        }
    }
}
