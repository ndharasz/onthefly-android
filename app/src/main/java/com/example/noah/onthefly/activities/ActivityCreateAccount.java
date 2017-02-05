package com.example.noah.onthefly.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.noah.onthefly.R;

import java.sql.ResultSetMetaData;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.graphics.Color.BLACK;
import static android.graphics.Color.RED;
import static com.google.android.gms.R.id.button;

public class ActivityCreateAccount extends AppCompatActivity {
    EditText firstName_input;
    EditText lastName_input;
    EditText email_input;
    EditText pass_input;
    EditText confirmPass_input;
    Button create;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        inputSetup();

        checkNames(firstName_input);
        checkNames(lastName_input);
        checkEMail(email_input);
        checkPassMatch(pass_input, confirmPass_input);

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

    protected void createAccount(View v) {
        String firstName = firstName_input.getText().toString();
        String lastName = lastName_input.getText().toString();
        String email = email_input.getText().toString();
        String password = pass_input.getText().toString();
        String conf_pass = confirmPass_input.getText().toString();



        if (firstName_input.getCurrentTextColor() == RED ||
                lastName_input.getCurrentTextColor() == RED ||
                email_input.getCurrentTextColor() == RED ||
                firstName.matches("") || lastName.matches("") || email.matches("") || password.matches("") || conf_pass.matches("")) {
                    android.app.AlertDialog notValid = new android.app.AlertDialog.Builder(ActivityCreateAccount.this, android.app.AlertDialog.THEME_DEVICE_DEFAULT_LIGHT).create();
                    notValid.setTitle("Form Error");
                    notValid.setMessage("One or more of your fields were empty or invalid. Invalid fields are marked in red.");
                    notValid.setButton(android.app.AlertDialog.BUTTON_POSITIVE, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                    dialog.dismiss();
                                }
                            });
                    notValid.show();

        } else if (!(password.equals(conf_pass))) {
            pass_input.setTextColor(RED);
            confirmPass_input.setTextColor(RED);
            android.app.AlertDialog notValid = new android.app.AlertDialog.Builder(ActivityCreateAccount.this, android.app.AlertDialog.THEME_DEVICE_DEFAULT_LIGHT).create();
            notValid.setTitle("Password Error");
            notValid.setMessage("Your passwords do not match.");
            notValid.setButton(android.app.AlertDialog.BUTTON_POSITIVE, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                            dialog.dismiss();
                        }
                    });
            notValid.show();

        } else {
            pass_input.setTextColor(BLACK);
            confirmPass_input.setTextColor(BLACK);
            Intent loginIntent = new Intent(this, ActivityLogin.class);
            this.startActivity(loginIntent);
        }

    }

    protected void checkNames(final EditText text) {
        text.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {

                    Pattern pattern = Pattern.compile("[~#@*+%{}<>\\[\\]|\"\\_^1234567890;!$^&*(]");
                    Matcher matcher = pattern.matcher(text.getText().toString());
                    if (matcher.find() || text.getText().toString().equals("")) {
                        text.setTextColor(RED);
                        android.app.AlertDialog notValid = new android.app.AlertDialog.Builder(ActivityCreateAccount.this, android.app.AlertDialog.THEME_DEVICE_DEFAULT_LIGHT).create();
                        notValid.setTitle("Name Input Error");
                        notValid.setMessage("The first and last name fields can contain only alphabetical characters.");
                        notValid.setButton(android.app.AlertDialog.BUTTON_POSITIVE, "OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {

                                        dialog.dismiss();
                                    }
                                });
                        notValid.show();
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


    protected void checkEMail(final EditText text) {
        text.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            public void onFocusChange(View v, boolean hasFocus) {

                if(!hasFocus) {

                    boolean isValid = false;

                    String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
                    CharSequence inputStr = text.getText().toString();

                    Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
                    Matcher matcher = pattern.matcher(inputStr);
                    if (!matcher.matches()) {
                        text.setTextColor(RED);
                        android.app.AlertDialog notValid = new android.app.AlertDialog.Builder(ActivityCreateAccount.this, android.app.AlertDialog.THEME_DEVICE_DEFAULT_LIGHT).create();
                        notValid.setTitle("Email Error");
                        notValid.setMessage("You have entered an invalid email address.");
                        notValid.setButton(android.app.AlertDialog.BUTTON_POSITIVE, "OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {

                                        dialog.dismiss();
                                    }
                                });
                        notValid.show();
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

    protected void checkPassMatch(final EditText pass, final EditText confirm) {
        confirm.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) {

                    if (!(pass.getText().toString().equals(confirm.getText().toString()))) {
                        pass.setTextColor(RED);
                        confirm.setTextColor(RED);

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
}
