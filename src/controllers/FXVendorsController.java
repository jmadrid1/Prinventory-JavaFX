package controllers;

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
import models.Vendor;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;

import static constants.Stages.*;

/**
 * This is the controller class
 * for the Vendor Dashboard view
 */

public class FXVendorsController implements Initializable{

    @FXML
    private javafx.scene.control.TextField companyField;
    @FXML
    private javafx.scene.control.TextField phoneField;
    @FXML
    private javafx.scene.control.TextField emailField;
    @FXML
    private TextField streetField;
    @FXML
    private TextField cityField;
    @FXML
    private ComboBox<String> statesCombo;
    @FXML
    private TextField zipField;
    @FXML
    private Text companyText;
    @FXML
    private Text phoneText;
    @FXML
    private Text emailText;
    @FXML
    private Text cityText;
    @FXML
    private Text streetText;
    @FXML
    private Text stateText;
    @FXML
    private Text zipText;
    @FXML
    private Button saveBtn;
    @FXML
    private Button deleteBtn;
    @FXML
    private Button editBtn;
    @FXML
    private Button createBtn;
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
    private TableColumn<Vendor, Integer> colID;
    @FXML
    private TableColumn<Vendor, String> colCompany;
    @FXML
    private TableColumn<Vendor, String> colPhone;
    @FXML
    private TableColumn<Vendor, String> colEmail;
    @FXML
    private TableColumn<Vendor, String> colStreet;
    @FXML
    private TableColumn<Vendor, String> colCity;
    @FXML
    private TableColumn<Vendor, String> colState;
    @FXML
    private TableColumn<Vendor, String> colZip;
    @FXML
    private TableView<Vendor> vendorTable;

    private ObservableList<Vendor> data;

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

