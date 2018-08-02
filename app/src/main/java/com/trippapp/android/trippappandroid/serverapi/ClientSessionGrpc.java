package com.trippapp.android.trippappandroid.serverapi;

/**
 * Created by pektar on 7/30/2018.
 */

public class ClientSessionGrpc {
    public final static String SESSION_HEADER_KEY = "session_key";
    private String sessionID;
    private static ClientSessionGrpc instance = null;

    private ClientSessionGrpc() {
        sessionID = null;
    }

    public static ClientSessionGrpc getInstance() {
        if (instance == null)
            instance = new ClientSessionGrpc();
        return instance;
    }

    public String getSessionID() {
        return sessionID;
    }

    public void setSessionID(String sessionID) {
        this.sessionID = sessionID;
    }
}
