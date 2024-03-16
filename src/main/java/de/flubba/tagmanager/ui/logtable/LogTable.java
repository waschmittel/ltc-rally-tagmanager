package de.flubba.tagmanager.ui.logtable;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ScrollPaneConstants;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;

import static java.awt.Font.BOLD;

public class LogTable extends JPanel {
    private final LogTableModel logMessages = new LogTableModel();
    private final JTable table = new JTable(logMessages);
    private final JScrollPane scrollPane = new JScrollPane(table);

    public LogTable() {
        table.getColumnModel().getColumn(0).setPreferredWidth(1500);
        table.getColumnModel().getColumn(1).setPreferredWidth(800);
        table.getColumnModel().getColumn(1).setCellRenderer(new LevelRenderer(new Font(getFont().getName(), BOLD, getFont().getSize())));
        table.getColumnModel().getColumn(2).setPreferredWidth(8000);
        table.getColumnModel().getColumn(2).setCellRenderer(new CellWithTooltipRenderer());
        table.setFillsViewportHeight(true);

        scrollPane.setPreferredSize(new Dimension(700, 800));
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        setLayout(new GridLayout(1, 1));
        add(scrollPane);
    }

    void addMessage(LogTableModel.Level level, String message) {
        logMessages.addLogMessage(level, message);
        scrollPane.getViewport().updateUI();
    }
}
