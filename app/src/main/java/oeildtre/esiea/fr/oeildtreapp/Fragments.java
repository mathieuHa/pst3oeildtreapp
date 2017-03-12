package oeildtre.esiea.fr.oeildtreapp;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

public class Fragments extends FragmentActivity {

    private PagerAdapter mPagerAdapter;
    public JSONArray values;

    public static final String UPDATES_SENSOR="UPDATES_SENSOR";
    public static final String UPDATES_DATA="UPDATES_DATA";
    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.fragmentation);

        // Création de la liste de Fragments que fera défiler le PagerAdapter
        List fragments = new Vector();

        // Ajout des Fragments dans la liste
        fragments.add(Fragment.instantiate(this,FragDay.class.getName()));
        fragments.add(Fragment.instantiate(this,FragMonth.class.getName()));
        fragments.add(Fragment.instantiate(this,FragYear.class.getName()));
        GraphService.startActionFoo(getApplicationContext(),"sensors","");


        // Création de l'adapter qui s'occupera de l'affichage de la liste de
        // Fragments
        this.mPagerAdapter = new FragmentAdapter(super.getSupportFragmentManager(), fragments);

        ViewPager pager = (ViewPager) super.findViewById(R.id.viewpager);
        // Affectation de l'adapter au ViewPager
        pager.setAdapter(this.mPagerAdapter);

    }
}


