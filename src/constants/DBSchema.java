package constants;

/**
 * Class utilized for easily creating and managing
 * the database's schema.
 */

public class DBSchema {

    // Database Tables
    public static final String PRINTER_TABLE= "printers";
    public static final String TONER_TABLE = "toners";
    public static final String VENDOR_TABLE = "vendors";

    // Common Column
    public static final String ID = "ID INTEGER NOT NULL auto_increment PRIMARY KEY";

    // Printer Table Columns
    public static final String PRINTER_MAKE= "Make VARCHAR(25) NOT NULL";
    public static final String PRINTER_MODEL = "Model VARCHAR(11) NOT NULL";
    public static final String PRINTER_SERIAL = "Serial VARCHAR(30) NOT NULL";
    public static final String PRINTER_STATUS= "Status VARCHAR(10) NOT NULL";
    public static final String PRINTER_COLOR = "Color VARCHAR(10) NOT NULL";
    public static final String PRINTER_OWNER = "Ownership VARCHAR(28) NOT NULL";
    public static final String PRINTER_DEPT= "Department VARCHAR(15) NOT NULL";
    public static final String PRINTER_LOCATION = "Location VARCHAR(20) NOT NULL";
    public static final String PRINTER_FLOOR = "Floor VARCHAR(10) NOT NULL";
    public static final String PRINTER_IP = "IP INTEGER NOT NULL";

    // Toner Table Columns
    public static final String TONER_MAKE= "Make VARCHAR(25) NOT NULL";
    public static final String TONER_MODEL = "Model VARCHAR(11) NOT NULL";
    public static final String TONER_TMODEL = "TModel VARCHAR(28) NOT NULL";
    public static final String TONER_COLOR = "Color VARCHAR(10) NOT NULL";
    public static final String TONER_BLACK = "Black INTEGER NOT NULL";
    public static final String TONER_CYAN = "Cyan INTEGER NOT NULL";
    public static final String TONER_YELLOW = "Yellow INTEGER NOT NULL";
    public static final String TONER_MAGENTA = "Magenta INTEGER NOT NULL";

    // Vendor Table Columns
    public static final String VENDOR_COMPANY= "Company VARCHAR(25) NOT NULL";
    public static final String VENDOR_PHONE = "Phone VARCHAR(11) NOT NULL";
    public static final String VENDOR_EMAIL = "Email VARCHAR(28) NOT NULL";
    public static final String VENDOR_STREET = "Street VARCHAR(30) NOT NULL";
    public static final String VENDOR_CITY = "City VARCHAR(30) NOT NULL";
    public static final String VENDOR_STATE = "State VARCHAR(2) NOT NULL";
    public static final String VENDOR_ZIPCODE = "Zipcode VARCHAR(5) NOT NULL";

}
