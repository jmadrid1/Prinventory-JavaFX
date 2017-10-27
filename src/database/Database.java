package database;

import constants.DBSchema;
import javafx.scene.control.Alert;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Database class that creates the database schema if
 * it does not exist along with methods for simplified
 * database tasks.
 */

public class Database {

    private Connection con;
    private PreparedStatement pstate;

    private static final String HOST = "jdbc:mysql://localhost:3306/print?&useSSL=false";
    private static final String USER = "root";
    private static final String PASSWORD = "fiver55";

    /**
     * Creates the database's schema
     * @throws SQLException
     */
    public void createDatabase() throws SQLException {

        try {

            String sql = "CREATE TABLE IF NOT EXISTS " + DBSchema.PRINTER_TABLE + " (" +
                                                            DBSchema.ID + ", " +
                                                            DBSchema.PRINTER_MAKE + ", " +
                                                            DBSchema.PRINTER_MODEL + ", " +
                                                            DBSchema.PRINTER_SERIAL + ", " +
                                                            DBSchema.PRINTER_STATUS + ", " +
                                                            DBSchema.PRINTER_COLOR + ", " +
                                                            DBSchema.PRINTER_OWNER + ", " +
                                                            DBSchema.PRINTER_DEPT + ", " +
                                                            DBSchema.PRINTER_LOCATION + ", " +
                                                            DBSchema.PRINTER_FLOOR + ", " +
                                                            DBSchema.PRINTER_IP + ");";

            updateSQL(sql);

            sql = "CREATE TABLE IF NOT EXISTS " + DBSchema.TONER_TABLE + " (" +
                                                    DBSchema.ID + ", " +
                                                    DBSchema.TONER_MAKE + ", " +
                                                    DBSchema.TONER_MODEL + ", " +
                                                    DBSchema.TONER_TMODEL + ", " +
                                                    DBSchema.TONER_COLOR + ", " +
                                                    DBSchema.TONER_BLACK + ", " +
                                                    DBSchema.TONER_CYAN + ", " +
                                                    DBSchema.TONER_YELLOW + ", " +
                                                    DBSchema.TONER_MAGENTA + ");";

            updateSQL(sql);

            sql = "CREATE TABLE IF NOT EXISTS " + DBSchema.VENDOR_TABLE + " (" +
                                                    DBSchema.ID  + ", " +
                                                    DBSchema.VENDOR_COMPANY + ", " +
                                                    DBSchema.VENDOR_PHONE + ", " +
                                                    DBSchema.VENDOR_EMAIL + ", " +
                                                    DBSchema.VENDOR_STREET + ", " +
                                                    DBSchema.VENDOR_CITY + ", " +
                                                    DBSchema.VENDOR_STATE + ", " +
                                                    DBSchema.VENDOR_ZIPCODE + ");";

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
