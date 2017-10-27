package controllers;

import constants.Stages;
import database.Database;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import models.Toner;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * This is the controller class that
 * for the Toner Dashboard view
 */

public class FXTonersController implements Initializable {

    @FXML
    private javafx.scene.control.TextField makeField;
    @FXML
    private javafx.scene.control.TextField modelField;
    @FXML
    private javafx.scene.control.TextField tmodelField;
    @FXML
    private ComboBox<String> colorCombo;
    @FXML
    private javafx.scene.control.TextField blackField;
    @FXML
    private Spinner<Integer> blackSpinner;
    @FXML
    private Spinner<Integer> cyanSpinner;
    @FXML
    private Spinner<Integer> yellowSpinner;
    @FXML
    private Spinner<Integer> magentaSpinner;
    @FXML
    private Text makeText;
    @FXML
    private Text modelText;
    @FXML
    private Text tmodelText;
    @FXML
    private Text blackText;
    @FXML
    private Text cyanText;
    @FXML
    private Text yellowText;
    @FXML
    private Text magentaText;
    @FXML
    private Button saveBtn;
    @FXML
    private Button createBtn;
    @FXML
    private Button deleteBtn;
    @FXML
    private Button editBtn;
    @FXML
    private Button editSaveBtn;
    @FXML
    private Button cancelBtn;
    @FXML
    private Button refreshBtn;
    @FXML
    private ImageView closeWindowBtn;
    @FXML
    private ImageView minimizeWindowBtn;
    @FXML
    private TableColumn<Toner, Integer> colID;
    @FXML
    private TableColumn<Toner, String> colMake;
    @FXML
    private TableColumn<Toner, String> colModel;
    @FXML
    private TableColumn<Toner, String> colTModel;
    @FXML
    private TableColumn<Toner, String> colColor;
    @FXML
    private TableColumn<Toner, Integer> colBlack;
    @FXML
    private TableColumn<Toner, Integer> colCyan;
    @FXML
    private TableColumn<Toner, Integer> colYellow;
    @FXML
    private TableColumn<Toner, Integer> colMagenta;
    @FXML
    private TableView<Toner> tonerTable;
    @FXML
    private MenuItem dash_Printer;
    @FXML
    private MenuItem dash_Vendor;

    private ObservableList<Toner> data;

    private int editID;
    private Database db;
    private Connection con;
    private PreparedStatement pstate;
    private ResultSet rs;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        db = new Database();

