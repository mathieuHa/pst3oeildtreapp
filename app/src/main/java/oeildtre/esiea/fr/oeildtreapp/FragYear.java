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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class FragYear extends Fragment {
    public static final String UPDATES_SENSOR="UPDATES_SENSOR";
    public static final String UPDATES_DATA="UPDATES_DATA";
    private JSONArray list_obj, list_data;
    private String sensor;
    private String link;
    private Sensor sensorList;


    public class UpdateSensor extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (null != intent) {
                list_obj = getFromFile("sensors","");
                Log.e("OK", list_obj.toString());
            }
        }
    }
    public class UpdateData extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (null != intent) {
                Log.d("D'acc", "Callback de Mathieu");
                list_data = getFromFile("sensors", link);
                for (int i = 0; i < list_data.length(); i++) {
                    try {
                        sensorList.setAlObject(list_data.getJSONObject(i));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
    public JSONArray getFromFile(String param1,String param2) {
        try {
            Log.e("coco",getActivity().getCacheDir() + "/"+param1+param2+".json" );
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
        GraphService.startActionFoo(getContext(),"sensors","");
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
                humi.setChecked(false);
                son.setChecked(false);
                lum.setChecked(false);
                sensor = "température";
                for (int i=0; i<list_obj.length(); i++){
                    try {
                        if (list_obj.getJSONObject(i).getString("name").equals(sensor)){
                            link = "/"+list_obj.getJSONObject(i).getString("id")+"/dailydata/year?year=2017";

                            GraphService.startActionBaz(getContext(),"sensors",link);
                            IntentFilter inFi = new IntentFilter(UPDATES_DATA);
                            LocalBroadcastManager.getInstance(getContext()).registerReceiver(new UpdateData(),inFi);
                            Log.e("Lol","On essaye" );
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        });
        lum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                humi.setChecked(false);
                son.setChecked(false);
                temp.setChecked(false);
                sensor = "luminosité";
            }
        });
        humi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                temp.setChecked(false);
                son.setChecked(false);
                lum.setChecked(false);
                sensor = "humidité";
            }
        });
        son.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                humi.setChecked(false);
                temp.setChecked(false);
                lum.setChecked(false);
                sensor = "son";
            }
        });

        /*ArrayList<CheckBox> cb = new ArrayList<CheckBox>();
        cb.add(temp);cb.add(humi);cb.add(son);cb.add(lum);





        for (CheckBox box : cb){
            if(box.isChecked()) {
                for (int i = 0; i < 365; i += 1)
                    time = time + "," + (int) (Math.random() * 20);
                Log.e("time", time);
            }
        }
        try {
            URL url = new URL("http://90.92.227.92/pst3oeildtre/web/app.php/sensors/1/data/day?day=19&month=2&year=2017");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }*/


        final ImageView img = (ImageView) year.findViewById(R.id.img_year);
        Picasso.with(getActivity()).load(
                "http://chart.apis.google.com/chart?cht=lc&chs=300x150" +
                        "&chd=t:"+time+"&chl=time").into(img);
        return year;
    }
}