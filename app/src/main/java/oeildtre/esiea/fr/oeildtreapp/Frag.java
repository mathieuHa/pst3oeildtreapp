package oeildtre.esiea.fr.oeildtreapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;


public class Frag extends Fragment {
    public static final String UPDATES_SENSORS="UPDATES_SENSORS";
    public static final String UPDATES_DATA="UPDATES_DATA";
    protected JSONArray list_obj;
    protected String sensor;
    protected String link, link2;
    protected String web = "/data/day?day=19&month=2&year=2017";
    protected boolean fini = false;
    protected UpdateSensor us;
    protected Button btn;
    protected TextView text;


    public void init(View panel){
        btn= (Button) panel.findViewById(R.id.bdate);
        text = (TextView) panel.findViewById(R.id.date);
        final CheckBox temp = (CheckBox) panel.findViewById(R.id.temp);
        final CheckBox humi = (CheckBox) panel.findViewById(R.id.humi);
        final CheckBox son  = (CheckBox) panel.findViewById(R.id.son);
        final CheckBox lum  = (CheckBox) panel.findViewById(R.id.lum);
        temp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(fini && temp.isChecked()) {
                    humi.setChecked(false);
                    son.setChecked(false);
                    lum.setChecked(false);
                    sensor = "température";
                    for (int i = 0; i < list_obj.length(); i++) {
                        try {
                            if (list_obj.getJSONObject(i).getString("name").equals(sensor)) {
                                link = "/" + list_obj.getJSONObject(i).getString("id") + web;
                                link2 = "/" + list_obj.getJSONObject(i).getString("id");
                                GraphService.startActionBaz(getContext(), "sensors", link);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }
            }
        });
        lum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fini && lum.isChecked()){
                    humi.setChecked(false);
                    son.setChecked(false);
                    temp.setChecked(false);
                    sensor = "luminosité";

                    for (int i = 0; i < list_obj.length(); i++) {
                        try {
                            if (list_obj.getJSONObject(i).getString("name").equals(sensor)) {
                                link = "/" + list_obj.getJSONObject(i).getString("id") + web;
                                link2 = "/" + list_obj.getJSONObject(i).getString("id");
                                GraphService.startActionBaz(getContext(), "sensors", link);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
        humi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fini && humi.isChecked()) {
                    temp.setChecked(false);
                    son.setChecked(false);
                    lum.setChecked(false);
                    sensor = "humidité";

                    for (int i = 0; i < list_obj.length(); i++) {
                        try {
                            if (list_obj.getJSONObject(i).getString("name").equals(sensor)) {
                                link = "/" + list_obj.getJSONObject(i).getString("id") + web;
                                link2 = "/" + list_obj.getJSONObject(i).getString("id");
                                GraphService.startActionBaz(getContext(), "sensors", link);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
        son.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fini && son.isChecked()) {
                    humi.setChecked(false);
                    temp.setChecked(false);
                    lum.setChecked(false);
                    sensor = "son";

                    for (int i = 0; i < list_obj.length(); i++) {
                        try {
                            if (list_obj.getJSONObject(i).getString("name").equals(sensor)) {
                                link = "/" + list_obj.getJSONObject(i).getString("id") + web;
                                link2 = "/" + list_obj.getJSONObject(i).getString("id");
                                GraphService.startActionBaz(getContext(), "sensors", link);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });


    }
    public JSONArray getFromFile(String param1,String param2) {
        try {
            InputStream is = new FileInputStream(getContext().getCacheDir()+"/" + param1 + param2 + ".json");
            Log.d("FD.USensor", getActivity().getCacheDir() + "/" + param1 + param2 + ".json");
            byte[] buffer = new byte[is.available()];
            //is.read(buffer);
            //is.close();
            return new JSONArray(new String(buffer, "UTF-8"));

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
            return new JSONArray();
        } catch (JSONException e) {
            e.printStackTrace();
            return new JSONArray();
        }
        return new JSONArray();
    }

    public class UpdateSensor extends BroadcastReceiver {
        //@Override
        public void onReceive(Context context, Intent intent) {
            if (null != intent) {
                list_obj = getFromFile("sensors","");
                Log.d("FD.Sensor", list_obj.toString());
                if(list_obj.length() > 0) fini = true;
            }
        }
    }
}
