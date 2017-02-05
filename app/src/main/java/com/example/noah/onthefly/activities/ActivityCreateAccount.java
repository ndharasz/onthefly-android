package com.example.noah.onthefly.activities;

import android.content.Intent;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.noah.onthefly.R;
import com.example.noah.onthefly.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ActivityCreateAccount extends AppCompatActivity {
    EditText firstName_input;
    EditText lastName_input;
    EditText email_input;
    EditText pass_input;
    EditText confirmPass_input;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    private final String TAG = "ActivityCreateAccount";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        inputSetup();

        mAuth = FirebaseAuth.getInstance();
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Log.d(TAG, "User is logged in: " + user.getEmail());
                }
            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthStateListener != null) {
            mAuth.removeAuthStateListener(mAuthStateListener);
        }
    }

    protected void inputSetup() {
        firstName_input = (EditText) findViewById(R.id.first_name_input);
        lastName_input = (EditText) findViewById(R.id.last_name_input);
        email_input = (EditText) findViewById(R.id.email_input);
        pass_input = (EditText) findViewById(R.id.password_input);
        confirmPass_input = (EditText) findViewById(R.id.confirm_password_input);
    }

    protected void createAccount(View v) {
        final String firstName = firstName_input.getText().toString();
        final String lastName = lastName_input.getText().toString();
        final String email = email_input.getText().toString();
        String password = pass_input.getText().toString();
        String conf_pass = confirmPass_input.getText().toString();

        if (!validPassword(password)) {
            Toast.makeText(ActivityCreateAccount.this,
                    "Password must have at least 8 characters.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(conf_pass)) {
            Toast.makeText(ActivityCreateAccount.this,
                    "The passwords do not match.", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                        if (!task.isSuccessful()) {
                            Toast.makeText(ActivityCreateAccount.this, "Invalid email.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            // Save user data after authentication is proven
                            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            DatabaseReference dRef = database.getReference("users");
                            dRef.child(uid).setValue(new User(firstName, lastName, email));

                            // Redirect to the login screen
                            Toast.makeText(ActivityCreateAccount.this, "Account creation successful",
                                    Toast.LENGTH_SHORT).show();
                            Intent loginIntent = new Intent(ActivityCreateAccount.this,
                                    ActivityLogin.class);
                            ActivityCreateAccount.this.startActivity(loginIntent);
                        }
                    }
                });
    }

    protected boolean validPassword(String password) {
        return (password.length() >= 8);
    }
}
