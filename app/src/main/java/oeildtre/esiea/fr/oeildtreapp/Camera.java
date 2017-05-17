package oeildtre.esiea.fr.oeildtreapp;


        import android.content.SharedPreferences;
        import android.os.AsyncTask;
        import android.os.Bundle;
        import android.support.v4.app.Fragment;
        import android.util.Log;
        import android.view.LayoutInflater;
        import android.view.MotionEvent;
        import android.view.View;
        import android.view.ViewGroup;
        import android.webkit.WebView;
        import android.widget.ImageButton;
        import android.widget.LinearLayout;
        import android.widget.Toast;

        import org.json.JSONException;
        import org.json.JSONObject;

        import java.io.BufferedReader;
        import java.io.BufferedWriter;
        import java.io.IOException;
        import java.io.InputStreamReader;
        import java.io.OutputStream;
        import java.io.OutputStreamWriter;
        import java.net.HttpURLConnection;
        import java.net.MalformedURLException;
        import java.net.ProtocolException;
        import java.net.URL;

        import javax.net.ssl.HttpsURLConnection;

public class Camera extends Fragment {

    WebView webView;
    private ImageButton left,up,down,right,camera,center;
    private LinearLayout panel;
    private boolean visible = false;
    private GraphService gs = new GraphService();

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View cam = inflater.inflate(R.layout.camera, container, false);
        webView = (WebView) cam.findViewById(R.id.webView);
        webView.loadUrl(gs.getStream()+":8090/?action=stream");
        left = (ImageButton) cam.findViewById(R.id.left);
        up = (ImageButton) cam.findViewById(R.id.up);
        down = (ImageButton) cam.findViewById(R.id.down);
        right = (ImageButton) cam.findViewById(R.id.right);
        camera = (ImageButton) cam.findViewById(R.id.cam);
        center = (ImageButton) cam.findViewById(R.id.center);
        panel = (LinearLayout) cam.findViewById(R.id.buttonPanel);
        left.setRotation(180);
        up.setRotation(-90);
        down.setRotation(90);
        webView.bringToFront();

        //Le TOKEN !!!
        //Log.e("token", getContext().getSharedPreferences("MyPref",1).getString("Token",""));
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GraphService.startActionBaz2(getContext(), "media/shot?id=",getContext().getSharedPreferences("MyPref",1).getString("user",""));
                Toast.makeText(getContext(), "Picture Taken", Toast.LENGTH_LONG).show();
            }
        });


        webView.setOnTouchListener(new View.OnTouchListener() {

            final static int FINGER_RELEASED = 0;
            final static int FINGER_TOUCHED = 1;
            final static int FINGER_DRAGGING = 2;
            final static int FINGER_UNDEFINED = 3;

            private int fingerState = FINGER_RELEASED;


            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (fingerState == FINGER_RELEASED) fingerState = FINGER_TOUCHED;
                        else fingerState = FINGER_UNDEFINED;
                        break;
                    case MotionEvent.ACTION_UP:
                        if(fingerState != FINGER_DRAGGING) {
                            fingerState = FINGER_RELEASED;
                            Log.e("WebViewListener","click");
                            if (visible) {
                                panel.bringToFront();
                                visible = false;
                            } else {
                                webView.bringToFront();
                                visible = true;
                            }
                        }
                        else if (fingerState == FINGER_DRAGGING) fingerState = FINGER_RELEASED;
                        else fingerState = FINGER_UNDEFINED;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (fingerState == FINGER_TOUCHED || fingerState == FINGER_DRAGGING) fingerState = FINGER_DRAGGING;
                        else fingerState = FINGER_UNDEFINED;
                        break;
                    default:
                        fingerState = FINGER_UNDEFINED;
                }

                return false;
            }
        });
        return cam;
    }
}