        // Prevents anything but numerical characters to be inputted
        phoneField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!newValue.matches("\\d*")) {
                    phoneField.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });

        // Prevents anything but numerical characters to be inputted
        zipField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!newValue.matches("\\d*")) {
                    zipField.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });

        // Array for populating the States Spinner
        ObservableList states =FXCollections.observableArrayList("AK", "AL", "AR", "AS", "AZ","CA", "CO","CT", "DC", "DE", "FL",
                "GA", "GU", "HI", "IA", "ID", "IL", "IN", "KS", "KY", "LA", "MA",
                "MD", "ME", "MI", "MN", "MO", "MS", "MT", "NC", "ND", "NE", "NH",
                "NJ", "NM", "NV", "NY", "OH", "OK", "OR", "PA", "PR", "RI", "SC",
                "SD", "TN", "TX", "UT", "VA", "VI", "VT", "WA", "WI", "WV", "WY");

        statesCombo.setItems(states);
        statesCombo.getSelectionModel().select(0);

        checkTextFields();

        try {
            getVendorData();
        } catch (SQLException e) {
            System.err.println("Encountered error while collecting data from MySQL for tableview: " + e.getErrorCode() + ": " + e.getCause());
        }
        vendorTable.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends Vendor> observable,
                                                                            Vendor oldValue,
                                                                            Vendor newValue) -> {
            if (newValue == null) {
                editID = 0;
                companyField.setText("");
                phoneField.setText("");
                emailField.setText("");
                streetField.setText("");
                cityField.setText("");
                statesCombo.getSelectionModel().select(0);
                zipField.setText("");

                return;
            }

            editID = newValue.getId();
            companyField.setText(newValue.getCompany());
            phoneField.setText(newValue.getPhone());
            emailField.setText(newValue.getEmail());
            streetField.setText(newValue.getStreet());
            cityField.setText(String.valueOf(newValue.getCity()));
            statesCombo.getSelectionModel().select(newValue.getState());
            zipField.setText(newValue.getZip());

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
    private void getVendorData() throws SQLException {

        try {

            con = db.getConnection();
            data = FXCollections.observableArrayList();

            String query = "SELECT * FROM vendors;";
            rs = con.createStatement().executeQuery(query);

            while (rs.next()) {
                int id = rs.getInt(1);
                String company = rs.getString(2);
                String phone = rs.getString(3);
                String email = rs.getString(4);
                String street = rs.getString(5);
                String city = rs.getString(6);
                String state = rs.getString(7);
                String zipcode = rs.getString(8);

                data.add(new Vendor(id, company, phone, email, street, city, state, zipcode));
            }
        } catch (SQLException e) {
            System.err.println("Encountered error while fetching data from MySQL: " + e.getErrorCode() + ": " + e.getCause());
        }

        colID.setCellValueFactory(new PropertyValueFactory<>("id"));
        colCompany.setCellValueFactory(new PropertyValueFactory<>("company"));
        colPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colStreet.setCellValueFactory(new PropertyValueFactory<>("street"));
        colCity.setCellValueFactory(new PropertyValueFactory<>("city"));
        colState.setCellValueFactory(new PropertyValueFactory<>("state"));
        colZip.setCellValueFactory(new PropertyValueFactory<>("zip"));
        vendorTable.setItems(null);
        vendorTable.setItems(data);
    }

    /**
     * Adds a vendor to the database
     * @throws SQLException
     */
    @FXML
    private void addVendor() throws SQLException {

        try {

            String sql = "INSERT INTO vendors (Company, " +
                                                "Phone, " +
                                                "Email, " +
                                                "Street, " +
                                                "City, " +
                                                "State, " +
                                                "Zipcode) " +
                                                    "VALUES(?, ?, ?, ?, ?, ?, ?);";

            con = db.getConnection();
            pstate = con.prepareStatement(sql);

            pstate.setString(1, companyField.getText());
            pstate.setString(2, phoneField.getText());
            pstate.setString(3, emailField.getText());
            pstate.setString(4, streetField.getText());
            pstate.setString(5, cityField.getText());
            pstate.setString(6, statesCombo.getSelectionModel().getSelectedItem());
            pstate.setString(7, zipField.getText());

            pstate.executeUpdate();
            con.commit();

            getVendorData();
            revertToDefault();
            pstate.close();
            con.close();

        } catch (SQLException e){
            System.err.println("Encountered error while adding new data to MySQL: " + e.getErrorCode() + ": " + e.getCause());
        }
    }

    /**
     * Edits the selected vendor from the tableview
     * @throws SQLException
     */
    @FXML
    private void editVendor() throws SQLException {

        try{

            String sql = "UPDATE vendors SET Company=?, " +
                                            "Phone=?, " +
                                            "Email=?, " +
                                            "Street=?, " +
                                            "City=?, " +
                                            "State=?, " +
                                            "Zipcode=? " +
                                                "WHERE ID=?";

            con = db.getConnection();
            pstate = con.prepareStatement(sql);

            pstate.setString(1, companyField.getText());
            pstate.setString(2, phoneField.getText());
            pstate.setString(3, emailField.getText());
            pstate.setString(4, streetField.getText());
            pstate.setString(5, cityField.getText());
            pstate.setString(6, statesCombo.getSelectionModel().getSelectedItem());
            pstate.setString(7, zipField.getText());
            pstate.setInt(8, editID);

            pstate.executeUpdate();
            con.commit();

            getVendorData();
            revertToDefault();
            pstate.close();
            con.close();

        } catch (SQLException e){
            System.err.println("Encountered error while committing changes to existent data from MySQL: " + e.getErrorCode() + ": " + e.getCause());
        }
    }

    /**
     * Removes the selected vendor from the tableview
     * @param id
     * @throws SQLException
     */
    private void deleteVendor(int id) throws SQLException {

        try{

            String sql = "DELETE FROM vendors WHERE ID=?;";

            con = db.getConnection();

            pstate = con.prepareStatement(sql);
            pstate.setInt(1, id);

            pstate.executeUpdate();
            con.commit();

            getVendorData();
            revertToDefault();
            pstate.close();
            con.close();

        } catch (SQLException e){
            System.err.println("Encountered error while deleting data from MySQL: " + e.getErrorCode() + ": " + e.getCause());
        }
    }

    /**
     * Refreshes data on our tableview and also sets
     * buttons back to their default states
     * @throws SQLException
     */
    @FXML
    private void refresh() throws SQLException {

        getVendorData();
        revertToDefault();
    }

    /**
     * Clears any texts on the textfields and resets spinners
     */
    @FXML
    private void clearFields() {

        companyField.clear();
        phoneField.clear();
        emailField.clear();
        streetField.clear();
        cityField.clear();
        statesCombo.getSelectionModel().selectFirst();
        zipField.clear();
    }

    /**
     * Dialog that appears to confirm deletion of
     * selected vendor from tableview
     */
    @FXML
    private void showConfirmationDialog() {

        int id = editID;

        ButtonType yes = new ButtonType("Yes", ButtonBar.ButtonData.YES);
        ButtonType no = new ButtonType("No", ButtonBar.ButtonData.NO);

        Alert dialog = new Alert(Alert.AlertType.WARNING,
                "Are you sure you want to delete the selected vendor?",
                yes, no);
        dialog.setTitle("Delete Vendor");

        Optional<ButtonType> selection = dialog.showAndWait();

        if(selection.isPresent() && (selection.get() == (yes))){

            try {
                deleteVendor(id);
            }catch (SQLException e){
                System.err.println("Encountered error while deleting data from MySQL: " + e.getErrorCode() + ": " + e.getCause());
            }
        }else {
            dialog.close();
        }
    }

    /**
     * Hides buttons unrelated to updating a vendor.
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
     * state.
     * @throws SQLException
     */
    @FXML
    private void revertToDefault() throws SQLException {

        clearFields();
        getVendorData();
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

        BooleanBinding companyFieldValid = Bindings.createBooleanBinding(() -> {
            if(companyField.getText().length() != 0){
                return true;
            }
            return false;
        }, companyField.textProperty());

        BooleanBinding phoneFieldValid = Bindings.createBooleanBinding(() -> {
            if(phoneField.getText().length() != 0){
                return true;
            }
            return false;
        }, phoneField.textProperty());

        BooleanBinding emailFieldValid = Bindings.createBooleanBinding(() -> {
            if(emailField.getText().length() != 0){
                return true;
            }
            return false;
        }, emailField.textProperty());

        BooleanBinding streetFieldValid = Bindings.createBooleanBinding(() -> {
            if(streetField.getText().length() != 0){
                return true;
            }
            return false;
        }, streetField.textProperty());

        BooleanBinding cityFieldValid = Bindings.createBooleanBinding(() -> {
            if(cityField.getText().length() != 0){
                return true;
            }
            return false;
        }, cityField.textProperty());

        BooleanBinding zipFieldValid = Bindings.createBooleanBinding(() -> {
            if(zipField.getText().length() != 0){
                return true;
            }
            return false;
        }, zipField.textProperty());

        saveBtn.disableProperty().bind(companyFieldValid.not()
                .or(phoneFieldValid.not())
                .or(emailFieldValid.not())
                .or(streetFieldValid.not())
                .or(cityFieldValid.not())
                .or(zipFieldValid.not()));

        editSaveBtn.disableProperty().bind(companyFieldValid.not()
                .or(phoneFieldValid.not())
                .or(emailFieldValid.not())
                .or(streetFieldValid.not())
                .or(cityFieldValid.not())
                .or(zipFieldValid.not()));
    }

    /**
     * Navigates to the Printer Dashboard stage
     */
    @FXML
    private void switchToPrinterDash() {

        Navigation nav = new Navigation();

        try {
            Stage currentStage = (Stage) saveBtn.getScene().getWindow();
            nav.newStage(PRINTERS_STAGE, "Prinventory - Printer Dashboard", true);
            currentStage.hide();
        } catch (Exception e) {
            System.err.println("Critical failure occurred when switching over to Vendor Dashboard: " + e.getMessage());
        }
    }

    /**
     * Navigates to the Toner Dashboard stage
     */
    @FXML
    private void switchToTonerDash() {

        Navigation nav = new Navigation();

        try {
            Stage currentStage = (Stage) saveBtn.getScene().getWindow();
            nav.newStage(TONERS_STAGE, "Prinventory - Toner Dashboard", true);
            currentStage.hide();
        } catch (Exception e) {
            System.err.println("Critical failure occurred when switching over to Toner Dashboard: " + e.getMessage());
        }
    }
}
