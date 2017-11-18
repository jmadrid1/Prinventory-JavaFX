package database;

import javafx.scene.control.Alert;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static constants.DBSchema.*;

/**
 * Database class that creates the database schema if
 * it does not exist along with methods for simplified
 * database tasks.
 */

public class Database {

    private Connection con;
    private PreparedStatement pstate;

    private static final String HOST = "jdbc:mysql://localhost:3306/print?&useSSL=false";
    private static final String USER = "admin";
    private static final String PASSWORD = "admin123";

    /**
     * Creates the database's schema
     * @throws SQLException
     */
    public void createDatabase() throws SQLException {

        try {

            String sql = "CREATE TABLE IF NOT EXISTS " + PRINTER_TABLE + " (" +
                                                            ID + ", " +
                                                            PRINTER_MAKE + ", " +
                                                            PRINTER_MODEL + ", " +
                                                            PRINTER_SERIAL + ", " +
                                                            PRINTER_STATUS + ", " +
                                                            PRINTER_COLOR + ", " +
                                                            PRINTER_OWNER + ", " +
                                                            PRINTER_DEPT + ", " +
                                                            PRINTER_LOCATION + ", " +
                                                            PRINTER_FLOOR + ", " +
                                                            PRINTER_IP + ");";

            updateSQL(sql);

            sql = "CREATE TABLE IF NOT EXISTS " + TONER_TABLE + " (" +
                                                    ID + ", " +
                                                    TONER_MAKE + ", " +
                                                    TONER_MODEL + ", " +
                                                    TONER_TMODEL + ", " +
                                                    TONER_COLOR + ", " +
                                                    TONER_BLACK + ", " +
                                                    TONER_CYAN + ", " +
                                                    TONER_YELLOW + ", " +
                                                    TONER_MAGENTA + ");";

            updateSQL(sql);

            sql = "CREATE TABLE IF NOT EXISTS " + VENDOR_TABLE + " (" +
                                                    ID  + ", " +
                                                    VENDOR_COMPANY + ", " +
                                                    VENDOR_PHONE + ", " +
                                                    VENDOR_EMAIL + ", " +
                                                    VENDOR_STREET + ", " +
                                                    VENDOR_CITY + ", " +
                                                    VENDOR_STATE + ", " +
                                                    VENDOR_ZIPCODE + ");";

            updateSQL(sql);

        } catch (SQLException e) {
            System.err.println("Encountered error while creating MySQL tables: " + e.getErrorCode() + ": " + e.getCause());
        }
    }

    /**
     * Makes obtaining a database connection simpler
     * along with creating the schema if it doesn't
     * already exist
     * @return
     */
    public Connection getConnection(){

        try {
            Class.forName("com.mysql.jdbc.Driver");
            con = DriverManager.getConnection(HOST, USER, PASSWORD);
            con.setAutoCommit(false);

            createDatabase();

            return con;

        } catch (ClassNotFoundException e) {
            System.err.println(e.toString());
        } catch (SQLException e) {
            Alert dialog = new Alert(Alert.AlertType.ERROR);
            dialog.setContentText("Failed to establish connection to MySQL Server. Please check connection and try again.");
            dialog.show();

            System.err.println("Encountered error while attempting to connect to MySQL server: " + e.getErrorCode() + ": " + e.getCause());
        }
        return null;
    }


    /**
     * Receives a SQL update to make changes to the database.
     * @param sql
     * @throws SQLException
     */
    public void updateSQL(String sql) throws SQLException {

        try {
            pstate = con.prepareStatement(sql);

            pstate.executeUpdate(sql);

        } catch (SQLException e) {
            System.err.println("Encountered error while updating MySQL: " + e.getErrorCode() + ": " + e.getCause());
        }
    }
}
