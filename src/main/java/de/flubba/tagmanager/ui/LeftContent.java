package de.flubba.tagmanager.ui;

import de.flubba.tagmanager.smartcard.CardAction;
import de.flubba.tagmanager.smartcard.ReaderThread;
import lombok.extern.slf4j.Slf4j;
import org.kordamp.ikonli.fontawesome6.FontAwesomeSolid;
import org.kordamp.ikonli.swing.FontIcon;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SpringLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.util.List;

import static javax.swing.SpringLayout.EAST;
import static javax.swing.SpringLayout.NORTH;
import static javax.swing.SpringLayout.SOUTH;
import static javax.swing.SpringLayout.WEST;

@Slf4j
public class LeftContent extends JPanel {

    private final JTabbedPane tabs = new JTabbedPane();

    private final List<CardActionPanel> cardActionPanels = List.of(
            new TagAssignmentTab(),
            new TagQueryTab(),
            new LapCountingTab()
    );

    private ReaderThread currentReaderThread = null;

    private void setReaderThread(CardAction cardAction) {
        if (currentReaderThread != null) {
            currentReaderThread.interrupt();
            log.info("Waiting for current Reader to stop.");
            try {
                currentReaderThread.join(); // this will block the UI, but it should not take longer than a second
            } catch (InterruptedException e) {
                log.error("I've been interrupted. This is weird.");
                Thread.currentThread().interrupt();
            }
            log.info("Current Reader stopped.");
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

        tabs.putClientProperty("JTabbedPane.tabType", "card");
        tabs.putClientProperty("JTabbedPane.hasFullBorder", true);
        tabs.putClientProperty("JTabbedPane.tabAreaAlignment", "fill");
        tabs.setPreferredSize(new Dimension(700, 800));
        tabs.addChangeListener(e -> setActionToCurrentCard());
        addCardActionTabs();
        tabs.setSelectedIndex(2);

        var hostAndPortConfig = new HostAndPortConfig();
        var logSettings = new SettingsPanel();

        add(hostAndPortConfig);
        add(logSettings);
        add(tabs);

        springLayout.putConstraint(NORTH, hostAndPortConfig, 0, NORTH, this);
        springLayout.putConstraint(EAST, hostAndPortConfig, 0, EAST, this);
        springLayout.putConstraint(WEST, hostAndPortConfig, 0, WEST, this);
        springLayout.putConstraint(NORTH, logSettings, 6, SOUTH, hostAndPortConfig);
        springLayout.putConstraint(WEST, logSettings, 0, WEST, this);
        springLayout.putConstraint(EAST, logSettings, 0, EAST, this);
        springLayout.putConstraint(NORTH, tabs, 6, SOUTH, logSettings);
        springLayout.putConstraint(WEST, this, 0, WEST, hostAndPortConfig);
        springLayout.putConstraint(EAST, this, 0, EAST, hostAndPortConfig);
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
        var icons = List.of(
            FontIcon.of(FontAwesomeSolid.ID_CARD, 16, new Color(76, 175, 80)),
            FontIcon.of(FontAwesomeSolid.SEARCH, 16, new Color(33, 150, 243)),
            FontIcon.of(FontAwesomeSolid.FLAG_CHECKERED, 16, new Color(255, 87, 34))
        );

        for (int i = 0; i < cardActionPanels.size(); i++) {
            var tab = cardActionPanels.get(i);
            tabs.addTab(tab.getTitle(), icons.get(i), tab);
        }
    }
}
