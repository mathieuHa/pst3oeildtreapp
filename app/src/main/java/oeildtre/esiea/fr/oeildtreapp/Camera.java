package oeildtre.esiea.fr.oeildtreapp;


        import android.os.Bundle;
        import android.support.v4.app.Fragment;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.webkit.WebView;
        import android.widget.ImageButton;

public class Camera extends Fragment {
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View cam = inflater.inflate(R.layout.camera, container, false);
        WebView webView=(WebView) cam.findViewById(R.id.webView);
        GraphService gs = new GraphService();
        webView.loadUrl("http://"+gs.getSource()+":8090/?action=stream");

        ImageButton left = (ImageButton) cam.findViewById(R.id.left);
        ImageButton up = (ImageButton) cam.findViewById(R.id.up);
        ImageButton down = (ImageButton) cam.findViewById(R.id.down);
        ImageButton right = (ImageButton) cam.findViewById(R.id.right);
        ImageButton camera = (ImageButton) cam.findViewById(R.id.cam);
        ImageButton center = (ImageButton) cam.findViewById(R.id.center);
        left.setRotation(180);
        up.setRotation(-90);
        down.setRotation(90);
        return cam;
    }
}