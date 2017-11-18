package controllers;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

import static constants.Stages.*;

/**
 * This is the controller class
 * for the Main Dashboard view
 */

public class FXMainDashController implements Initializable {

    @FXML
    private ImageView closeWindowBtn;
    @FXML
    private ImageView minimizeWindowBtn;
    @FXML
    private ImageView printerDashBtn;
    @FXML
    private ImageView tonerDashBtn;
    @FXML
    private ImageView vendorDashBtn;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        minimizeWindowBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {

                Stage stage = (Stage)((ImageView)event.getSource()).getScene().getWindow();
                stage.setIconified(true);
            }
        });

        closeWindowBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                Platform.exit();
            }
        });
    }

    @FXML
    private void switchToPrinterDash() {

        Navigation nav = new Navigation();

        try {
            Stage currentStage = (Stage) printerDashBtn.getScene().getWindow();
            nav.newStage(PRINTERS_STAGE, "Prinventory - Printer Dashboard", true);
            currentStage.hide();
        } catch (Exception e) {
            System.err.println("Critical failure occurred when switching over to Printer Dashboard: " + e.getMessage());
        }
    }

    @FXML
    private void switchToTonerDash() {

        Navigation nav = new Navigation();

        try {
            Stage currentStage = (Stage) printerDashBtn.getScene().getWindow();
            nav.newStage(TONERS_STAGE, "Prinventory - Toner Dashboard", true);
            currentStage.hide();
        } catch (Exception e) {
            System.err.println("Critical failure occurred when switching over to Toner Dashboard: " + e.getMessage());
        }
    }

    /**
     * Navigates to the Vendor Dashboard stage
     */
    @FXML
    private void switchToVendorDash() {

        Navigation nav = new Navigation();
        try {
            Stage currentStage = (Stage) printerDashBtn.getScene().getWindow();
            nav.newStage(VENDORS_STAGE, "Prinventory - Vendor Dashboard", true);
            currentStage.hide();
        } catch (Exception e) {
            System.err.println("Critical failure occurred when switching over to Vendor Dashboard: " + e.getMessage());
        }
    }

}
