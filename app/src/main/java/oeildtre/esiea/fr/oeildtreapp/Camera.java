package oeildtre.esiea.fr.oeildtreapp;


import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;

import com.github.niqdev.mjpeg.DisplayMode;
import com.github.niqdev.mjpeg.Mjpeg;
import com.github.niqdev.mjpeg.MjpegView;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by Max on 29/04/2017.
 */

public class Camera extends Fragment implements MediaPlayer.OnPreparedListener,
        SurfaceHolder.Callback {
    final static String USERNAME = "admin";
    final static String PASSWORD = "camera";
    final static String RTSP_URL = "http://93.12.53.245:8090/?action=stream";

    private MediaPlayer _mediaPlayer;
    private SurfaceHolder _surfaceHolder;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set up a full-screen black window.
        getActivity().requestWindowFeature(Window.FEATURE_NO_TITLE);
        Window window = getActivity().getWindow();
        window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        window.setBackgroundDrawableResource(android.R.color.black);

        getActivity().setContentView(R.layout.camera);

        // Configure the view that renders live video.
        SurfaceView surfaceView =
                (SurfaceView) getActivity().findViewById(R.id.surfaceView);
        _surfaceHolder = surfaceView.getHolder();
        _surfaceHolder.addCallback(this);
        _surfaceHolder.setFixedSize(320, 240);
    }

    @Override
    public void surfaceChanged(
            SurfaceHolder sh, int f, int w, int h) {}

    @Override
    public void surfaceCreated(SurfaceHolder sh) {
        _mediaPlayer = new MediaPlayer();
        _mediaPlayer.setDisplay(_surfaceHolder);

        Context context = getActivity().getApplicationContext();
        Map<String, String> headers = getRtspHeaders();
        Uri source = Uri.parse(RTSP_URL);

        try {
            // Specify the IP camera's URL and auth headers.
            _mediaPlayer.setDataSource(context, source, headers);

            // Begin the process of setting up a video stream.
            _mediaPlayer.setOnPreparedListener(this);
            _mediaPlayer.prepareAsync();
        }
        catch (Exception e) {}
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder sh) {
        _mediaPlayer.release();
    }

    private Map<String, String> getRtspHeaders() {
        Map<String, String> headers = new HashMap<String, String>();
        String basicAuthValue = getBasicAuthValue(USERNAME, PASSWORD);
        headers.put("Authorization", basicAuthValue);
        return headers;
    }

    private String getBasicAuthValue(String usr, String pwd) {
        String credentials = usr + ":" + pwd;
        int flags = Base64.URL_SAFE | Base64.NO_WRAP;
        byte[] bytes = credentials.getBytes();
        return "Basic " + Base64.encodeToString(bytes, flags);
    }
    @Override
    public void onPrepared(MediaPlayer mp) {
        _mediaPlayer.start();
    }
    /*extends Activity {
    //@BindView(R.id.stream)
    MjpegView mjpegView;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera);

        final ImageButton droite = (ImageButton) findViewById(R.id.droite);
        final ImageButton gauche = (ImageButton) findViewById(R.id.gauche);
        final ImageButton haut = (ImageButton) findViewById(R.id.haut);
        final ImageButton bas = (ImageButton) findViewById(R.id.bas);
        final ImageButton recenter = (ImageButton) findViewById(R.id.recenter);
        final ImageButton cam = (ImageButton) findViewById(R.id.cam);

        droite.setImageResource(R.drawable.ic_media_play);
        gauche.setImageResource(R.drawable.ic_media_play);
        haut.setImageResource(R.drawable.ic_media_play);
        bas.setImageResource(R.drawable.ic_media_play);
        recenter.setImageResource(R.drawable.ic_menu_mylocation);
        cam.setImageResource(R.drawable.ic_menu_camera);
        gauche.setRotation(droite.getRotation() + 180);
        haut.setRotation(haut.getRotation() - 90);
        bas.setRotation(bas.getRotation() + 90);

        int TIMEOUT = 5;
        Mjpeg.newInstance()
                .credential("USERNAME", "PASSWORD")
                .open("http://93.12.53.245:8090/?action=stream")
                .subscribe(inputStream -> {
                    mjpegView.setSource(inputStream);
                    mjpegView.setDisplayMode(DisplayMode.BEST_FIT);
                    mjpegView.showFps(true);
                });
    }*/

}
