package org.infokin.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import org.infokin.controller.api.Controller;

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

    private int flashDuration = 200;

    /*---------------------------
    | User interface components |
    ---------------------------*/

    /**
     * The root node of this view.
     */
    @FXML
    private BorderPane rootNode;

    @FXML
    private AnchorPane outputPane;

    @FXML
    private Canvas canvas;

    @FXML
    private Label outputLabel;

    @FXML
    private Slider flashDurationSlider;

    /*--------------------
    | Life cycle methods |
    --------------------*/

    /**
     * Initializes the controller class. This method is automatically called
     * after the FXML file has been loaded.
     */
    @FXML
    private void initialize() {
        // Set handling for flash duration slider
        flashDurationSlider.valueProperty().addListener((value, oldValue, newValue) -> flashDuration = newValue.intValue());

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

        /*
         * Prepare and start timer.
         *
         * The Timer will fire continuously in equal intervals until it is canceled.
         */
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
                    flashText("the");
                    state = 0;
                }
            }
        }, 0, 1000);

        isRunning = true;
    }

    private void stop() {
        timer.cancel();
        isRunning = false;

        state = 0;

        clearMarks();

        // Set information text
        displayText("Press space bar to start/stop");
    }

    private void flashText(String text) {
        Platform.runLater(() -> displayText(text));

        long startTime = System.currentTimeMillis();
        long elapsedTime = 0L;

        while (elapsedTime < flashDuration) {
            elapsedTime = System.currentTimeMillis() - startTime;
        }

        Platform.runLater(this::clearText);
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
