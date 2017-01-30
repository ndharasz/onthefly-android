package com.example.noah.onthefly.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.example.noah.onthefly.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ActivityLogin extends AppCompatActivity {
    EditText usernameField;
    EditText passwordField;
    Button login;
    Button forgotPass;
    CheckBox rememberMe;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private String TAG = "ActivityLogin";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        inputSetup();
        buttonSetup();

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is logged in
                } else {
                    // Display error message?
                }
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    protected void inputSetup() {
        usernameField = (EditText) findViewById(R.id.username_input);
        passwordField = (EditText) findViewById(R.id.password_input);
        passwordField.setTypeface(Typeface.DEFAULT);
        passwordField.setTransformationMethod(new PasswordTransformationMethod());
    }

    protected void buttonSetup() {
        login = (Button) findViewById(R.id.log_in_button);
        forgotPass = (Button) findViewById(R.id.forgot_password_button);
        rememberMe = (CheckBox) findViewById(R.id.remember_me_checkbox);
    }

    protected void login(View loginButton) {
        Log.d(TAG, "Task started");
        mAuth.createUserWithEmailAndPassword(usernameField.getText().toString(),
                    passwordField.getText().toString())
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Log.d(TAG, "Login task complete");
                if (task.isSuccessful()) {
                    Intent flightListIntent = new Intent(ActivityLogin.this,
                                ActivityFlightList.class);
                    ActivityLogin.this.startActivity(flightListIntent);
                } else {
                    Log.d(TAG, "Login failed.");
                    // print error message?
                }
            }
        });

    }

    protected void forgotPassword(View v) {
        Intent forgotPasswordIntent = new Intent(this, ActivityForgotPassword.class);
        this.startActivity(forgotPasswordIntent);
    }

    protected void createAccount(View v) {
        Intent createAccountIntent = new Intent(this, ActivityCreateAccount.class);
        this.startActivity(createAccountIntent);
    }
}
