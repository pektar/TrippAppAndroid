package com.trippapp.android.trippappandroid.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.trippapp.android.trippappandroid.R;
import com.trippapp.android.trippappandroid.serverapi.ServerApi;

import io.grpc.StatusRuntimeException;
import io.grpc.trippapp.microservice.SignupResp;


public class SignUpActivity extends AppCompatActivity {

    EditText username, email, rawPassword;
    Button signupBtn;
    TextView loginTx;
    ServerApi server = null;

    private void initView() {
        username = findViewById(R.id.et_username_signup);
        email = findViewById(R.id.et_email_signup);
        rawPassword = findViewById(R.id.et_password_signup);
        signupBtn = findViewById(R.id.bt_signup_signup);
        loginTx = findViewById(R.id.tv_login_signup);
    }

    private void clickView() {
        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    // send request to server and start next activity
                    SignupResp response = server.signup(username.getText().toString(), email.getText().toString(), rawPassword.getText().toString());
                    // TODO : Start InitProfileActivity.class instead of show Toast
                    Toast.makeText(SignUpActivity.this, "Successfully user registered, client sessionID is :\n" + response.getSessionKey(), Toast.LENGTH_LONG).show();
                } catch (StatusRuntimeException e) {
                    // show message error for user, message is user friendly
                    // TODO : Check e.getStatus().getCode and show relative AlertDialog instead of show Toast
                    Toast.makeText(SignUpActivity.this, "Message Error: " + e.getStatus().getDescription(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // attach view from xml
        initView();

        // implement OnClickListener interface for clickable view
        clickView();
    }

    @Override
    protected void onStart() {
        super.onStart();

        // create connection to server
        server = new ServerApi(this);
    }

    @Override
    protected void onRestart() {
        // TODO : Check connection in this state and reconnect
        super.onRestart();
    }

    @Override
    protected void onResume() {
        // TODO : Different between Terminate and Shutdown
        super.onResume();
    }

    @Override
    protected void onStop() {
        // close connection
        try {
            server.shutdown();
        } catch (InterruptedException e) {
            // TODO : why shutdown method has exception
            e.printStackTrace();
        }
        super.onStop();
    }
}