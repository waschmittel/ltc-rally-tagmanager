package de.flubba.tagmanager.ui;

import de.flubba.tagmanager.smartcard.CardAction;
import de.flubba.tagmanager.smartcard.ReaderThread;
import de.flubba.tagmanager.ui.LogTable.LogMessage.Level;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import java.awt.Dimension;
import java.util.List;

import static de.flubba.tagmanager.ui.LogTable.LogMessage.Level.INFO;
import static de.flubba.tagmanager.ui.UI.LOG_TABLE;
import static javax.swing.SpringLayout.EAST;
import static javax.swing.SpringLayout.NORTH;
import static javax.swing.SpringLayout.SOUTH;
import static javax.swing.SpringLayout.WEST;
import static javax.swing.SwingConstants.CENTER;

public class LeftContent extends JPanel {

    private final JTabbedPane tabs = new JTabbedPane();

    private final List<CardActionPanel> cardActionPanels = List.of(
            new TagAssignmentTab(),
            new TagQueryTab(),
            new LapCoutingTab()
    );

    private ReaderThread currentReaderThread = null;

    private void setReaderThread(CardAction cardAction) {
        if (currentReaderThread != null) {
            currentReaderThread.interrupt();
            LOG_TABLE.addMessage(INFO, "Waiting for current Reader to stop.");
            try {
                currentReaderThread.join(); // this will block the UI, but it should not take longer than a second
            } catch (InterruptedException e) {
                LOG_TABLE.addMessage(Level.ERROR, "I've been interrupted. This is weird.");
                Thread.currentThread().interrupt();
            }
            LOG_TABLE.addMessage(INFO, "Current Reader stopped.");
        }
        currentReaderThread = new ReaderThread(cardAction);
        currentReaderThread.start();
    }

    @Override
    public void removeNotify() {
        if (currentReaderThread != null) {
            currentReaderThread.interrupt();
        }
        super.removeNotify();
    }


    public LeftContent() {
        var springLayout = new SpringLayout();
        setLayout(springLayout);

        var urlField = new JTextField("http://localhost:8080");//TODO: proper URL field with validation?
        urlField.setMinimumSize(new Dimension(300, 30));
        urlField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        urlField.setHorizontalAlignment(CENTER);

        tabs.setPreferredSize(new Dimension(700, 800));
        tabs.addChangeListener(e -> setActionToCurrentCard());
        addCardActionTabs();
        tabs.setSelectedIndex(2);

        add(urlField);
        add(tabs);

        springLayout.putConstraint(NORTH, urlField, 0, NORTH, this);
        springLayout.putConstraint(EAST, urlField, 0, EAST, this);
        springLayout.putConstraint(WEST, urlField, 0, WEST, this);
        springLayout.putConstraint(NORTH, tabs, 6, SOUTH, urlField);
        springLayout.putConstraint(WEST, this, 0, WEST, urlField);
        springLayout.putConstraint(EAST, this, 0, EAST, urlField);
        springLayout.putConstraint(WEST, this, 0, WEST, tabs);
        springLayout.putConstraint(EAST, this, 0, EAST, tabs);
        springLayout.putConstraint(SOUTH, this, 0, SOUTH, tabs);

    }

    private void setActionToCurrentCard() {
        if (tabs.getComponentAt(tabs.getSelectedIndex()) instanceof CardActionPanel cardActionPanel) {
            setReaderThread(cardActionPanel);
        }
    }

    private void addCardActionTabs() {
        for (var tab : cardActionPanels) {
            tabs.addTab(tab.getTitle(), tab);
        }
    }
}
