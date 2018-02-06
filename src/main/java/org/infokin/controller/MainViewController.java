package org.infokin.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.LoadException;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.infokin.Global;
import org.infokin.controller.api.Controller;
import org.infokin.util.LayoutLoader;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Controller for main view.
 */
public class MainViewController extends Controller {

    /*------------------
    | Member variables |
    ------------------*/

    /**
     * If true, the timer and the application is in running mode, if false, the timer is stopped.
     */
    private boolean isRunning;

    /**
     * Sequence timer.
     */
    private Timer timer;

    /**
     * Sequence state.
     * <p>
     * 0 - green marks
     * 1 - orange marks
     * 2 - red marks
     * 3 - displays random word
     */
    private int sequenceState = 0;

    /**
     * The length of the displayed word. This corresponds to a word list and the difficulty of the recognition.
     */
    private int textLength = 1;

    /**
     * The amount of milliseconds that text is displayed.
     */
    private int flashDuration = 200;

    /**
     * The even interval of the sequence.
     */
    private int intervalDuration = 1000;

    /**
     * An indication that the interval changed (used for user interface processing).
     */
    private boolean intervalDurationChanged = false;

    /*---------------------------
    | User interface components |
    ---------------------------*/

    /**
     * The root node of this view.
     */
    @FXML
    private BorderPane rootNode;

    @FXML
    private Canvas canvas;

    @FXML
    private Label outputLabel;

    @FXML
    private Slider textLengthSlider;

    @FXML
    private Slider flashDurationSlider;

    @FXML
    private Slider intervalDurationSlider;

    /*--------------------
    | Life cycle methods |
    --------------------*/

    /**
     * Initializes the controller class. This method is automatically called
     * after the FXML file has been loaded.
     */
    @FXML
    private void initialize() {
        // Set handling for text length slider
        textLengthSlider.valueProperty().addListener((value, oldValue, newValue) -> textLength = newValue.intValue());

        // Set handling for flash duration slider
        flashDurationSlider.valueProperty().addListener((value, oldValue, newValue) -> flashDuration = newValue.intValue());

        // Set handling for interval duration.
        intervalDurationSlider.valueProperty().addListener((value, oldValue, newValue) -> {
            intervalDuration = newValue.intValue();
            intervalDurationChanged = true;
        });

        intervalDurationSlider.setOnMouseReleased((MouseEvent event) -> {
            if (intervalDurationChanged && isRunning) {
                // Stop timer
                timer.cancel();

                // Restart time with new interval duration
                startSequenceTimer(intervalDuration);

                intervalDurationChanged = false;
            }
        });

        // Set space bar handler
        rootNode.setOnKeyReleased(keyEvent -> {
            if (keyEvent.getCode().equals(KeyCode.SPACE)) {
                handleSpaceBarPressed();
            }
        });
    }

    /*-------------------------
    | User interface handlers |
    -------------------------*/

    @FXML
    private void handleCloseApplication() {
        Platform.exit();
        System.exit(0);
    }

    @FXML
    private void handleAboutApplication() {
        Stage dialogStage = new Stage();
        AboutDialogViewController aboutViewController = null;

        try {
            // Load dialog layout
            aboutViewController = (AboutDialogViewController) LayoutLoader.loadLayout(Global.ABOUT_DIALOG_VIEW_LAYOUT).getController();
        } catch (LoadException e) {
            e.printStackTrace();

            Platform.exit();
            System.exit(0);
        }

        // Add event handler to close dialog
        aboutViewController.getOkButton().setOnAction(event -> dialogStage.close());

        dialogStage.setScene(new Scene(aboutViewController.getRootNode()));
        dialogStage.setResizable(false);

        dialogStage.initOwner(Global.primaryStage);
        dialogStage.initModality(Modality.APPLICATION_MODAL);

        dialogStage.show();
    }

    @FXML
    private void handleSpaceBarPressed() {
        if (isRunning) {
            stop();
        } else {
            start();
        }
    }

    /*---------
    | Methods |
    ---------*/

    /**
     * Begins display of the sequence.
     */
    private void start() {
        // Remove information text
        clearText();

        // Start timer
        startSequenceTimer(intervalDuration);

        isRunning = true;
    }

    /**
     * Stops display of the sequence and resets user interface.
     */
    private void stop() {
        // Stop timer
        timer.cancel();
        isRunning = false;

        // Reset sequence state
        sequenceState = 0;

        // Reset user interface
        clearMarks();

        // Set information text
        displayText("Press space bar to start/stop");
    }

    /**
     * Starts the sequence timer. The Timer will fire continuously in even intervals until it is canceled.
     *
     * @param interval The length of an interval in milliseconds.
     */
    private void startSequenceTimer(long interval) {
        timer = new Timer();

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (sequenceState == 0) {
                    displayMarks(Color.GREEN);
                    sequenceState++;
                } else if (sequenceState == 1) {
                    displayMarks(Color.ORANGE);
                    sequenceState++;
                } else if (sequenceState == 2) {
                    displayMarks(Color.RED);
                    sequenceState++;
                } else if (sequenceState == 3) {
                    clearMarks();
                    flashText();
                    sequenceState = 0;
                }
            }
        }, 0, interval);
    }

    /**
     * Displays colored marks.
     *
     * @param color The color of the marks.
     */
    private void displayMarks(Color color) {
        clearMarks();

        double width = canvas.getWidth();
        double height = canvas.getHeight();

        GraphicsContext graphicsContext = canvas.getGraphicsContext2D();

        graphicsContext.setStroke(color);
        graphicsContext.setLineWidth(5);

        int lineLength = 12;
        int margin = 10;

        // Draw marks
        graphicsContext.strokeLine(width / 2, margin, width / 2, margin + lineLength);
        graphicsContext.strokeLine(width / 2, height - margin, width / 2, height - margin - lineLength);
        graphicsContext.strokeLine(margin, height / 2, margin + lineLength, height / 2);
        graphicsContext.strokeLine(width - margin, height / 2, width - margin - lineLength, height / 2);
    }

    /**
     * Clears the canvas area.
     */
    private void clearMarks() {
        canvas.getGraphicsContext2D().clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    /**
     * Displays a random word for a very short amount of time.
     */
    private void flashText() {
        String word = getNextWord();

        Platform.runLater(() -> displayText(word));

        long startTime = System.currentTimeMillis();
        long elapsedTime = 0L;

        while (elapsedTime < flashDuration) {
            elapsedTime = System.currentTimeMillis() - startTime;
        }

        Platform.runLater(this::clearText);
    }

    /**
     * Gets a random word from the currently selected word list.
     *
     * @return A random word using the specified settings.
     */
    private String getNextWord() {
        Random random = new Random();

        // Create random index value
        int value = random.nextInt(Global.WORDS[textLength - 1].length);

        // Return word at index
        return Global.WORDS[textLength - 1][value];
    }

    /**
     * Displays {@code text} in the center of the output area.
     *
     * @param text Text that is displayed.
     */
    private void displayText(String text) {
        outputLabel.setText(text);
    }

    /**
     * Removes text from display.
     */
    private void clearText() {
        outputLabel.setText("");
    }

    /*---------------------
    | Getters and Setters |
    ---------------------*/

    /**
     * {@inheritDoc}
     */
    @Override
    public BorderPane getRootNode() {
        return rootNode;
    }
}
