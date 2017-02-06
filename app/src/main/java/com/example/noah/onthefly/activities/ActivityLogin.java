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
import android.widget.Toast;

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

    private SharedPreferences loginPrefs;
    private SharedPreferences.Editor loginPrefsEditor;
    Boolean saveLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        inputSetup();
        buttonSetup();
        firebaseSetup();
        saveLoginSetup();
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

    protected void saveLoginSetup() {
        loginPrefs = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        loginPrefsEditor = loginPrefs.edit();
        saveLogin = loginPrefs.getBoolean("saveLogin", false);
        if (saveLogin == true) {
            usernameField.setText(loginPrefs.getString("username", ""));
            passwordField.setText(loginPrefs.getString("password", ""));
            rememberMe.setChecked(true);
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

    protected void firebaseSetup() {
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

    protected void login(View loginButton) {
        Log.d(TAG, "Task started");
        final String username = usernameField.getText().toString();
        final String pass = passwordField.getText().toString();
        if(username.equals("") || pass.equals("")){
            Toast.makeText(ActivityLogin.this,
                    "Username or password was invalid.", Toast.LENGTH_LONG).show();
        } else {
            mAuth.signInWithEmailAndPassword(username, pass)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "sucess");
                                if (rememberMe.isChecked()) {
                                    loginPrefsEditor.putBoolean("saveLogin", true);
                                    loginPrefsEditor.putString("username", username);
                                    loginPrefsEditor.putString("password", pass);
                                    loginPrefsEditor.commit();
                                }
                                Intent flightListIntent = new Intent(ActivityLogin.this,
                                        ActivityFlightList.class);
                                ActivityLogin.this.startActivity(flightListIntent);
                            } else {
                                Log.d(TAG, "failure");
                                Toast.makeText(ActivityLogin.this,
                                        "Username or password invalid.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
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
