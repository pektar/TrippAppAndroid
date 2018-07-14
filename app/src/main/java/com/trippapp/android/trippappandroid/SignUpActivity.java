package com.trippapp.android.trippappandroid;

import android.app.Activity;
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
import io.grpc.trippapp.Account.SignupReq;
import io.grpc.trippapp.Account.SignupResp;

public class SignUpActivity extends AppCompatActivity {

    EditText firstName, lastName, username, email, password;
    Button signUp;
    TextView loginText;

    private void initView() {
        firstName = findViewById(R.id.et_firstname_signup);
        lastName = findViewById(R.id.et_lastname_signup);
        username = findViewById(R.id.et_username_signup);
        email = findViewById(R.id.et_email_signup);
        password = findViewById(R.id.et_password_signup);
        signUp = findViewById(R.id.bt_signup_signup);
        loginText = findViewById(R.id.tv_login_signup);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        initView();

        loginText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SignUpGrpcTask(SignUpActivity.this).execute(
                        firstName.getText().toString(),
                        lastName.getText().toString(),
                        username.getText().toString(),
                        password.getText().toString(),
                        email.getText().toString()
                );

            }
        });
    }


    private static class SignUpGrpcTask extends AsyncTask<String,String,String>{
        private ManagedChannel channel;
        private final WeakReference<Activity> activityRefrence;
        private SignupResp response;

        private SignUpGrpcTask(Activity activity) {
            activityRefrence = new WeakReference<>(activity);
        }


        @Override
        protected String doInBackground(String... params) {

            String firstName = params[0];
            String lastName = params[1];
            String username = params[2];
            String password = params[3];
            String email = params[4];

           try{
            channel = ManagedChannelBuilder.forAddress("localhost",8585).build();
            AccountServiceGrpc.AccountServiceBlockingStub stub = AccountServiceGrpc.newBlockingStub(channel);
            SignupReq request = SignupReq.newBuilder().setEmail(email).setFirstName(firstName).setLastName(lastName).setPassword(password).setUsername(username).build();
            response = stub.signup(request);
            return "ok";} catch (Exception e) {

               StringWriter sw = new StringWriter();
               PrintWriter pw = new PrintWriter(sw);
               e.printStackTrace(pw);
               pw.flush();
               return String.format("Failed... : %n%s", sw);
           }
        }

        @Override
        protected void onPostExecute(String s) {
            Toast.makeText(this.activityRefrence.get().getApplicationContext(), "server token :" + response.getToken() +
                    "\n server succes : " + response.getResult().getSuccuss() +
                            "\n server message : " + response.getResult().getMessage() +
                    "\n server fullname : " + response.getUser().getFullName() +
                    "\n server username : " + response.getUser().getUsername(), Toast.LENGTH_SHORT).show();
            try {
                channel.shutdown().awaitTermination(1, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }



}
