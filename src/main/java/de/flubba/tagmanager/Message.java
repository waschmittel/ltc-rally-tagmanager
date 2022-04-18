package de.flubba.tagmanager;

import javafx.scene.paint.Color;
import org.apache.commons.lang3.time.FastDateFormat;

import java.util.Date;

public class Message {
    public enum Type {
        ERROR(Color.RED), WARNING(Color.ORANGE), INFO(Color.BLACK);

        public final Color color;

        Type(Color color) {
            this.color = color;
        }
    }

    public static class TableDate extends Date {
        private static final FastDateFormat formatter = FastDateFormat.getInstance("HH:mm:ss");

        @Override
        public String toString() {
            return formatter.format(this);
        }
    }

    public TableDate date;
    public String message;
    public Type type = Type.INFO;

    public TableDate getDate() {
        return date;
    }

    public void setDate(TableDate date) {
        this.date = date;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
