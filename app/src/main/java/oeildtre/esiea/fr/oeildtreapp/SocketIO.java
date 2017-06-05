package oeildtre.esiea.fr.oeildtreapp;



import android.util.Log;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;


public class SocketIO {
    private static SocketIO INSTANCE = null;
    private Socket mSocket;

    private SocketIO() {
        try {
            mSocket = IO.socket("https://oeildtcam.hanotaux.fr:8080/");
        } catch (URISyntaxException e) {
            e.printStackTrace();
            Log.e("Socket.io", "Marche bordel de merde");
        }


        mSocket.connect();
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
