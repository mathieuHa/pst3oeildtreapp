package oeildtre.esiea.fr.oeildtreapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.EventLog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RadioButton;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class FragYear extends Fragment {
    public static final String UPDATES_SENSOR="UPDATES_SENSOR";
    public static final String UPDATES_DATA3="UPDATES_DATA";
    private JSONArray list_obj, list_data;
    private String sensor;
    private String link;
    private String graph="0";
    private boolean fini = false;


    public class UpdateSensor extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (null != intent) {
                list_obj = getFromFile("sensors","");
                Log.d("FY.USensor", list_obj.toString());
                try {
                    Log.i("Test",list_obj.getJSONObject(0).toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                fini = true;
            }
        }
    }
    public class UpdateData extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (null != intent) {
                graph="0";
                list_data = getFromFile("sensors", "_data3");
                Log.d("FD.UData", list_data.toString());
                for(int i = 0; i<list_data.length();i++){
                    try {
                        graph = graph+","+String.valueOf(list_data.getJSONObject(i).getInt("value"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                Log.v("Graph", graph);
                final ImageView img = (ImageView) getActivity().findViewById(R.id.img_year);
                Picasso.with(getActivity()).load(
                        "http://chart.apis.google.com/chart?cht=lc&chs=300x150" +
                                "&chd=t:"+graph+"&chl=time").into(img);
            }
        }
    }
    public JSONArray getFromFile(String param1,String param2) {
        try {
            InputStream is = new FileInputStream(getActivity().getCacheDir() + "/"+param1+param2+".json");
            byte[] buffer = new byte[is.available()];
            is.read(buffer);
            is.close();
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


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View year = inflater.inflate(R.layout.graphe_year, container, false);


        IntentFilter inF = new IntentFilter(UPDATES_SENSOR);
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(new UpdateSensor(),inF);


        String time ="10";
        final CheckBox temp = (CheckBox) year.findViewById(R.id.temp);
        final CheckBox humi = (CheckBox) year.findViewById(R.id.humi);
        final CheckBox son  = (CheckBox) year.findViewById(R.id.son);
        final CheckBox lum  = (CheckBox) year.findViewById(R.id.lum);
        temp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(fini == true && temp.isChecked()) {
                    humi.setChecked(false);
                    son.setChecked(false);
                    lum.setChecked(false);
                    sensor = "température";
                    for (int i = 0; i < list_obj.length(); i++) {
                        try {
                            if (list_obj.getJSONObject(i).getString("name").equals(sensor)) {
                                link = "/" + list_obj.getJSONObject(i).getString("id") + "/data/day?day=19&month=2&year=2017";

                                GraphService.startActionBaz3(getContext(), "sensors", link);

                                Log.e("FD.Temp", "On essaye");
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
                if (fini == true && lum.isChecked()){
                    humi.setChecked(false);
                    son.setChecked(false);
                    temp.setChecked(false);
                    sensor = "luminosité";

                    for (int i = 0; i < list_obj.length(); i++) {
                        try {
                            if (list_obj.getJSONObject(i).getString("name").equals(sensor)) {
                                link = "/" + list_obj.getJSONObject(i).getString("id") + "/data/day?day=19&month=2&year=2017";

                                GraphService.startActionBaz3(getContext(), "sensors", link);

                                Log.e("Lol", "On essaye");
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
                if (fini == true && humi.isChecked()) {
                    temp.setChecked(false);
                    son.setChecked(false);
                    lum.setChecked(false);
                    sensor = "humidité";

                    for (int i = 0; i < list_obj.length(); i++) {
                        try {
                            if (list_obj.getJSONObject(i).getString("name").equals(sensor)) {
                                link = "/" + list_obj.getJSONObject(i).getString("id") + "/data/day?day=19&month=2&year=2017";

                                GraphService.startActionBaz3(getContext(), "sensors", link);

                                Log.e("Lol", "On essaye");
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
                if (fini == true && son.isChecked()) {
                    humi.setChecked(false);
                    temp.setChecked(false);
                    lum.setChecked(false);
                    sensor = "son";

                    for (int i = 0; i < list_obj.length(); i++) {
                        try {
                            if (list_obj.getJSONObject(i).getString("name").equals(sensor)) {
                                link = "/" + list_obj.getJSONObject(i).getString("id") + "/data/day?day=19&month=2&year=2017";

                                GraphService.startActionBaz3(getContext(), "sensors", link);

                                Log.e("Lol", "On essaye");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
        IntentFilter inFi = new IntentFilter(UPDATES_DATA3);
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(new UpdateData(), inFi);
        return year;
    }
}