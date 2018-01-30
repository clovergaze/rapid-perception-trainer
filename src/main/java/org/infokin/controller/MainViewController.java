package org.infokin.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import org.infokin.Global;
import org.infokin.controller.api.Controller;

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

    private boolean isRunning;

    private Timer timer;

    private int state = 0;

    private int textLength = 1;

    private int flashDuration = 200;

    private int intervalDuration = 1000;

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
                timer.cancel();
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

    private void start() {
        clearText();
        startSequenceTimer(intervalDuration);

        isRunning = true;
    }

    /*
     * Prepare and start timer.
     *
     * The Timer will fire continuously in equal intervals until it is canceled.
     */
    private void startSequenceTimer(long interval) {
        timer = new Timer();

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (state == 0) {
                    displayMarks(Color.GREEN);
                    state++;
                } else if (state == 1) {
                    displayMarks(new Color(0.9, 0.9, 0.0, 1.0));
                    state++;
                } else if (state == 2) {
                    displayMarks(Color.RED);
                    state++;
                } else if (state == 3) {
                    clearMarks();
                    flashText();
                    state = 0;
                }
            }
        }, 0, interval);
    }

    private void stop() {
        timer.cancel();
        isRunning = false;

        state = 0;

        clearMarks();

        // Set information text
        displayText("Press space bar to start/stop");
    }

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

    private String getNextWord() {
        Random random = new Random();

        int value = random.nextInt(Global.WORDS[textLength - 1].length);

        return Global.WORDS[textLength - 1][value];
    }

    private void displayText(String text) {
        outputLabel.setText(text);
    }

    private void clearText() {
        outputLabel.setText("");
    }

    private void displayMarks(Color color) {
        clearMarks();

        double width = canvas.getWidth();
        double height = canvas.getHeight();

        GraphicsContext graphicsContext = canvas.getGraphicsContext2D();

        graphicsContext.setStroke(color);
        graphicsContext.setLineWidth(5);

        int lineLength = 12;
        int margin = 10;

        graphicsContext.strokeLine(width / 2, margin, width / 2, margin + lineLength);
        graphicsContext.strokeLine(width / 2, height - margin, width / 2, height - margin - lineLength);
        graphicsContext.strokeLine(margin, height / 2, margin + lineLength, height / 2);
        graphicsContext.strokeLine(width - margin, height / 2, width - margin - lineLength, height / 2);
    }

    private void clearMarks() {
        canvas.getGraphicsContext2D().clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
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
