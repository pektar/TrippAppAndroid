package com.trippapp.android.trippappandroid;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.ref.WeakReference;
import java.util.concurrent.TimeUnit;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.trippapp.Account.AccountServiceGrpc;
import io.grpc.trippapp.Account.LoginReq;
import io.grpc.trippapp.Account.LoginResp;

public class LoginActivity extends AppCompatActivity {

    EditText username_email, password;
    Button login;
    TextView signUpText;

    private void initViews() {
        username_email = findViewById(R.id.et_username_email_login);
        password = findViewById(R.id.et_password_login);
        login = findViewById(R.id.bt_login_login);
        signUpText = findViewById(R.id.tv_signup_login);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initViews();
    }

    public void GotoSingUp(View view) {
        startActivity(new Intent(this, SignUpActivity.class));
        new LoginGrpcTask(this).execute(
                username_email.getText().toString(),
                password.getText().toString());
    }

    private static class LoginGrpcTask extends AsyncTask<String, String, String> {
        private ManagedChannel channel;
        private final WeakReference<Activity> activityReference;
        private LoginResp response;


        private LoginGrpcTask(Activity activity) {
            activityReference = new WeakReference<>(activity);
        }

        @Override
        protected String doInBackground(String... params) {
            String username_email = params[0];
            String password = params[1];
            try {
                channel = ManagedChannelBuilder.forAddress("localhost", 8585).usePlaintext().build();
                AccountServiceGrpc.AccountServiceBlockingStub stub = AccountServiceGrpc.newBlockingStub(channel);
                LoginReq request = LoginReq.newBuilder().setUsernameEmail(username_email).setPassword(password).build();
                response = stub.login(request);
                return "ok";
            } catch (Exception e) {

                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                e.printStackTrace(pw);
                pw.flush();
                return String.format("Failed... : %n%s", sw);
            }
        }

        @Override
        protected void onPostExecute(String response) {
            Toast.makeText(activityReference.get().getApplicationContext(), "server token : " + this.response.getToken() + "\n server user "
                    + this.response.getUser() + "server message : " + this.response.getResult().getMessage() +
                    "\n server succes: " + this.response.getResult().getSuccuss(), Toast.LENGTH_LONG).show();
            try {
                channel.shutdown().awaitTermination(1, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }


        }
    }


}
