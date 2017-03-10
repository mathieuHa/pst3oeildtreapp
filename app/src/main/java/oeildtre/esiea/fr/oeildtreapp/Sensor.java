package oeildtre.esiea.fr.oeildtreapp;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Max on 10/03/2017.
 */

public class Sensor {
    private ArrayList<JSONObject> al = new ArrayList<JSONObject>();
    public ArrayList<JSONObject> getAl(){
        return al;
    }
    public void setAlObject(JSONObject obj){
        al.add(obj);
    }
}
