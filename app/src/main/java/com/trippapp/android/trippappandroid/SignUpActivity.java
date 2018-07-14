package com.trippapp.android.trippappandroid;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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
        loginText = findViewById(R.id.tv_signup_login);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        initView();
    }

    public void GotoLogin(View view) {
        startActivity(new Intent(this, LoginActivity.class));
    }
}
