package models;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Vendor {

    private final IntegerProperty id;
    private final StringProperty company;
    private final StringProperty phone;
    private final StringProperty email;
    private final StringProperty street;
    private final StringProperty city;
    private final StringProperty state;
    private final StringProperty zip;

    public Vendor(Integer id, String company, String phone,
                  String email, String street, String city,
                  String state, String zipcode) {

        this.id = new SimpleIntegerProperty(id);
        this.company = new SimpleStringProperty(company);
        this.phone = new SimpleStringProperty(phone);
        this.email = new SimpleStringProperty(email);
        this.street = new SimpleStringProperty(street);
        this.city = new SimpleStringProperty(city);
        this.state = new SimpleStringProperty(state);
        this.zip = new SimpleStringProperty(zipcode);
    }

    public int getId(){ return id.get(); }

    public String getCompany(){ return company.get(); }

    public String getPhone(){ return phone.get(); }

    public String getEmail(){ return email.get(); }

    public String getStreet(){ return street.get(); }

    public String getCity(){ return city.get(); }

    public String getState(){ return state.get(); }

    public String getZip(){ return zip.get(); }
}
