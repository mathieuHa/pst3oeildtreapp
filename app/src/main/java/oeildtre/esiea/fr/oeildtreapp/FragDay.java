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


public class FragDay extends Frag {
    private UpdateData ud;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View day = inflater.inflate(R.layout.graphe_day, container, false);
        GraphService.startActionFoo(getContext(), "sensors", "day");
        IntentFilter inF = new IntentFilter(UPDATES_SENSORS);
        us = new UpdateSensor();
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(us, inF);

        init(day);
        IntentFilter inFi = new IntentFilter(UPDATES_DATA);
        ud = new FragDay.UpdateData();
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(ud, inFi);
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
                                if (fini) GraphService.startActionBaz(getContext(), "sensors", link2+web);
                            }
                        }, mYear, mMonth, mDay);
                dpd.show();
            }
        });

        return day;
    }

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
                JSONArray list_data = getFromFile("sensors", "_data1");
                Log.d("FD.Data", list_data.toString());
                for(int i = 0; i<24*2;i++){
                    try {
                        if (i>list_data.length()) graph += ",0";
                        else {
                            if(i==0) graph = String.valueOf(list_data.getJSONObject(i).getInt("value"));
                            else graph += ","+String.valueOf(list_data.getJSONObject(i).getInt("value"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                Log.i("Graph", graph);
                final ImageView img = (ImageView) getActivity().findViewById(R.id.img);
                Picasso.with(getActivity()).load(
                        //"http://chart.apis.google.com/chart?cht=lc&chs=300x150" +
                              //  "&chd=t:"+graph+"&chl=time").into(img);
                "http://chart.apis.google.com/chart?cht=lc&chxt=x,x,y&chxl=1:||Temps/"+sensor+"||0:|0h|6h|12h|18h|24h&chd=t:"+
                        graph+"&chs=400x250&chco=FF0000&chg=25,33,1,5"/*&chxs=0,0000dd,10|1,0000dd,12,0"*/).into(img);
            }
        }
    }

}