        // Exits application upon being clicked
        closeWindowBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {

                Platform.exit();
            }
        });

        // Minimizes window upon being clicked
        minimizeWindowBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {

                Stage stage = (Stage)((ImageView)event.getSource()).getScene().getWindow();
                stage.setIconified(true);
            }
        });

        // Makes every character typed uppercase
        tmodelField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (newValue != null){
                        tmodelField.setText(newValue.toUpperCase());
                }
            }
        });

        // Spinners for changing Color usage
        ObservableList color =FXCollections.observableArrayList("B/W", "Color");
        colorCombo.setItems(color);
        colorCombo.getSelectionModel().selectFirst();

        // Spinners for incrementing or decrementing toner quantity
        SpinnerValueFactory<Integer> bSvf = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 99);
        blackSpinner.setValueFactory(bSvf);

        SpinnerValueFactory<Integer> cSvf = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 99);
        cyanSpinner.setValueFactory(cSvf);

        SpinnerValueFactory<Integer> ySvf = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 99);
        yellowSpinner.setValueFactory(ySvf);

        SpinnerValueFactory<Integer> mSvf = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 99);
        magentaSpinner.setValueFactory(mSvf);

        checkTextFields();

        try {
            getTonerData();
        } catch (SQLException e) {
            System.err.println("Encountered error while collecting data from MySQL for tableview: " + e.getErrorCode() + ": " + e.getCause());
        }
        tonerTable.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends Toner> observable,
                                                                           Toner oldValue,
                                                                           Toner newValue) -> {
            if (newValue == null) {
                editID = 0;
                makeField.setText("");
                modelField.setText("");
                tmodelField.setText("");
                colorCombo.getSelectionModel().select(0);
                blackSpinner.getValueFactory().setValue(0);
                cyanSpinner.getValueFactory().setValue(0);
                yellowSpinner.getValueFactory().setValue(0);
                magentaSpinner.getValueFactory().setValue(0);

                return;
            }
            editID = newValue.getId();
            makeField.setText(newValue.getMake());
            modelField.setText(newValue.getModel());
            tmodelField.setText(newValue.getTModel());

            /*
              Hides colored toner spinners if Combobox
              selection is "Black" otherwise colored
              toners are viewable and editable
             */
            colorCombo.getSelectionModel().select(newValue.getColor());
            String comboSelection = colorCombo.getSelectionModel().getSelectedItem();

            if(comboSelection == "Color"){
                cyanText.setVisible(true);
                cyanSpinner.setVisible(true);
                yellowText.setVisible(true);
                yellowSpinner.setVisible(true);
                magentaText.setVisible(true);
                magentaSpinner.setVisible(true);
            }else{
                cyanText.setVisible(false);
                cyanSpinner.setVisible(false);
                yellowText.setVisible(false);
                yellowSpinner.setVisible(false);
                magentaText.setVisible(false);
                magentaSpinner.setVisible(false);
            }

            blackSpinner.getValueFactory().setValue(newValue.getBlack());
            cyanSpinner.getValueFactory().setValue(newValue.getCyan());
            yellowSpinner.getValueFactory().setValue(newValue.getYellow());
            magentaSpinner.getValueFactory().setValue(newValue.getMagenta());

            saveBtn.setVisible(false);
            editBtn.setVisible(true);
            cancelBtn.setVisible(false);
            editSaveBtn.setVisible(false);
            deleteBtn.setVisible(true);
            createBtn.setVisible(true);

        });
    }

    /**
     * Collects data from SQL to populate TableView
     * @throws SQLException
     */
    private void getTonerData() throws SQLException {

        try {
            con = db.getConnection();
            data = FXCollections.observableArrayList();
            String query = "SELECT * FROM toners;";

            rs = con.createStatement().executeQuery(query);

            while (rs.next()) {
                int id = rs.getInt(1);
                String make = rs.getString(2);
                String model = rs.getString(3);
                String tmodel = rs.getString(4).toUpperCase();
                String color = rs.getString(5);
                int black = rs.getInt(6);
                int cyan = rs.getInt(7);
                int yellow = rs.getInt(8);
                int magenta = rs.getInt(9);

                data.add(new Toner(id, make, model, tmodel, color, black, cyan, yellow, magenta));
            }
        } catch (SQLException e) {
            System.err.println("Encountered error fetching data from MySQL: " + e.getErrorCode() + ": " + e.getCause());
        }

        colID.setCellValueFactory(new PropertyValueFactory<>("id"));
        colMake.setCellValueFactory(new PropertyValueFactory<>("make"));
        colModel.setCellValueFactory(new PropertyValueFactory<>("model"));
        colTModel.setCellValueFactory(new PropertyValueFactory<>("tModel"));
        colColor.setCellValueFactory(new PropertyValueFactory<>("color"));
        colBlack.setCellValueFactory(new PropertyValueFactory<>("black"));
        colCyan.setCellValueFactory(new PropertyValueFactory<>("cyan"));
        colYellow.setCellValueFactory(new PropertyValueFactory<>("yellow"));
        colMagenta.setCellValueFactory(new PropertyValueFactory<>("magenta"));
        tonerTable.setItems(null);
        tonerTable.setItems(data);

    }

    /**
     * Adds a toner to the database
     * @throws SQLException
     */
    @FXML
    private void addToner() throws SQLException {

        try {

            String sql = "INSERT INTO toners (Make, " +
                                            "Model, " +
                                            "TModel, " +
                                            "Color, " +
                                            "Black, " +
                                            "Cyan, " +
                                            "Yellow, " +
                                            "Magenta) " +
                                                "VALUES(?, ?, ?, ?, ?, ?, ?, ?);";

            con = db.getConnection();
            pstate = con.prepareStatement(sql);

            pstate.setString(1, makeField.getText());
            pstate.setString(2, modelField.getText());
            pstate.setString(3, tmodelField.getText());
            pstate.setString(4, colorCombo.getSelectionModel().getSelectedItem());
            pstate.setInt(5, blackSpinner.getValueFactory().getValue());
            pstate.setInt(6, cyanSpinner.getValueFactory().getValue());
            pstate.setInt(7, yellowSpinner.getValueFactory().getValue());
            pstate.setInt(8, magentaSpinner.getValueFactory().getValue());

            pstate.executeUpdate();
            con.commit();

            getTonerData();
            revertToDefault();
            pstate.close();
            con.close();

        } catch (SQLException e){
            System.err.println("Encountered error while adding new data to MySQL: " + e.getErrorCode() + ": " + e.getCause());
        }
    }

    /**
     * Edits the selected toner from the tableview
     * @throws SQLException
     */
    @FXML
    private void editToner() throws SQLException {

        try{

            String sql = "UPDATE toners SET Make=?, " +
                                            "Model=?, " +
                                            "TModel=?, " +
                                            "Color=?, " +
                                            "Black=?, " +
                                            "Cyan=?, " +
                                            "Yellow=?, " +
                                            "Magenta=? " +
                                                "WHERE ID=?";

            con = db.getConnection();
            pstate = con.prepareStatement(sql);

            pstate.setString(1, makeField.getText());
            pstate.setString(2, modelField.getText());
            pstate.setString(3, tmodelField.getText());
            pstate.setString(4, colorCombo.getSelectionModel().getSelectedItem());
            pstate.setInt(5, blackSpinner.getValueFactory().getValue());
            pstate.setInt(6, cyanSpinner.getValueFactory().getValue());
            pstate.setInt(7, yellowSpinner.getValueFactory().getValue());
            pstate.setInt(8, magentaSpinner.getValueFactory().getValue());
            pstate.setInt(9, editID);

            pstate.executeUpdate();
            con.commit();

            getTonerData();
            revertToDefault();
            pstate.close();
            con.close();

        } catch (SQLException e){
            System.err.println("Encountered error while committing changes to existent data from MySQL: " + e.getErrorCode() + ": " + e.getCause());
        }
    }

    /**
     * Removes the selected toner from the tableview
     * @param id
     * @throws SQLException
     */
    private void deleteToner(int id) throws SQLException{

        try{

            String sql = "DELETE FROM toners WHERE ID=?;";

            con = db.getConnection();

            pstate = con.prepareStatement(sql);
            pstate.setInt(1, id );

            pstate.executeUpdate();
            con.commit();

            getTonerData();
            revertToDefault();
            pstate.close();
            con.close();

        } catch (SQLException e){
            System.err.println("Encountered error while removing data from MySQL: " + e.getErrorCode() + ": " + e.getCause());
        }
    }

    /**
     * Refreshes data on our tableview and also sets
     * buttons back to their default states
     * @throws SQLException
     */
    @FXML
    private void refresh() throws SQLException {
        getTonerData();
        revertToDefault();
    }

    /**
     * Clears any text on the textfields and
     * resets spinners values to 0
     */
    @FXML
    private void clearFields() {

        makeField.clear();
        modelField.clear();
        tmodelField.clear();
        colorCombo.getSelectionModel().select(0);
        blackSpinner.getValueFactory().setValue(0);
        cyanSpinner.getValueFactory().setValue(0);
        yellowSpinner.getValueFactory().setValue(0);
        magentaSpinner.getValueFactory().setValue(0);
    }

    /**
     * Dialog that appears to confirm deletion of
     * selected toner from tableview
     */
    @FXML
    private void showConfirmationDialog(){

        int id = editID;

        ButtonType yes = new ButtonType("Yes", ButtonBar.ButtonData.YES);
        ButtonType no = new ButtonType("No", ButtonBar.ButtonData.NO);

        Alert dialog = new Alert(Alert.AlertType.WARNING,
                "Are you sure you want to delete the selected toner?",
                yes, no);
        dialog.setTitle("Delete Toner");

        Optional<ButtonType> selection = dialog.showAndWait();

        if(selection.isPresent() && (selection.get() == (yes))){
            try {
                deleteToner(id);
            }catch (SQLException e){
                System.err.println("Encountered error displaying failure dialog: " + e.getErrorCode() + ": " + e.getCause());
            }
        }else {
            dialog.close();
        }
    }

    /**
     * Shows colored toner texts & spinners when
     * combobox selection is on "Color" otherwise
     * colored toners are hidden
     */
    @FXML
    private void hideSpinners(){

        if(colorCombo.getSelectionModel().getSelectedItem() == "Color") {
            cyanText.setVisible(true);
            cyanSpinner.setVisible(true);

            yellowText.setVisible(true);
            yellowSpinner.setVisible(true);

            magentaSpinner.setVisible(true);
            magentaText.setVisible(true);

        } else {
            cyanText.setVisible(false);
            cyanSpinner.setVisible(false);
            cyanSpinner.getValueFactory().setValue(0);

            yellowText.setVisible(false);
            yellowSpinner.setVisible(false);
            yellowSpinner.getValueFactory().setValue(0);

            magentaText.setVisible(false);
            magentaSpinner.setVisible(false);
            magentaSpinner.getValueFactory().setValue(0);
        }
    }

    /**
     * Hides buttons unrelated to updating a toner.
     */
    @FXML
    private void editTask() {

        deleteBtn.setVisible(false);
        editBtn.setVisible(false);
        createBtn.setVisible(false);
        editSaveBtn.setVisible(true);
        cancelBtn.setVisible(true);

        hideSpinners();
    }

    /**
     * Clears textfields and spinners while also
     * returning buttons and spinners to their default
     * visibility state.
     * @throws SQLException
     */
    @FXML
    private void revertToDefault() throws SQLException {

        clearFields();
        getTonerData();
        saveBtn.setVisible(true);
        cancelBtn.setVisible(false);
        createBtn.setVisible(false);
        editSaveBtn.setVisible(false);
        editBtn.setVisible(false);
        deleteBtn.setVisible(false);

        colorCombo.setVisible(true);
        blackText.setVisible(true);
        blackSpinner.setVisible(true);

        hideSpinners();
    }

    /**
     * Disables save button while textfields are
     * left unanswered/blank
     */
    private void checkTextFields() {

        BooleanBinding makeFieldValid = Bindings.createBooleanBinding(() -> {
            if(makeField.getText().length() != 0){
                return true;
            }
            return false;
        }, makeField.textProperty());

        BooleanBinding mode1FieldValid = Bindings.createBooleanBinding(() -> {
            if(modelField.getText().length() != 0){
                return true;
            }
            return false;
        }, modelField.textProperty());

        BooleanBinding tModelFieldValid = Bindings.createBooleanBinding(() -> {
            if(tmodelField.getText().length() != 0){
                return true;
            }
            return false;
        }, tmodelField.textProperty());

        saveBtn.disableProperty().bind(makeFieldValid.not()
                .or(mode1FieldValid.not())
                .or(tModelFieldValid.not()));

        editSaveBtn.disableProperty().bind(makeFieldValid.not()
                .or(mode1FieldValid.not())
                .or(tModelFieldValid.not()));
    }

    /**
     * Navigates to the Printer Dashboard stage
     */
    @FXML
    private void switchToPrinterDash() {

        Navigation nav = new Navigation();

        try {
            Stage currentStage = (Stage) saveBtn.getScene().getWindow();
            nav.newStage(Stages.PRINTERS_STAGE, "Prinventory - Printer Dashboard", true);
            currentStage.hide();
        } catch (Exception e) {
            System.err.println("Critical failure occurred when switching over to Printer Dashboard: " + e.getMessage());
        }
    }

    /**
     * Navigates to the Vendor Dashboard stage
     */
    @FXML
    private void switchToVendorDash() {

        Navigation nav = new Navigation();

        try {
            Stage currentStage = (Stage) saveBtn.getScene().getWindow();
            nav.newStage(Stages.VENDORS_STAGE, "Prinventory - Vendor Dashboard", true);
            currentStage.hide();
        } catch (Exception e) {
            System.err.println("Critical failure occurred when switching over to Vendor Dashboard: " + e.getMessage());
        }
    }

}