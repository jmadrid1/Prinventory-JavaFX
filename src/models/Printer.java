package models;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Printer {

    private final IntegerProperty id;
    private final StringProperty make;
    private final StringProperty model;
    private final StringProperty serial;
    private final StringProperty status;
    private final StringProperty color;
    private final StringProperty owner;
    private final StringProperty department;
    private final StringProperty location;
    private final StringProperty floor;
    private final IntegerProperty ip;

    public Printer(Integer id, String make, String model,
                   String serial,  String status, String color,
                   String owner, String department, String location,
                   String floor, Integer ip) {

        this.id = new SimpleIntegerProperty(id);
        this.make = new SimpleStringProperty(make);
        this.model = new SimpleStringProperty(model);
        this.serial = new SimpleStringProperty(serial);
        this.status = new SimpleStringProperty(status);
        this.color = new SimpleStringProperty(color);
        this.owner = new SimpleStringProperty(owner);
        this.department = new SimpleStringProperty(department);
        this.location = new SimpleStringProperty(location);
        this.floor = new SimpleStringProperty(floor);
        this.ip = new SimpleIntegerProperty(ip);

    }

    public int getId(){ return id.get(); }

    public String getMake(){ return make.get(); }

    public String getModel(){ return model.get(); }

    public String getSerial(){ return serial.get(); }

    public String getOwner(){ return owner.get(); }

    public String getDepartment(){ return department.get(); }

    public String getLocation(){ return location.get(); }

    public String getFloor(){ return floor.get(); }

    public int getIp(){ return ip.get(); }

    public String getStatus(){ return status.get(); }

    public String getColor(){ return color.get(); }

}
