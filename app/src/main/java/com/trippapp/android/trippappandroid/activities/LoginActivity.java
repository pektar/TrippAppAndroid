package com.trippapp.android.trippappandroid.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.trippapp.android.trippappandroid.R;
import com.trippapp.android.trippappandroid.serverapi.ServerApi;

import io.grpc.StatusRuntimeException;
import io.grpc.trippapp.microservice.LoginResp;

public class LoginActivity extends AppCompatActivity {

    EditText username_email, rawPassword;
    Button loginBtn;
    TextView signupTxt;
    ServerApi server = null;

    private void initView() {
        username_email = findViewById(R.id.et_username_email_login);
        rawPassword = findViewById(R.id.et_password_login);
        loginBtn = findViewById(R.id.bt_login_login);
        signupTxt = findViewById(R.id.txv_signup_login);
    }

    private void clickView() {
        signupTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(LoginActivity.this, "x", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    // send request to server and start next activity
                    LoginResp response = server.login(username_email.getText().toString(), rawPassword.getText().toString());
                    // TODO : Start MainActivity.class instead of show Toast
                    Toast.makeText(LoginActivity.this, "Successfully user logged in.", Toast.LENGTH_LONG).show();
                } catch (StatusRuntimeException e) {
                    // show message error for user, message is user friendly
                    // TODO : Check e.getStatus().getCode and show relative AlertDialog instead of show Toast
                    Toast.makeText(LoginActivity.this, "Message Error: " + e.getStatus().getDescription(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

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
