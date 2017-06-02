package oeildtre.esiea.fr.oeildtreapp;

import android.content.Context;

import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import java.net.URISyntaxException;

/**
 * Created by Max on 28/05/2017.
 */

public class SocketIO {
    private static SocketIO INSTANCE = null;
    private Socket mSocket;

    {
        try {
            mSocket = IO.socket("https://oeildtcam.hanotaux.fr:8080/");
        } catch (URISyntaxException e) {
            e.getStackTrace();
        }
    }
    private SocketIO() {
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
