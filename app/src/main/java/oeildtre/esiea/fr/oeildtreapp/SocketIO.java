package oeildtre.esiea.fr.oeildtreapp;



import android.util.Log;

import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;

import io.socket.client.IO;
import io.socket.client.Socket;


public class SocketIO {
    private static SocketIO INSTANCE = null;
    private Socket mSocket;


    private SocketIO() {
        try {
            String url = "https://oeildtcam.hanotaux.fr:8080";
            /*IO.Options op = new IO.Options();
            SSLContext sslContext = SSLContext.getInstance("TLS");
            op.hostnameVerifier = new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    HostnameVerifier hv = HttpsURLConnection.getDefaultHostnameVerifier();
                    return hv.verify(hostname, session);
                }
            };
            op.sslContext = sslContext;
            op.secure = true;
            op.reconnection = true;
            op.reconnectionAttempts = 2;
            op.reconnectionDelay = 2000;*/

            mSocket = IO.socket(url);//, op);
            mSocket.connect();
        } catch (URISyntaxException e) {
            e.printStackTrace();
            Log.e("Socket.io", "Marche bordel de merde");
        }/* catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }*/
    }
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
