package com.trippapp.android.trippappandroid.serverapi;

import android.content.Context;
import android.util.Log;

import com.google.common.io.ByteSink;
import com.google.common.io.FileWriteMode;
import com.google.common.io.Files;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import io.grpc.Channel;
import io.grpc.ClientInterceptors;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import io.grpc.trippapp.microservice.Chunk;
import io.grpc.trippapp.microservice.Empty;
import io.grpc.trippapp.microservice.LoginReq;
import io.grpc.trippapp.microservice.LoginResp;
import io.grpc.trippapp.microservice.ServerApiGrpc;
import io.grpc.trippapp.microservice.SignupReq;
import io.grpc.trippapp.microservice.SignupResp;

/**
 * Created by pektar on 7/30/2018.
 */

public class ServerApi {
    private Context mContext;
    private static final String host = "localhost";
    private static final int port = 8585;
    private static final String TAG = ServerApi.class.getName().toUpperCase();

    private final ManagedChannel managedChannel;
    private final Channel channel;
    private final ServerApiGrpc.ServerApiStub asyncStub;
    private final ServerApiGrpc.ServerApiBlockingStub blockingStub;

    public ServerApi(Context context) {
        this(ManagedChannelBuilder.forAddress(host, port).usePlaintext());
        mContext = context;
    }

    private ServerApi(ManagedChannelBuilder<?> channelBuilder) {
        managedChannel = ManagedChannelBuilder.forAddress(host, port).maxInboundMessageSize(10000000).usePlaintext().build();
        SessionHeaderInterceptor sessionHeaderInterceptor = new SessionHeaderInterceptor();
        channel = ClientInterceptors.intercept(managedChannel, sessionHeaderInterceptor);
        blockingStub = ServerApiGrpc.newBlockingStub(channel);
        asyncStub = ServerApiGrpc.newStub(channel);
    }

    public SignupResp signup(String username, String email, String rawPassword) throws StatusRuntimeException {
        SignupResp response = null;
        SignupReq request = SignupReq.newBuilder()
                .setUsername(username)
                .setEmail(email)
                .setRawPassword(rawPassword)
                .build();
        response = blockingStub.signup(request);
        if (request != null) {
            ClientSessionGrpc.getInstance().setSessionID(response.getSessionKey());
        }
        return response;
    }

    public LoginResp login(String username, String rawPassword) throws StatusRuntimeException {
        LoginResp response = LoginResp.getDefaultInstance();
        LoginReq request = LoginReq.newBuilder()
                .setUsername(username)
                .setRawPassword(rawPassword)
                .build();
        response = blockingStub.login(request);
        return response;
    }

    public void getFile() throws StatusRuntimeException, IOException {
        final File imageFile = File.createTempFile("amingrpc", ".mp4", new File(String.valueOf(mContext.getExternalCacheDir())));
        imageFile.deleteOnExit();
//        Files.touch(imageFile);
        final ByteSink byteSink = Files.asByteSink(imageFile, FileWriteMode.APPEND);
        StreamObserver<Chunk> streamObserver = new StreamObserver<Chunk>() {

            @Override
            public void onNext(Chunk value) {
                try {
                    byteSink.write(value.getBlob().toByteArray());
                } catch (IOException e) {
                    Log.d("GRPC/IO", "error write file : " + e);
                    onError(e);
                }
            }

            @Override
            public void onError(Throwable t) {
                Log.d("GRPC/IO", "onError " + t.toString());

            }

            @Override
            public void onCompleted() {
                Log.d("GRPC/IO", "write image to " + imageFile.getAbsoluteFile());
            }
        };
        asyncStub.getFile(Empty.getDefaultInstance(), streamObserver);
    }

    public Empty isLoggedIn() throws StatusRuntimeException {
        Empty response = null;
        Empty request = Empty.newBuilder().build();
        response = blockingStub.isLoggedIn(request);
        return response;
    }

    public void shutdown() throws InterruptedException {
        managedChannel.shutdown().awaitTermination(3, TimeUnit.SECONDS);
    }

    public Channel getChannel() {
        return channel;
    }

    public ServerApiGrpc.ServerApiStub getAsyncStub() {
        return asyncStub;
    }
}
