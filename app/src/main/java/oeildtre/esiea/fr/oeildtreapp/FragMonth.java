package oeildtre.esiea.fr.oeildtreapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class FragMonth extends Fragment {
    public static final String UPDATES_SENSOR2="UPDATES_SENSOR2";
    public static final String UPDATES_DATA2="UPDATES_DATA2";
    private JSONArray list_obj, list_data;
    private String sensor;
    private String link;
    private String web = "/dailydata/month?month=3&year=2017";
    private boolean fini = false;


    public class UpdateSensor extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (null != intent) {
                list_obj = getFromFile("sensors","");
                Log.d("FM.USensor", list_obj.toString());
                fini = true;
            }
        }
    }
    public class UpdateData extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (null != intent) {
                String graph="", graphMax="", graphMin="";
                list_data = getFromFile("sensors", "_data2");
                Log.d("FM.UData", list_data.toString());
                for(int i = 0; i<list_data.length();i++){
                    try {
                        if(i==0)graph = String.valueOf(list_data.getJSONObject(i).getInt("value")*3.3+15);
                        else graph = graph+","+String.valueOf(list_data.getJSONObject(i).getInt("value")*3.3+15);
                        if(i==0)graphMax = String.valueOf(list_data.getJSONObject(i).getInt("max")*3.3+15);
                        else graphMax = graphMax+","+String.valueOf(list_data.getJSONObject(i).getInt("max")*3.3+15);
                        if(i==0)graphMin = String.valueOf(list_data.getJSONObject(i).getInt("min")*3.3+15);
                        else graphMin = graphMin+","+String.valueOf(list_data.getJSONObject(i).getInt("min")*3.3+15);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                Log.v("Graph", graph);
                final ImageView img = (ImageView) getActivity().findViewById(R.id.img_month);
                Picasso.with(getActivity()).load(
                        "http://chart.apis.google.com/chart?cht=lc&chxt=x,x,y&chxl=1:||Temps||0:|1j|8j|16j|24j|31j&chd=t:"+
                                graph+"|"+graphMax+"|"+graphMin+"&chxr=2,-10,30&chs=400x150&chco=FF0000,FFFF00,00FFFF&chg=25,33,1,5").into(img);
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

        View month = inflater.inflate(R.layout.graphe_month, container, false);

        GraphService.startActionFoo(getContext(),"sensors","");
        IntentFilter inF = new IntentFilter(UPDATES_SENSOR2);
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(new UpdateSensor(),inF);

        String time ="10";
        final CheckBox temp = (CheckBox) month.findViewById(R.id.temp);
        final CheckBox humi = (CheckBox) month.findViewById(R.id.humi);
        final CheckBox son  = (CheckBox) month.findViewById(R.id.son);
        final CheckBox lum  = (CheckBox) month.findViewById(R.id.lum);
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
                                link = "/" + list_obj.getJSONObject(i).getString("id") + web;

                                GraphService.startActionBaz2(getContext(), "sensors", link);
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
                                link = "/" + list_obj.getJSONObject(i).getString("id") + web;

                                GraphService.startActionBaz2(getContext(), "sensors", link);
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
                                link = "/" + list_obj.getJSONObject(i).getString("id") + web;

                                GraphService.startActionBaz2(getContext(), "sensors", link);
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
                                link = "/" + list_obj.getJSONObject(i).getString("id") + web;

                                GraphService.startActionBaz2(getContext(), "sensors", link);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
        IntentFilter inFi = new IntentFilter(UPDATES_DATA2);
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(new UpdateData(), inFi);
        return month;
    }
}