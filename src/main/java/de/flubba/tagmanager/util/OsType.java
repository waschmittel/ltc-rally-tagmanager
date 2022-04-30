package de.flubba.tagmanager.util;

import static java.util.Locale.ENGLISH;

public enum OsType {
    Windows, MacOS, Linux, Other;

    private static OsType os;

    public static OsType get() {
        if (os == null) {
            os = getFromSystem();
        }
        return os;
    }

    private static OsType getFromSystem() {
        String osName = System.getProperty("os.name", "").toLowerCase(ENGLISH);
        if (osName.contains("mac") || osName.contains("darwin")) {
            return MacOS;
        } else if (osName.contains("windows")) {
            return Windows;
        } else if (osName.contains("nux") || osName.contains("nix")) {
            return Linux;
        }
        return Other;
    }
}
