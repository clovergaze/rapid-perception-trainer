package org.infokin;

import javafx.application.Application;
import javafx.stage.Stage;
import org.infokin.controller.MainViewController;

/**
 * Container class for global constants and variables.
 */
public class Global {

	/*------------------
	| Global constants |
	------------------*/

    /**
     * Application title
     */
    public static final String APP_TITLE = "Rapid Perception Trainer";

    /**
     * Main view resource
     */
    public static final String MAIN_VIEW_LAYOUT = "view/MainView.fxml";

    /**
     * Number of word lists.
     */
    public static final int NUMBER_OF_WORD_LISTS = 4;

    /**
     * Array with lists of words.
     */
    public static final String[][] WORDS = new String[NUMBER_OF_WORD_LISTS][];

    /*------------------
    | Global variables |
    ------------------*/

    /**
     * The application instance.
     */
    public static Application application = null;

    /**
     * The main window.
     */
    public static Stage primaryStage = null;

    /**
     * The main view controller with navigation elements.
     */
    public static MainViewController mainViewController = null;
}
