package oeildtre.esiea.fr.oeildtreapp;



import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;

import static android.content.Context.MODE_PRIVATE;


public class FragDay extends Fragment {
    public static final String UPDATES_SENSORS1="UPDATES_SENSORS1";
    public static final String UPDATES_DATA1="UPDATES_DATA1";
    private JSONArray list_obj;
    private String sensor;
    private String link, link2;
    private String web = "/data/day?day=19&month=2&year=2017";
    private boolean fini = false;
    private UpdateSensor us;
    private UpdateData ud;

    public JSONArray getFromFile(String param1,String param2) {
        try {
            InputStream is = new FileInputStream(getContext().getCacheDir()+"/" + param1 + param2 + ".json");
            Log.d("FD.USensor", getActivity().getCacheDir() + "/" + param1 + param2 + ".json");
            int size=is.available();
            byte[] buffer=new byte[size];
            is.read(buffer);
            is.close();
            String text=new String(buffer);
            return new JSONArray(text);

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return new JSONArray();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View day = inflater.inflate(R.layout.graphe_day, container, false);
        GraphService.startActionFoo(getContext(), "sensors", "day");
        IntentFilter inF = new IntentFilter(UPDATES_SENSORS1);
        us = new UpdateSensor();
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(us, inF);

        SharedPreferences Properties = getContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        SharedPreferences.Editor editor = Properties.edit();
        editor.putInt("position", 1);
        editor.commit();

        IntentFilter inFi = new IntentFilter(UPDATES_DATA1);
        ud = new UpdateData();
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(ud, inFi);

        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);
        web = "/data/day?day="+mDay+"&month="+(mMonth+1)+"&year="+mYear;

        final Button btn = (Button) day.findViewById(R.id.bdate);
        final TextView text = (TextView) day.findViewById(R.id.date);
        final CheckBox temp = (CheckBox) day.findViewById(R.id.temp);
        final CheckBox humi = (CheckBox) day.findViewById(R.id.humi);
        final CheckBox son  = (CheckBox) day.findViewById(R.id.son);
        final CheckBox lum  = (CheckBox) day.findViewById(R.id.lum);
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
                                link = "sensors/" + list_obj.getJSONObject(i).getString("id") + web;
                                link2 = "sensors/" + list_obj.getJSONObject(i).getString("id");
                                GraphService.startActionBaz1(getContext(), "1", link);
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
                                link = "sensors/" + list_obj.getJSONObject(i).getString("id") + web;
                                link2 = "sensors/" + list_obj.getJSONObject(i).getString("id");
                                GraphService.startActionBaz1(getContext(), "1", link);
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
                                link = "sensors/" + list_obj.getJSONObject(i).getString("id") + web;
                                link2 = "sensors/" + list_obj.getJSONObject(i).getString("id");
                                GraphService.startActionBaz1(getContext(), "1", link);
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
                                link = "sensors/" + list_obj.getJSONObject(i).getString("id") + web;
                                link2 = "sensors/" + list_obj.getJSONObject(i).getString("id");
                                GraphService.startActionBaz1(getContext(), "1", link);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR);
                int mMonth = c.get(Calendar.MONTH);
                int mDay = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dpd = new DatePickerDialog(getContext(),
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                text.setText(dayOfMonth + "/"
                                        + (monthOfYear + 1) + "/" + year);
                                web = "/data/day?day="+dayOfMonth+"&month="+(monthOfYear+1)+"&year="+year;
                                GraphService.startActionBaz1(getContext(), "1", link2+web);
                            }
                        }, mYear, mMonth, mDay);
                dpd.show();
            }
        });

        return day;
    }
    @Override
    public void onDestroy() {
        // Unregister since the activity is about to be closed.
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(us);
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(ud);
        super.onDestroy();
    }
    public class UpdateSensor extends BroadcastReceiver {
        //@Override
        public void onReceive(Context context, Intent intent) {
            if (null != intent) {
                list_obj = getFromFile("sensors","");
                Log.d("FD.Sensor", list_obj.toString());
                if (list_obj.length() > 0) fini = true;
            }
        }
    }

    public class UpdateData extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (null != intent) {
                final ImageView img = (ImageView) getActivity().findViewById(R.id.img);
                String graph="";
                String abscisse = "", str;
                JSONArray list_data = getFromFile("sensors", "_data1");
                Log.d("FD.Data", list_data.toString());
                if (list_data.length() < 6){
                    img.setImageResource(R.drawable.no_data);
                }
                else {
                    for (int i = 0; i < list_data.length(); i++) {
                        try {
                            if (0 == i % ((list_data.length() - 1) / 5)) {
                                str = list_data.getJSONObject(i).getString("date");
                                str = "|" + str.substring(str.length() - 14, str.length() - 12) + "h";
                                abscisse += str;
                            }
                            if (i == 0)
                                graph = String.valueOf(list_data.getJSONObject(i).getInt("value"));
                            if (i % 10 == 0 || Math.abs(list_data.getJSONObject(i).getInt("value") - list_data.getJSONObject(i - 1).getInt("value")) > 20)
                                graph += "," + String.valueOf(list_data.getJSONObject(i).getInt("value"));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    Log.i("Abscisse", abscisse + " " + graph.length());
                    Log.i("Graph", graph);
                    Picasso.with(getActivity()).load(
                            //"http://chart.apis.google.com/chart?cht=lc&chs=300x150" +
                            //  "&chd=t:"+graph+"&chl=time").into(img);
                            "http://chart.apis.google.com/chart?cht=lc&chxt=x,x,y&chxl=1:||Temps||0:" + abscisse + "&chd=t:" +
                                    graph + "&chs=400x250&chco=FF0000&chg=25,33,1,5"/*&chxs=0,0000dd,10|1,0000dd,12,0"*/).into(img);
                }
            }
        }
    }

}