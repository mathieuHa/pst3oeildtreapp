package oeildtre.esiea.fr.oeildtreapp;


public class DataModel {

    private int icon;
    private String name;

    // Constructor.
    public DataModel(int icon, String name) {

        this.icon = icon;
        this.name = name;
    }

    public int getIcon() {
        return icon;
    }

    public String getName() {
        return name;
    }
}