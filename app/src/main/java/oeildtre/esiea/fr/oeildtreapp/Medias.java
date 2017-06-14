package oeildtre.esiea.fr.oeildtreapp;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
//import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class Medias extends Fragment {
    public static final String UPDATES_IMAGES="UPDATES_IMAGES";

    private static final String TAG = "RecyclerViewFragment";
    private static final String KEY_LAYOUT_MANAGER = "layoutManager";
    //private static final int SPAN_COUNT = 2;
    //private static final int DATASET_COUNT = 60;
    protected LayoutManagerType mCurrentLayoutManagerType;
    protected RecyclerView mRecyclerView;
    protected CustomAdapter mAdapter;
    protected RecyclerView.LayoutManager mLayoutManager;
    private List<ItemRecyclerView> item = new ArrayList<ItemRecyclerView>();
    private GraphService gs = new GraphService();
    private UpdateImages ui;
    private RadioButton my,all;
    @Override
    public void onDestroy() {
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(ui);
        super.onDestroy();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.medias, container, false);

        SharedPreferences Properties = getContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        SharedPreferences.Editor editor = Properties.edit();
        editor.putInt("position", 5);
        editor.commit();

        rootView.setTag(TAG);
        my = (RadioButton) rootView.findViewById(R.id.my);
        all = (RadioButton) rootView.findViewById(R.id.all);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);
        mRecyclerView.bringToFront();
        GraphService.startActionBaz2(getContext(),"media/images/",getContext().getSharedPreferences("MyPref",MODE_PRIVATE).getString("UserId",""));
        IntentFilter inF = new IntentFilter(UPDATES_IMAGES);
        ui = new UpdateImages();
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(ui, inF);
        // LinearLayoutManager is used here, this will layout the elements in a similar fashion
        // to the way ListView would layout elements. The RecyclerView.LayoutManager defines how
        // elements are laid out.
        mLayoutManager = new LinearLayoutManager(getActivity());

        mCurrentLayoutManagerType = LayoutManagerType.LINEAR_LAYOUT_MANAGER;

        if (savedInstanceState != null) {
            // Restore saved layout manager type.
            mCurrentLayoutManagerType = (LayoutManagerType) savedInstanceState
                    .getSerializable(KEY_LAYOUT_MANAGER);
        }
        setRecyclerViewLayoutManager(mCurrentLayoutManagerType);

        //mAdapter = new CustomAdapter(item);
        // Set CustomAdapter as the adapter for RecyclerView.
        //mRecyclerView.setAdapter(mAdapter);
        //setRecyclerViewLayoutManager(LayoutManagerType.LINEAR_LAYOUT_MANAGER);
        my.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                all.setChecked(false);
                GraphService.startActionBaz2(getContext(),"media/images/",getContext().getSharedPreferences("MyPref",MODE_PRIVATE).getString("UserId",""));
            }
        });
        all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                my.setChecked(false);
                GraphService.startActionBaz2(getContext(),"media/","images");
            }
        });
        return rootView;
    }

    /**
     * Set RecyclerView's LayoutManager to the one given.
     *
     * @param layoutManagerType Type of layout manager to switch to.
     */
    public void setRecyclerViewLayoutManager(LayoutManagerType layoutManagerType) {
        int scrollPosition = 0;

        // If a layout manager has already been set, get current scroll position.
        if (mRecyclerView.getLayoutManager() != null) {
            scrollPosition = ((LinearLayoutManager) mRecyclerView.getLayoutManager())
                    .findFirstCompletelyVisibleItemPosition();
        }

        /*switch (layoutManagerType) {
            case GRID_LAYOUT_MANAGER:
                mLayoutManager = new GridLayoutManager(getActivity(), SPAN_COUNT);
                mCurrentLayoutManagerType = LayoutManagerType.GRID_LAYOUT_MANAGER;
                break;
            case LINEAR_LAYOUT_MANAGER:*/
                mLayoutManager = new LinearLayoutManager(getActivity());
                mCurrentLayoutManagerType = LayoutManagerType.LINEAR_LAYOUT_MANAGER;
                /*break;
            default:
                mLayoutManager = new LinearLayoutManager(getActivity());
                mCurrentLayoutManagerType = LayoutManagerType.LINEAR_LAYOUT_MANAGER;
        }*/

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.scrollToPosition(scrollPosition);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putSerializable(KEY_LAYOUT_MANAGER, mCurrentLayoutManagerType);
        super.onSaveInstanceState(savedInstanceState);
    }

    public JSONArray getFromFile() {
        try {
            InputStream is;
            if (all.isChecked())is = new FileInputStream(getContext().getCacheDir()+"/images.json");
            else is = new FileInputStream(getContext().getCacheDir()+"/"+getContext().getSharedPreferences("MyPref",MODE_PRIVATE).getString("UserId","")+".json");
            int size=is.available();
            byte[] buffer=new byte[size];
            is.read(buffer);
            is.close();
            String text=new String(buffer);
            return new JSONArray(text);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new JSONArray();
    }
    /**
     * Generates Strings for RecyclerView's adapter. This data would usually come
     * from a local content provider or remote server.
     */

    private enum LayoutManagerType {
        //GRID_LAYOUT_MANAGER,
        LINEAR_LAYOUT_MANAGER
    }

    public class UpdateImages extends BroadcastReceiver {
        //@Override

        public void onReceive(Context context, Intent intent) {
            item.clear();
            if (null != intent) {
                JSONArray list_obj = getFromFile();
                Log.d("Images", list_obj.toString());
                try {
                    for (int i = 0; i< list_obj.length(); i++) {
                        String autor = list_obj.getJSONObject(i).getJSONObject("user").getString("login");
                        String url = gs.getMedia() +"/"+ list_obj.getJSONObject(i).getString("url");
                        String urlth = gs.getMedia() +"/"+ list_obj.getJSONObject(i).getString("urlth");
                        item.add(new ItemRecyclerView(autor, url, urlth));
                    }
                    mAdapter = new CustomAdapter(item);
                    // Set CustomAdapter as the adapter for RecyclerView.
                    mRecyclerView.setAdapter(mAdapter);
                    setRecyclerViewLayoutManager(LayoutManagerType.LINEAR_LAYOUT_MANAGER);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}