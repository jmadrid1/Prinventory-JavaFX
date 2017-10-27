package controllers;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

/**
 * Utilized to switch between dashboards
 */

public class Navigation {

    public Navigation() {
    }

    /**
     * Switches user between dashboards
     * @param fxml
     * @param title
     * @param resizable
     * @throws IOException
     */
    public void newStage(String fxml, String title, boolean resizable) throws IOException {

        Parent root = FXMLLoader.load(getClass().getResource(fxml));

        Scene scene = new Scene(root);
        Stage stage = new Stage();

        stage.initStyle(StageStyle.UNDECORATED);
        stage.setScene(scene);
        stage.setTitle(title);

        // Makes stage maximized without hiding the toolbar/taskbar
        javafx.geometry.Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        stage.setX(screenBounds.getMinX());
        stage.setY(screenBounds.getMinY());
        stage.setWidth(screenBounds.getWidth());
        stage.setHeight(screenBounds.getHeight());

        stage.show();
    }

}
