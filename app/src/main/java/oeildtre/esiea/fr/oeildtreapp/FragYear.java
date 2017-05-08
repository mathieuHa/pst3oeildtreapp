package oeildtre.esiea.fr.oeildtreapp;

import android.app.DatePickerDialog;
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
import android.widget.DatePicker;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.Calendar;


public class FragYear extends Frag {
    //http://mathieuhanotaux.ddns.net/pst3oeildtre/web/app.php/sensors/4/dailydata/year?year=2017
    private UpdateData ud;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View year = inflater.inflate(R.layout.graphe_year, container, false);

        GraphService.startActionFoo(getContext(),"sensors","year");
        IntentFilter inF = new IntentFilter(UPDATES_SENSORS);
        us = new UpdateSensor();
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(us,inF);
        init(year);

        IntentFilter inFi = new IntentFilter(UPDATES_DATA);
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
                                text.setText((monthOfYear + 1) + "/" + year);
                                web = "/dailydata/year?year="+year;
                                if (fini) GraphService.startActionBaz(getContext(), "sensors", link2+web);
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



    public class UpdateData extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (null != intent) {
                String graph="";
                JSONArray list_data = getFromFile("sensors", "_data3");
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