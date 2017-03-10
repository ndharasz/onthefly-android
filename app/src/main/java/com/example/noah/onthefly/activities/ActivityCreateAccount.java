package com.example.noah.onthefly.activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.noah.onthefly.R;
import com.example.noah.onthefly.models.User;
import com.example.noah.onthefly.util.GlobalVars;
import com.example.noah.onthefly.util.Mailer;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.graphics.Color.BLACK;
import static android.graphics.Color.RED;

public class ActivityCreateAccount extends AppCompatActivity {
    EditText firstName_input;
    EditText lastName_input;
    EditText email_input;
    EditText pass_input;
    EditText confirmPass_input;
    Button create;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    private final String TAG = "ActivityCreateAccount";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        inputSetup();
        addNameListener(firstName_input);
        addNameListener(lastName_input);
        addEmailListener(email_input);
        addPassListener(pass_input, confirmPass_input);
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
        create = (Button) findViewById(R.id.create_account_button);
        create.requestFocus();
    }

    protected void addNameListener(final EditText text) {
        text.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {

                    Pattern pattern = Pattern.compile("[~#@*+%{}<>\\[\\]|\"\\_^1234567890;!$^&*(]");
                    Matcher matcher = pattern.matcher(text.getText().toString());
                    if (matcher.find() && !text.getText().toString().equals("")) {
                        text.setTextColor(RED);
                        Toast.makeText(ActivityCreateAccount.this, "Names can contain only alphabetical characters.", Toast.LENGTH_SHORT).show();
                    } else {
                        text.setTextColor(BLACK);
                    }
                    if (create.hasFocus()) {
                        createAccount(v);
                    }
                }
            }
        });

    }


    protected void addEmailListener(final EditText email_input) {
        email_input.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) {
                    String email = email_input.getText().toString();
                    if (!Mailer.isEmailValid(email)) {
                        email_input.setTextColor(RED);
                        Toast.makeText(ActivityCreateAccount.this, "You have entered an invalid email address.", Toast.LENGTH_SHORT).show();
                    } else {
                        email_input.setTextColor(BLACK);
                    }
                    if (create.hasFocus()) {
                        createAccount(v);
                    }
                }
            }


        });

    }

    protected void addPassListener(final EditText pass, final EditText confirm) {
        pass.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (pass.getText().toString().length() < 8) {
                        pass.setTextColor(RED);
                        confirm.setTextColor(RED);
                        Toast.makeText(v.getContext(), "Password must be at least 8 characters", Toast.LENGTH_SHORT).show();
                    } else {
                        pass.setTextColor(BLACK);
                        confirm.setTextColor(BLACK);
                    }
                }
            }
        });
        confirm.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) {
                    String passText = pass.getText().toString();
                    String confirmText = confirm.getText().toString();

                    if ((!passText.equals(confirmText)) && (!passText.equals(""))
                            && (!confirmText.equals(""))) {
                        pass.setTextColor(RED);
                        confirm.setTextColor(RED);
                        Toast.makeText(v.getContext(), "Passwords do not match", Toast.LENGTH_SHORT).show();

                    } else {
                        pass.setTextColor(BLACK);
                        confirm.setTextColor(BLACK);
                    }
                    if (create.hasFocus()) {
                        createAccount(v);
                    }
                }
            }
        });
    }

    protected void createAccount(View v) {
        final String firstName = firstName_input.getText().toString();
        final String lastName = lastName_input.getText().toString();
        final String email = email_input.getText().toString();
        String password = pass_input.getText().toString();
        String conf_pass = confirmPass_input.getText().toString();

        if (firstName.matches("") || lastName.matches("") ||
                email.matches("") || password.matches("") || conf_pass.matches("")) {

            Toast.makeText(this,
                    "One or more of your fields were empty.", Toast.LENGTH_SHORT).show();

        } else if (firstName_input.getCurrentTextColor() == RED ||
                lastName_input.getCurrentTextColor() == RED ||
                email_input.getCurrentTextColor() == RED ) {

            Toast.makeText(this,
                    "One or more of your fields were invalid.", Toast.LENGTH_SHORT).show();

        } else if (!validPassword(password)) {

            Toast.makeText(ActivityCreateAccount.this,
                    "An account cannot be created with this email and password", Toast.LENGTH_SHORT).show();
        } else if (!(password.equals(conf_pass))) {

            pass_input.setTextColor(RED);
            confirmPass_input.setTextColor(RED);
            Toast.makeText(this, "Your passwords did not match.", Toast.LENGTH_SHORT).show();

        } else {
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                            if (!task.isSuccessful()) {
                                Toast.makeText(ActivityCreateAccount.this,
                                        task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            } else {
                                // Save user data after authentication is proven
                                String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                FirebaseDatabase database = FirebaseDatabase.getInstance();
                                DatabaseReference dRef = database.getReference(GlobalVars.USER_DB);
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
    }

    protected boolean validPassword(String password) {
        return (password.length() >= 8);
    }
}
