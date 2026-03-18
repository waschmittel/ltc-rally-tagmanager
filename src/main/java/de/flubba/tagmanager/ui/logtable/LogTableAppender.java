package de.flubba.tagmanager.ui.logtable;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;

import java.util.Optional;

import static ch.qos.logback.classic.Level.DEBUG;
import static ch.qos.logback.classic.Level.ERROR;
import static ch.qos.logback.classic.Level.INFO;
import static ch.qos.logback.classic.Level.WARN;
import static de.flubba.tagmanager.ui.UI.LOG_TABLE;

public class LogTableAppender extends AppenderBase<ILoggingEvent> {
    @Override
    protected void append(ILoggingEvent event) {
        getLevel(event).ifPresent(level -> LOG_TABLE.addMessage(level, event.getFormattedMessage()));
    }

    private Optional<LogTableModel.Level> getLevel(ILoggingEvent event) {
        var logbackLevel = event.getLevel();
        if (ERROR.equals(logbackLevel)) {
            return Optional.of(LogTableModel.Level.ERROR);
        }
        if (WARN.equals(logbackLevel)) {
            return Optional.of(LogTableModel.Level.WARN);
        }
        if (INFO.equals(logbackLevel)) {
            return Optional.of(LogTableModel.Level.INFO);
        }
        if (DEBUG.equals(logbackLevel)) {
            return Optional.of(LogTableModel.Level.DEBUG);
        }
        return Optional.empty();
    }
}

