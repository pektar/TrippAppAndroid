package com.trippapp.android.trippappandroid;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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
}
