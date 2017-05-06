package oeildtre.esiea.fr.oeildtreapp;


        import android.os.Bundle;
        import android.support.v4.app.Fragment;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.webkit.WebView;

public class Camera extends Fragment {
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View cam = inflater.inflate(R.layout.camera, container, false);
        WebView webView=(WebView) cam.findViewById(R.id.webView);

        webView.loadUrl("http://mathieuhanotaux.ddns.net:8090/?action=stream");
        return cam;
    }
}