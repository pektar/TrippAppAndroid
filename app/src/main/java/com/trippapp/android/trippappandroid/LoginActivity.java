package com.trippapp.android.trippappandroid;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
    }

    private class LoginGrpc extends AsyncTask<String, String, LoginResp> {
        private ManagedChannel channel;

        @Override
        protected LoginResp doInBackground(String... params) {
            String username_email = params[0];
            String password = params[1];
            channel = ManagedChannelBuilder.forAddress("localhost",8585).usePlaintext().build();
            AccountServiceGrpc.AccountServiceBlockingStub stub = AccountServiceGrpc.newBlockingStub(channel);
            LoginReq request = LoginReq.newBuilder().setUsernameEmail(username_email).setPassword(password).build();
            LoginResp response = stub.login(request);
            return response;
        }

        @Override
        protected void onPostExecute(LoginResp response) {
            Toast.makeText(LoginActivity.this, "server token : " + response.getToken() + "\n server user "
                    + response.getUser() + "server message : " +response.getResult().getMessage() +
                    "\n server succes: " + response.getResult().getSuccuss() , Toast.LENGTH_LONG).show();
        }
    }




}
