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
import models.Printer;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * This is the controller class
 * for the Printer Dashboard view
 */

public class FXPrintersController implements Initializable {

    @FXML
    private Button deleteBtn;
    @FXML
    private Button editBtn;
    @FXML
    private Button editSaveBtn;
    @FXML
    private Button cancelBtn;
    @FXML
    private Button createBtn;
    @FXML
    private Button saveBtn;
    @FXML
    private Button refreshBtn;
    @FXML
    private TextField makeField;
    @FXML
    private TextField modelField;
    @FXML
    private TextField serialField;
    @FXML
    private ComboBox<String> activeCombo;
    @FXML
    private ComboBox<String> colorCombo;
    @FXML
    private TextField ownerField;
    @FXML
    private TextField deptField;
    @FXML
    private TextField locationField;
    @FXML
    private TextField floorField;
    @FXML
    private TextField ipField;
    @FXML
    private Text makeText;
    @FXML
    private Text modelText;
    @FXML
    private Text serialText;
    @FXML
    private Text statusText;
    @FXML
    private Text colorText;
    @FXML
    private Text ownerText;
    @FXML
    private Text deptText;
    @FXML
    private Text locationText;
    @FXML
    private Text floorText;
    @FXML
    private Text ipText;
    @FXML
    private ImageView closeWindowBtn;
    @FXML
    private ImageView minimizeWindowBtn;
    @FXML
    private TableColumn<Printer, Integer> colID;
    @FXML
    private TableColumn<Printer, String> colMake;
    @FXML
    private TableColumn<Printer, String> colModel;
    @FXML
    private TableColumn<Printer, String> colSerial;
    @FXML
    private TableColumn<Printer, String> colStatus;
    @FXML
    private TableColumn<Printer, String> colColor;
    @FXML
    private TableColumn<Printer, String> colOwner;
    @FXML
    private TableColumn<Printer, String> colDept;
    @FXML
    private TableColumn<Printer, String> colLocation;
    @FXML
    private TableColumn<Printer, String> colFloor;
    @FXML
    private TableColumn<Printer, Integer> colIp;
    @FXML
    private TableView<Printer> printTable;

    private ObservableList<Printer> data;

    static int editID;

