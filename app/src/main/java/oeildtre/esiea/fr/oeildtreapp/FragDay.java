package oeildtre.esiea.fr.oeildtreapp;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class FragDay extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View day = inflater.inflate(R.layout.graphe_day, container, false);


        String time ="10";
        for (int i=10; i<24; i+=1)
            time = time+","+(int)(Math.random()*20);
        Log.e("time", time);
        final ImageView img = (ImageView) day.findViewById(R.id.img_day);
         Picasso.with(getActivity()).load(
                "http://chart.apis.google.com/chart?cht=lc&chs=300x150" +
                        "&chd=t:"+time+"&chl=time").into(img);
        return day;
    }
}
