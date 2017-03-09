package oeildtre.esiea.fr.oeildtreapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.EventLog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RadioButton;

import com.squareup.picasso.Picasso;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class FragYear extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View year = inflater.inflate(R.layout.graphe_year, container, false);

        String time ="10";
        final CheckBox temp = (CheckBox) year.findViewById(R.id.temp);
        temp.setChecked(true);
        final CheckBox humi = (CheckBox) year.findViewById(R.id.humi);
        final CheckBox son = (CheckBox) year.findViewById(R.id.son);
        final CheckBox lum = (CheckBox) year.findViewById(R.id.lum);
        ArrayList<CheckBox> cb = new ArrayList<CheckBox>();
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
        }


        final ImageView img = (ImageView) year.findViewById(R.id.img_year);
        Picasso.with(getActivity()).load(
                "http://chart.apis.google.com/chart?cht=lc&chs=300x150" +
                        "&chd=t:"+time+"&chl=time").into(img);
        return year;
    }
}