    private Connection con;
    private Database db;
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
        serialField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (newValue != null){
                        serialField.setText(newValue.toUpperCase());
                }
            }
        });

        // Spinners for changing Status state and Color usage
        ObservableList<String> status = FXCollections.observableArrayList("Inactive", "Active");
        activeCombo.setItems(status);
        activeCombo.getSelectionModel().select(0);

        ObservableList<String> color = FXCollections.observableArrayList("B/W", "Color");
        colorCombo.setItems(color);
        colorCombo.getSelectionModel().select(0);

        // Prevents anything but numerical characters to be inputted
        ipField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (newValue != null && !newValue.matches("\\d*")) {
                        ipField.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });

        checkTextFields();

        try {
            getPrinterData();
        } catch (SQLException e) {
            System.err.println("Encountered error while collecting data from MySQL for tableview: " + e.getErrorCode() + ": " + e.getCause());
        }

        printTable.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends Printer> observable,
                                                                           Printer oldValue,
                                                                           Printer newValue) -> {
            if (newValue == null) {
                editID = 0;
                makeField.setText("");
                modelField.setText("");
                serialField.setText("");
                activeCombo.getSelectionModel().selectFirst();
                colorCombo.getSelectionModel().selectFirst();
                ownerField.setText("");
                deptField.setText("");
                locationField.setText("");
                floorField.setText("");
                ipField.setText("");

                return;
            }
            editID = newValue.getId();
            makeField.setText(newValue.getMake());
            modelField.setText(newValue.getModel());
            serialField.setText(newValue.getSerial());
            activeCombo.getSelectionModel().select(newValue.getStatus());
            colorCombo.getSelectionModel().select(newValue.getColor());
            ownerField.setText(newValue.getOwner());
            deptField.setText(newValue.getDepartment());
            locationField.setText(newValue.getLocation());
            floorField.setText(newValue.getFloor());
            ipField.setText(String.valueOf(newValue.getIp()));

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
    private void getPrinterData() throws SQLException {

        try {

            con = db.getConnection();
            data = FXCollections.observableArrayList();

            String sql = "SELECT * FROM printers";
            rs = con.createStatement().executeQuery(sql);

            while (rs.next()) {
                int id = rs.getInt(1);
                String make = rs.getString(2);
                String model = rs.getString(3);
                String serial = rs.getString(4).toUpperCase();
                String status = rs.getString(5);
                String color = rs.getString(6);
                String ownership = rs.getString(7);
                String department = rs.getString(8);
                String location = rs.getString(9);
                String floor = rs.getString(10);
                int ip = rs.getInt(11);

                data.add(new Printer(id, make, model, serial, status, color, ownership, department, location, floor, ip));
            }

        } catch (SQLException e) {
            System.err.println("Encountered error while fetching data from MySQL: " + e.getErrorCode() + ": " + e.getCause());
        }
        colID.setCellValueFactory(new PropertyValueFactory<>("id"));
        colMake.setCellValueFactory(new PropertyValueFactory<>("make"));
        colModel.setCellValueFactory(new PropertyValueFactory<>("model"));
        colSerial.setCellValueFactory(new PropertyValueFactory<>("serial"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colColor.setCellValueFactory(new PropertyValueFactory<>("color"));
        colOwner.setCellValueFactory(new PropertyValueFactory<>("owner"));
        colDept.setCellValueFactory(new PropertyValueFactory<>("department"));
        colLocation.setCellValueFactory(new PropertyValueFactory<>("location"));
        colFloor.setCellValueFactory(new PropertyValueFactory<>("floor"));
        colIp.setCellValueFactory(new PropertyValueFactory<>("ip"));

        printTable.setItems(null);
        printTable.setItems(data);
    }

    /**
     * Adds a printer to the database
     * @throws SQLException
     */
    @FXML
    private void addPrinter() throws SQLException {

        try {

            String sql = "INSERT INTO printers (Make, " +
                                                "Model, " +
                                                "Serial, " +
                                                "Status, " +
                                                "Color, " +
                                                "Ownership, " +
                                                "Department, " +
                                                "Location, " +
                                                "Floor, " +
                                                "IP) " +

                                                    "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";

            con = db.getConnection();
            pstate = con.prepareStatement(sql);

            pstate.setString(1, makeField.getText());
            pstate.setString(2, modelField.getText());
            pstate.setString(3, serialField.getText());
            pstate.setString(4, activeCombo.getSelectionModel().getSelectedItem());
            pstate.setString(5, colorCombo.getSelectionModel().getSelectedItem());
            pstate.setString(6, ownerField.getText());
            pstate.setString(7, deptField.getText());
            pstate.setString(8, locationField.getText());
            pstate.setString(9, floorField.getText());
            pstate.setInt(10, Integer.parseInt(ipField.getText()));


            pstate.executeUpdate();
            con.commit();

            getPrinterData();
            revertToDefault();
            pstate.close();
            con.close();

        } catch (SQLException e){
            System.err.println("Encountered error while adding new data to MySQL: " + e.getErrorCode() + ": " + e.getCause());
        }
    }

    /**
     * Edits the selected printer from the tableview
     * @throws SQLException
     */
    @FXML
    private void editPrinter() throws SQLException {

        try{

            String sql = "UPDATE printers SET MAKE=?, " +
                                            "MODEL=?, " +
                                            "SERIAL=?, " +
                                            "Status=?, " +
                                            "Color=?, " +
                                            "OWNERSHIP=?, " +
                                            "DEPARTMENT=?, " +
                                            "LOCATION=?, " +
                                            "Floor=?, " +
                                            "IP=? " +

                                                "WHERE ID=?;";

            con = db.getConnection();
            pstate = con.prepareStatement(sql);

            pstate.setString(1, makeField.getText());
            pstate.setString(2, modelField.getText());
            pstate.setString(3, serialField.getText());
            pstate.setString(4, activeCombo.getSelectionModel().getSelectedItem());
            pstate.setString(5, colorCombo.getSelectionModel().getSelectedItem());
            pstate.setString(6, ownerField.getText());
            pstate.setString(7, deptField.getText());
            pstate.setString(8, locationField.getText());
            pstate.setString(9, floorField.getText());
            pstate.setInt(10, Integer.parseInt(ipField.getText()));

            pstate.setInt(11, editID);

            pstate.executeUpdate();
            con.commit();

            getPrinterData();
            revertToDefault();
            pstate.close();
            con.close();

        } catch (SQLException e){
            System.err.println("Encountered error while committing changes to existent data from MySQL: " + e.getErrorCode() + ": " + e.getCause());
        }
    }

    /**
     * Removes the selected printer from the tableview
     * @param id
     * @throws SQLException
     */
    private void deletePrinter(int id) throws SQLException {

        try{

            String sql = "DELETE FROM printers WHERE ID=?;";

            con = db.getConnection();

            pstate = con.prepareStatement(sql);
            pstate.setInt(1, id);

            pstate.executeUpdate();
            con.commit();

            getPrinterData();
            revertToDefault();
            pstate.close();
            con.close();

        } catch (SQLException e){
            System.err.println("Encountered error while deleting data from MySQL: " + e.getErrorCode() + ": " + e.getCause());
        }
    }

    /**
     * Clears any texts on the textfields and resets spinners
     */
    @FXML
    private void clearFields(){

        makeField.clear();
        modelField.clear();
        serialField.clear();
        activeCombo.getSelectionModel().selectFirst();
        colorCombo.getSelectionModel().selectFirst();
        ownerField.clear();
        deptField.clear();
        locationField.clear();
        floorField.clear();
        ipField.clear();
    }

    /**
     * Refreshes data on our tableview and also hide/shows
     * buttons back to default state
     * @throws SQLException
     */
    @FXML
    private void refresh() throws SQLException {

        getPrinterData();
        revertToDefault();
    }

    /**
     * Dialog that appears to confirm deletion of
     * selected printer from tableview
     */
    @FXML
    private void showConfirmationDialog(){

        int id = editID;

        ButtonType yes = new ButtonType("Yes", ButtonBar.ButtonData.YES);
        ButtonType no = new ButtonType("No", ButtonBar.ButtonData.NO);

        Alert dialog = new Alert(Alert.AlertType.WARNING,
                "Are you sure you want to delete the selected printer?",
                yes, no);
        dialog.setTitle("Delete Printer");

        Optional<ButtonType> selection = dialog.showAndWait();

        if(selection.isPresent() && (selection.get() == (yes))){
            try {
                deletePrinter(id);
            }catch (SQLException e){
                System.err.println("Encountered error while deleting data from MySQL: " + e.getErrorCode() + ": " + e.getCause());
            }
        }else{
            dialog.close();
        }
    }

    /**
     * Hides buttons unrelated to updating a printer.
     */
    @FXML
    private void editTask() {

        deleteBtn.setVisible(false);
        editBtn.setVisible(false);
        createBtn.setVisible(false);
        editSaveBtn.setVisible(true);
        cancelBtn.setVisible(true);
    }

    /**
     * Clears textfields and spinners while also
     * returning buttons to their needed visibility
     * state
     * @throws SQLException
     */
    @FXML
    private void revertToDefault() throws SQLException {

        clearFields();
        getPrinterData();
        saveBtn.setVisible(true);
        cancelBtn.setVisible(false);
        createBtn.setVisible(false);
        editSaveBtn.setVisible(false);
        editBtn.setVisible(false);
        deleteBtn.setVisible(false);
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

        BooleanBinding serialFieldValid = Bindings.createBooleanBinding(() -> {
            if(serialField.getText().length() != 0){
                return true;
            }
            return false;
        }, serialField.textProperty());

        BooleanBinding ownerFieldValid = Bindings.createBooleanBinding(() -> {
            if(ownerField.getText().length() != 0){
                return true;
            }
            return false;
        }, ownerField.textProperty());

        BooleanBinding deptFieldValid = Bindings.createBooleanBinding(() -> {
            if(deptField.getText().length() != 0){
                return true;
            }
            return false;
        }, deptField.textProperty());

        BooleanBinding locationFieldValid = Bindings.createBooleanBinding(() -> {
            if(locationField.getText().length() != 0){
                return true;
            }
            return false;
        }, locationField.textProperty());

        BooleanBinding floorFieldValid = Bindings.createBooleanBinding(() -> {
            if(floorField.getText().length() != 0){
                return true;
            }
            return false;
        }, floorField.textProperty());

        BooleanBinding ipFieldValid = Bindings.createBooleanBinding(() -> {
            if(ipField.getText().length() != 0){
                return true;
            }
            return false;
        }, ipField.textProperty());

        saveBtn.disableProperty().bind(makeFieldValid.not()
                .or(mode1FieldValid.not())
                .or(serialFieldValid.not())
                .or(ownerFieldValid.not())
                .or(deptFieldValid.not())
                .or(locationFieldValid.not())
                .or(floorFieldValid.not())
                .or(ipFieldValid.not()));

        editSaveBtn.disableProperty().bind(makeFieldValid.not()
                .or(mode1FieldValid.not())
                .or(serialFieldValid.not())
                .or(ownerFieldValid.not())
                .or(deptFieldValid.not())
                .or(locationFieldValid.not())
                .or(floorFieldValid.not())
                .or(ipFieldValid.not()));
    }

    /**
     * Navigates to the Toner Dashboard stage
     */
    @FXML
    private void switchToTonerDash() {

        Navigation nav = new Navigation();

        try {
            Stage currentStage = (Stage) saveBtn.getScene().getWindow();
            nav.newStage(Stages.TONERS_STAGE, "Prinventory - Toner Dashboard", true);
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
            Stage currentStage = (Stage) saveBtn.getScene().getWindow();
            nav.newStage(Stages.VENDORS_STAGE, "Prinventory - Vendor Dashboard", true);
            currentStage.hide();
        } catch (Exception e) {
            System.err.println("Critical failure occurred when switching over to Vendor Dashboard: " + e.getMessage());
        }
    }

}
