package de.flubba.tagmanager.ui;

import de.flubba.tagmanager.smartcard.CardAction;

import javax.swing.JPanel;

public abstract class CardActionPanel extends JPanel implements CardAction {
    public abstract String getTitle();
}
