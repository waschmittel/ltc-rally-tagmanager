package de.flubba.tagmanager.ui.logtable;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;

import java.util.Optional;

import static de.flubba.tagmanager.ui.UI.LOG_TABLE;

public class LogTableAppender extends AppenderBase<ILoggingEvent> {
    @Override
    protected void append(ILoggingEvent event) {
        getLevel(event).ifPresent(level -> LOG_TABLE.addMessage(level, event.getFormattedMessage()));
    }

    private Optional<LogTableModel.Level> getLevel(ILoggingEvent event) {
        var logbackLevel = event.getLevel();
        if (Level.ERROR.equals(logbackLevel)) {
            return Optional.of(LogTableModel.Level.ERROR);
        }
        if (Level.WARN.equals(logbackLevel)) {
            return Optional.of(LogTableModel.Level.WARN);
        } else if (Level.INFO.equals(logbackLevel)) {
            return Optional.of(LogTableModel.Level.INFO);
        }
        return Optional.empty();
    }
}

