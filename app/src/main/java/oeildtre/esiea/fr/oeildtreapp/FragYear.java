package oeildtre.esiea.fr.oeildtreapp;

import android.app.DatePickerDialog;
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


public class FragYear extends Fragment {
    public static final String UPDATES_SENSOR3="UPDATES_SENSOR3";
    public static final String UPDATES_DATA3="UPDATES_DATA3";
    private JSONArray list_obj;
    private JSONArray list_data;
    private String sensor;
    private String link, link2;
    private String web = "/dailydata/year?year=2018";
    private boolean fini = false;

    private UpdateSensor us;
    private UpdateData ud;

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

        GraphService.startActionFoo(getContext(),"sensors","year");
        IntentFilter inF = new IntentFilter(UPDATES_SENSOR3);
        us = new UpdateSensor();
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(us,inF);

        final Button btn = (Button) year.findViewById(R.id.bdate);
        final TextView text = (TextView) year.findViewById(R.id.date);
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
                                link = "/" + list_obj.getJSONObject(i).getString("id") + web;
                                link2 = "/" + list_obj.getJSONObject(i).getString("id");
                                GraphService.startActionBaz3(getContext(), "sensors", link);
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
                                link2 = "/" + list_obj.getJSONObject(i).getString("id");
                                GraphService.startActionBaz3(getContext(), "sensors", link);
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
                                link2 = "/" + list_obj.getJSONObject(i).getString("id");
                                GraphService.startActionBaz3(getContext(), "sensors", link);
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
                                link2 = "/" + list_obj.getJSONObject(i).getString("id");
                                GraphService.startActionBaz3(getContext(), "sensors", link);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
        IntentFilter inFi = new IntentFilter(UPDATES_DATA3);
        ud = new UpdateData();
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(new UpdateData(), inFi);
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
                                text.setText(year);
                                web = "/dailydata/year?year="+year;
                                GraphService.startActionBaz3(getContext(), "sensors", link2+web);
                            }
                        }, mYear, mMonth, mDay);
                dpd.show();
            }
        });
        return year;
    }

    @Override
    public void onDestroy() {
        // Unregister since the activity is about to be closed.
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(us);
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(ud);
        super.onDestroy();
    }

    public class UpdateSensor extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (null != intent) {
                list_obj = getFromFile("sensors","");
                Log.d("FY.USensor", list_obj.toString());
                fini = true;
            }
        }
    }

    public class UpdateData extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (null != intent) {
                String graph="";
                list_data = getFromFile("sensors", "_data3");
                Log.d("FY.UData", list_data.toString());
                for(int i = 0; i<list_data.length();i++){
                    try {
                        if(i==0)graph = String.valueOf(list_data.getJSONObject(i).getInt("value"));
                        else graph = graph+","+String.valueOf(list_data.getJSONObject(i).getInt("value"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                Log.v("Graph", graph);
                final ImageView img = (ImageView) getActivity().findViewById(R.id.img);
                Picasso.with(getActivity()).load(
                        "http://chart.apis.google.com/chart?cht=lc&chs=400x250" +
                                "&chd=t:"+graph+"&chl=time").into(img);
            }
        }
    }
}