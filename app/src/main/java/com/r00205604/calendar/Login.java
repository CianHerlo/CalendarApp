package com.r00205604.calendar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity {

    EditText emailInput, passwordInput;
    Button registerBTN, loginBTN;
    FirebaseAuth fireAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        loginBTN = findViewById(R.id.loginBTN);
        registerBTN = findViewById(R.id.registerBTN);
        fireAuth = FirebaseAuth.getInstance();

        loginBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailInput.getText().toString().trim();
                String password = passwordInput.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    emailInput.setError("Email is Required.");
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    passwordInput.setError("Password is Required.");
                    return;
                }
                if (password.length() < 6) {
                    passwordInput.setError("Minimum 6 Characters for Password");
                    return;
                }

                fireAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(Login.this, "Login Successful", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(Login.this, "Error!" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        registerBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailInput.getText().toString().trim();
                String password = passwordInput.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    emailInput.setError("Email is Required.");
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    passwordInput.setError("Password is Required.");
                    return;
                }
                if (password.length() < 6) {
                    passwordInput.setError("Minimum 6 Characters for Password");
                    return;
                }

                fireAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(Login.this, "User Registered", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), Login.class));
                    } else {
                        Toast.makeText(Login.this, "Error!" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}