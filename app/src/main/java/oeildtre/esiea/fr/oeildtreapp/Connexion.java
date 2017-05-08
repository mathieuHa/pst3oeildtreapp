package oeildtre.esiea.fr.oeildtreapp;

import android.drm.DrmStore;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;


public class Connexion extends Fragment {
    WebView webView;
    private GraphService gs = new GraphService();
    private Button con,ins;
    private EditText id,mdp;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FragmentManager fm = getFragmentManager();
        fm.addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                if(getFragmentManager().getBackStackEntryCount() == 0) {
                    if (webView.getAlpha() == 1) {
                        webView.setAlpha(0);
                        webView.setEnabled(false);
                        con.setEnabled(true);
                        ins.setEnabled(true);
                        id.setEnabled(true);
                        mdp.setEnabled(true);
                    } else getActivity().finish();
                }
            }
        });
    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View conn = inflater.inflate(R.layout.connexion, container, false);

        con = (Button) conn.findViewById(R.id.con);
        ins = (Button) conn.findViewById(R.id.ins);
        id = (EditText) conn.findViewById(R.id.id);
        mdp = (EditText) conn.findViewById(R.id.mdp);
        webView = (WebView) conn.findViewById(R.id.webView);;
        webView.setAlpha(0);
        webView.setEnabled(false);

        con.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String identifiant = id.getText().toString();
                String motsdepasse = mdp.getText().toString();
                Log.d("Connexion API", identifiant+"/"+motsdepasse);
            }
        });
        ins.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                con.setEnabled(false);
                ins.setEnabled(false);
                id.setEnabled(false);
                mdp.setEnabled(false);
                //webView.loadUrl("http://"+gs.getSource()+"/login");
                webView.loadUrl("http://www.journaldugamer.com/files/2017/02/Zelda-Breath-of-the-wild-DLC.jpg");
                webView.setEnabled(true);
                webView.setAlpha(1);
                webView.bringToFront();
                Log.d("alpha one", String.valueOf(webView.getAlpha()));
            }
        });

        return conn;
    }
}
