package oeildtre.esiea.fr.oeildtreapp;



import android.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;

public class FragDay extends Fragment {
    public static final String UPDATES_SENSOR1="UPDATES_SENSOR1";
    public static final String UPDATES_DATA1="UPDATES_DATA1";
    private JSONArray list_obj, list_data;
    private String sensor;
    private String link;
    private String web = "/data/day?day=19&month=2&year=2017";
    private boolean fini = false;

    private EditText mEdit;

    public class UpdateSensor extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (null != intent) {
                list_obj = getFromFile("sensors","");
                Log.d("FD.USensor", list_obj.toString());
                fini = true;
            }
        }
    }
    public class UpdateData extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (null != intent) {
                String graph="";
                list_data = getFromFile("sensors", "_data1");
                Log.d("FD.UData", list_data.toString());
                for(int i = 0; i<list_data.length();i++){
                    try {
                        if(i==0) graph = String.valueOf(list_data.getJSONObject(i).getInt("value")*3.3+15);
                        else graph = graph+","+String.valueOf(list_data.getJSONObject(i).getInt("value")*3.3+15);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                Log.i("Graph", graph);
                final ImageView img = (ImageView) getActivity().findViewById(R.id.img_day);
                Picasso.with(getActivity()).load(
                        //"http://chart.apis.google.com/chart?cht=lc&chs=300x150" +
                              //  "&chd=t:"+graph+"&chl=time").into(img);
                "http://chart.apis.google.com/chart?cht=lc&chxt=x,x,y&chxl=1:||Temps||0:|0h|6h|12h|18h|24h&chd=t:"+
                        graph+"&chxr=2,-10,30&chs=400x150&chco=FF0000&chg=25,33,1,5"/*&chxs=0,0000dd,10|1,0000dd,12,0"*/).into(img);
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

        final View day = inflater.inflate(R.layout.graphe_day, container, false);

        GraphService.startActionFoo(getContext(),"sensors","");
        IntentFilter inF = new IntentFilter(UPDATES_SENSOR1);
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(new UpdateSensor(),inF);

        final Button btn = (Button) day.findViewById(R.id.bdate);
        final CheckBox temp = (CheckBox) day.findViewById(R.id.temp);
        final CheckBox humi = (CheckBox) day.findViewById(R.id.humi);
        final CheckBox son  = (CheckBox) day.findViewById(R.id.son);
        final CheckBox lum  = (CheckBox) day.findViewById(R.id.lum);
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

                                GraphService.startActionBaz1(getContext(), "sensors", link);
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

                                GraphService.startActionBaz1(getContext(), "sensors", link);
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

                                GraphService.startActionBaz1(getContext(), "sensors", link);
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

                                GraphService.startActionBaz1(getContext(), "sensors", link);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
        IntentFilter inFi = new IntentFilter(UPDATES_DATA1);
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(new UpdateData(), inFi);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        return day;
    }
}