package org.infokin.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.AnchorPane;
import org.infokin.Global;
import org.infokin.controller.api.Controller;

/**
 * Controller for About dialog
 */
public class AboutDialogViewController extends Controller {

    /*------------------
    | Member variables |
    ------------------*/

    private final String homepageURL = "http://www.infokin.org/";

    private final String githubURL = "https://github.com/clovergaze/rapid-perception-trainer";

    /*---------------------------
    | User interface components |
    ---------------------------*/

    /**
     * The root node of this view.
     */
    @FXML
    private AnchorPane rootNode;

    @FXML
    private Button okButton;

    @FXML
    private Hyperlink homepageHyperlink;

    @FXML
    private Hyperlink githubHyperlink;

    /*--------------------
    | Life cycle methods |
    --------------------*/

    /**
     * Initializes the controller class. This method is automatically called
     * after the FXML file has been loaded.
     */
    @FXML
    private void initialize() {
        homepageHyperlink.setTooltip(new Tooltip(homepageURL));
        githubHyperlink.setTooltip(new Tooltip(githubURL));

        Platform.runLater(() -> okButton.requestFocus());
    }

    /*-------------------------
    | User interface handlers |
    -------------------------*/

    @FXML
    private void handleHomepageHyperlink() {
        Global.application.getHostServices().showDocument(homepageURL);
    }

    @FXML
    private void handleGitHubHyperlink() {
        Global.application.getHostServices().showDocument(githubURL);
    }

    @FXML
    private void handleOkButtonClick() {
    }

    /*---------------------
    | Getters and Setters |
    ---------------------*/

    /**
     * {@inheritDoc}
     */
    @Override
    public AnchorPane getRootNode() {
        return rootNode;
    }

    public Button getOkButton() {
        return okButton;
    }
}
