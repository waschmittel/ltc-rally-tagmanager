package de.flubba.tagmanager;

import de.flubba.tagmanager.Message.TableDate;
import de.flubba.tagmanager.Message.Type;
import de.flubba.tagmanager.cardaction.CardAction;
import de.flubba.tagmanager.cardaction.LapCountAction;
import de.flubba.tagmanager.cardaction.TagAssignmentAction;
import de.flubba.tagmanager.cardaction.TagQueryAction;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Labeled;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.util.Date;

public final class UI extends Application {
    private static UI INSTANCE;

    private final SplitPane mainPane = new SplitPane();
    private final VBox left = new VBox();

    private final TextField baseUrl = new TextField("http://localhost:8080");
    private final HBox top = new HBox();

    private final Button queryMode = new Button("Tag Query Mode");
    private final Button assignmentMode = new Button("Tag Assignment Mode");
    private final Button lapCountMode = new Button("Lap Count Mode");

    private final TextField nextRunnerNumber = new TextField();
    private final Label nextRunnerLabel = new Label("Next number to assign:");
    private final CheckBox overwrite = new CheckBox("overwrite existing assignment");

    private ReaderThread currentReaderThread = null;

    private final TableView<Message> messages = new TableView<>();
    private final TableColumn<Message, Date> dateColumn = new TableColumn<>("Date");
    private final TableColumn<Message, String> messageColumn = new TableColumn<>("Message");

    private final TextField runnerNumber = new TextField();
    private final TextField runnerName = new TextField();

    @Override
    public void start(Stage primaryStage) {
        INSTANCE = this;
        initMainLayout();
        initMessages();
        initLeft();
        initWindow(primaryStage);
        styleFields();

        assignmentMode.setOnAction(event -> assignmentMode());
        lapCountMode.setOnAction(event -> lapCountMode());
        queryMode.setOnAction(event -> queryMode());

        lapCountMode();
    }

    private void queryMode() {
        ObservableList<Node> children = left.getChildren();
        removeModeDependentFields(children);
        setReaderThread(new TagQueryAction());
        addMessage("Tag Query Mode enabled");
    }

    private void assignmentMode() {
        ObservableList<Node> children = left.getChildren();
        removeModeDependentFields(children);
        children.add(nextRunnerLabel);
        children.add(nextRunnerNumber);
        children.add(overwrite);
        setReaderThread(new TagAssignmentAction());
        addMessage("Tag Assignment Mode enabled");
    }

    private void lapCountMode() {
        ObservableList<Node> children = left.getChildren();
        removeModeDependentFields(children);
        children.add(runnerNumber);
        children.add(runnerName);
        setReaderThread(new LapCountAction());
        addMessage("Lap Count Mode enabled");
    }

    private void removeModeDependentFields(ObservableList<Node> children) {
        children.remove(runnerName);
        children.remove(runnerNumber);
        children.remove(nextRunnerLabel);
        children.remove(nextRunnerNumber);
        children.remove(overwrite);
    }

    private void styleFields() {
        runnerNumber.setFont(new Font(300));
        runnerName.setFont(new Font(30));
        runnerName.setEditable(false);
        runnerNumber.setEditable(false);
        runnerName.setAlignment(Pos.CENTER);
        runnerNumber.setAlignment(Pos.CENTER);
        runnerNumber.setStyle("-fx-padding: 3px");

        nextRunnerLabel.setFont(new Font(20));
        nextRunnerNumber.setFont(new Font(100));
        nextRunnerNumber.setAlignment(Pos.CENTER);
    }

    private void setReaderThread(CardAction cardAction) {
        if (currentReaderThread != null) {
            currentReaderThread.interrupt();
            addMessage("Waiting for current Reader to stop.");
            try {
                currentReaderThread.join(); // this will block the UI, but it should not take longer than a second
            } catch (InterruptedException e) {
                addErrorMessage("I've been interrupted. This is weird.");
                Thread.currentThread().interrupt();
            }
            addMessage("Current Reader stopped.");
        }
        currentReaderThread = new ReaderThread(cardAction);
        currentReaderThread.start();
    }

    private void initLeft() {
        left.setAlignment(Pos.TOP_CENTER);
        left.getChildren().add(baseUrl);
        baseUrl.setAlignment(Pos.CENTER);
        left.getChildren().add(top);
        top.setSpacing(10);
        top.setFillHeight(true);
        top.setPrefHeight(20000);
        top.setAlignment(Pos.TOP_CENTER);
        top.getChildren().add(queryMode);
        top.getChildren().add(assignmentMode);
        top.getChildren().add(lapCountMode);

        left.setAlignment(Pos.CENTER);
        left.setSpacing(10);
    }

    private void initMainLayout() {
        mainPane.getItems().add(left);
        mainPane.getItems().add(messages);
    }

    private void initMessages() {
        messages.setRowFactory(row -> new TableRow<>() {
            @Override
            public void updateItem(Message message, boolean empty) {
                super.updateItem(message, empty);

                if (message == null || empty) {
                    setStyle("");
                } else {
                    if (message.message.length() > 20) {
                        for (Node cell : getChildren()) {
                            if (cell instanceof Labeled labeledCell) {
                                labeledCell.setTextFill(message.type.color);
                            }
                        }
                    }
                }
            }
        });
        messages.getColumns().add(dateColumn);
        messages.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        dateColumn.setMinWidth(80);
        dateColumn.setMaxWidth(80);
        messageColumn.setCellValueFactory(new PropertyValueFactory<>("message"));
        messages.getColumns().add(messageColumn);
    }

    private void initWindow(Stage primaryStage) {
        Rectangle2D bounds = Screen.getPrimary().getVisualBounds();

        primaryStage.setWidth(bounds.getWidth() - 100);
        primaryStage.setHeight(bounds.getHeight() - 100);
        primaryStage.setMaximized(true);

        Scene scene = new Scene(mainPane);
        primaryStage.setTitle("LTC Rally Tag Manager");
        primaryStage.setScene(scene);
        primaryStage.show();

        primaryStage.setOnCloseRequest(event -> {
            if (currentReaderThread != null) {
                currentReaderThread.interrupt();
            }
            Platform.exit();
        });
    }

    public static void main(String[] args) {
        launch(args);
    }

    public static void addErrorMessage(String messageString) {
        addMessage(messageString, Type.ERROR);
    }

    public static void addMessage(String messageString) {
        addMessage(messageString, Type.INFO);
    }

    private static void addMessage(String messageString, Type type) {
        Platform.runLater(() -> {
            Message message = new Message();
            message.date = new TableDate();
            message.message = messageString;
            message.type = type;
            INSTANCE.messages.getItems().add(0, message);
        });
    }

    public static String getBaseUrl() {
        return INSTANCE.baseUrl.getText().trim();
    }

    public static AssignmentInformation getAssignmentInformation() throws NumberFormatException {
        return new AssignmentInformation(
                Long.valueOf(INSTANCE.nextRunnerNumber.getText().trim()),
                INSTANCE.overwrite.isSelected());
    }

    public static void setNextRunnerNumber(long next) {
        Platform.runLater(() ->
                INSTANCE.nextRunnerNumber.setText(String.valueOf(next)));
    }

    public static void setLastRunner(RunnerDto runner) {
        Platform.runLater(() -> {
            INSTANCE.runnerName.setText(runner.name());
            INSTANCE.runnerNumber.setText(String.valueOf(runner.id()));
        });
    }
}
