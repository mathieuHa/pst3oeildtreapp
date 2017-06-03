package oeildtre.esiea.fr.oeildtreapp;



import android.util.Log;


import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import javax.net.ssl.SSLContext;

public class SocketIO {
    private static SocketIO INSTANCE = null;
    private Socket mSocket;

    private SocketIO() {
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, null, null);
            IO.setDefaultSSLContext(sc);
            IO.Options opts = new IO.Options();
            opts.secure = true;
            opts.sslContext = sc;
            mSocket = IO.socket("https://oeildtcam.hanotaux.fr:8080/", opts);
            Log.e("Socket.io","Marche bordel de merde");
        } catch (URISyntaxException e) {
            e.getStackTrace();
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            e.printStackTrace();
        }

        mSocket.connect();
    }

    /**
     * Point d'accès pour l'instance unique du singleton
     */
    public static synchronized SocketIO getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new SocketIO();
        }
        return INSTANCE;
    }
    public Socket getSocket(){
        return mSocket;
    }
}
