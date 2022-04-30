package de.flubba.tagmanager.util;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public interface SimpleDocumentListener extends DocumentListener {
    @Override
    default void insertUpdate(DocumentEvent e) {
        onChange();
    }

    @Override
    default void removeUpdate(DocumentEvent e) {
        onChange();
    }

    @Override
    default void changedUpdate(DocumentEvent e) {
        onChange();
    }

    void onChange();
}
