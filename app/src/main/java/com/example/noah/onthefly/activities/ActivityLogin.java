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
import com.example.noah.onthefly.models.Plane;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.io.FileOutputStream;

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
        firebaseSetup();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
        syncPlanes();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    protected void syncPlanes() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "Searching for new planes");
                FirebaseDatabase db = FirebaseDatabase.getInstance();
                DatabaseReference ref = db.getReference("planes");
                ref.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        // Add this plane object to the persistent data
                        Plane plane = dataSnapshot.getValue(Plane.class);
                        plane.writeToFile(ActivityLogin.this);
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                        // Update this plane object in persistent data
                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {
                        // Remove this plane object from persistent data
                        Plane plane = dataSnapshot.getValue(Plane.class);
                        plane.deleteFile(ActivityLogin.this);
                        Log.d(TAG, "Plane was removed from database");
                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                        // I don't think this will ever be called
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // What is this event?
                    }
                });
            }
        });
        thread.start();
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
        mAuth.signInWithEmailAndPassword(usernameField.getText().toString(),
                    passwordField.getText().toString())
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "Login sucess");
                    Intent flightListIntent = new Intent(ActivityLogin.this,
                                ActivityFlightList.class);
                    ActivityLogin.this.startActivity(flightListIntent);
                } else {
                    Log.d(TAG, "Login failure.");
                    Toast.makeText(ActivityLogin.this, "Username or password was invalid.", Toast.LENGTH_SHORT).show();
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
