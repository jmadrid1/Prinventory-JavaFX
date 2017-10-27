package models;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Toner {

    private final IntegerProperty id;
    private final StringProperty make;
    private final StringProperty model;
    private final StringProperty tModel;
    private final StringProperty color;
    private final IntegerProperty black;
    private final IntegerProperty cyan;
    private final IntegerProperty yellow;
    private final IntegerProperty magenta;

    public Toner(Integer id, String make, String model,
                 String tModel, String color, Integer black,
                 Integer cyan, Integer yellow, Integer magenta) {

        this.id = new SimpleIntegerProperty(id);
        this.make = new SimpleStringProperty(make);
        this.model = new SimpleStringProperty(model);
        this.tModel = new SimpleStringProperty(tModel);
        this.color = new SimpleStringProperty(color);
        this.black = new SimpleIntegerProperty(black);
        this.cyan = new SimpleIntegerProperty(cyan);
        this.yellow = new SimpleIntegerProperty(yellow);
        this.magenta = new SimpleIntegerProperty(magenta);
    }

    public int getId(){ return id.get(); }

    public String getMake(){ return make.get(); }

    public String getModel(){ return model.get(); }

    public String getTModel(){ return tModel.get(); }

    public String getColor(){ return color.get(); }

    public int getBlack(){ return black.get(); }

    public int getCyan(){ return cyan.get(); }

    public int getYellow(){ return yellow.get(); }

    public int getMagenta(){ return magenta.get(); }

}
