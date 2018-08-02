package com.trippapp.android.trippappandroid.serverapi;

import android.util.Log;

import io.grpc.CallOptions;
import io.grpc.Channel;
import io.grpc.ClientCall;
import io.grpc.ClientInterceptor;
import io.grpc.ForwardingClientCall.SimpleForwardingClientCall;
import io.grpc.ForwardingClientCallListener.SimpleForwardingClientCallListener;
import io.grpc.Metadata;
import io.grpc.MethodDescriptor;

public class SessionHeaderInterceptor implements ClientInterceptor {
    private final String TAG = "GRPC" + "/" + this.getClass().getName();
    private ClientSessionGrpc session;

    SessionHeaderInterceptor() {
        session = ClientSessionGrpc.getInstance();
    }

    @Override
    public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(MethodDescriptor<ReqT, RespT> method,
                                                               CallOptions callOptions, Channel next) {
        return new SimpleForwardingClientCall<ReqT, RespT>(next.newCall(method, callOptions)) {

            @Override
            public void start(Listener<RespT> responseListener, Metadata headers) {
                // Set custom header for authenticate
                if (session.getSessionID() != null) {
                    Metadata.Key<String> sessionKey = Metadata.Key.of(ClientSessionGrpc.SESSION_HEADER_KEY, Metadata.ASCII_STRING_MARSHALLER);
                    headers.put(sessionKey, session.getSessionID());
                    Log.d(TAG, "header sent to server : " + session.getSessionID());
                }
                super.start(new SimpleForwardingClientCallListener<RespT>(responseListener) {
                    // Get header of received response from server
                    @Override
                    public void onHeaders(Metadata headers) {
                        Log.d(TAG, "header received from server:" + headers);
                        super.onHeaders(headers);
                    }
                }, headers);
            }
        };
    }
}
