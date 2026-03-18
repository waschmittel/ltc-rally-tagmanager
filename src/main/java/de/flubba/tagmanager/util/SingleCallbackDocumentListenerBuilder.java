package de.flubba.tagmanager.util;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public final class SingleCallbackDocumentListenerBuilder {
    private SingleCallbackDocumentListenerBuilder() {}

    public static DocumentListener onEveryEvent(Runnable callback) {
        return new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                callback.run();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                callback.run();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                callback.run();
            }
        };
    }
}
