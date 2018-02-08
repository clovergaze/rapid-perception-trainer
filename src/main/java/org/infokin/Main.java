package org.infokin;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.LoadException;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.infokin.controller.MainViewController;
import org.infokin.util.LayoutLoader;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

/**
 * Contains entry point and setup code of the application.
 */
public class Main extends Application {

    /**
     * Entry point of the application.
     *
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        // Launch JavaFX application
        launch(args);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Initializes and displays the primary stage.
     * </p>
     */
    @Override
    public void start(Stage primaryStage) {
        // Save application instance for global usage
        Global.application = this;

        // Save main window instance
        Global.primaryStage = primaryStage;

        loadWordLists();

        try {
            // Load main layout
            Global.mainViewController = (MainViewController) LayoutLoader.loadLayout(Global.MAIN_VIEW_LAYOUT).getController();
        } catch (LoadException e) {
            e.printStackTrace();

            // Terminate application if the main view does not load
            Platform.exit();
            System.exit(0);
        }

        // Initialize scene using main view
        Scene scene = new Scene(Global.mainViewController.getRootNode());

        primaryStage.setScene(scene);
        primaryStage.setTitle(Global.APP_TITLE);
        primaryStage.setResizable(false);

        // Display stage
        primaryStage.show();

        // Terminate application when main window closes
        primaryStage.setOnCloseRequest(event -> {
            Platform.exit();
            System.exit(0);
        });
    }

    /**
     * Loads all word lists from resources.
     */
    private void loadWordLists() {
        for (int counter = 0; counter < Global.NUMBER_OF_WORD_LISTS; counter++) {
            InputStream inputStream = Global.application.getClass().getResourceAsStream("words/words." + (counter + 1));

            String result = new BufferedReader(new InputStreamReader(inputStream)).lines().collect(Collectors.joining("\n"));
            String[] words = result.split("\n");

            Global.WORDS[counter] = new String[words.length];
            Global.WORDS[counter] = words;
        }
    }
}