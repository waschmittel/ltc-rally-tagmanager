package de.flubba.tagmanager;

// This class exists to prevent "Error: JavaFX runtime components are missing, and are required to run this application"
// see https://www.reddit.com/r/JavaFX/comments/k7aa9q/javafx_error_error_javafx_runtime_components_are/
public final class Tagmanager {
    private Tagmanager() {
    }

    public static void main(String... args) {
        UI.main(args);
    }
}
