package com.example.noah.onthefly.activities;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.noah.onthefly.R;
import com.example.noah.onthefly.util.GraphView;
import com.example.noah.onthefly.util.Mailer;

public class ActivityReport extends AppCompatActivity {
    CheckBox sendToSavedCheckbox;
    CheckBox sendtoOtherCheckbox;
    EditText otherEmailInput;
    CheckBox saveReportCheckbox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        LinearLayout layout = (LinearLayout)findViewById(R.id.activity_report);
        layout.addView(new GraphView(this));

        checkboxSetup();
        otherEmailInput = (EditText)findViewById(R.id.user_input_email);


    }

    protected void checkboxSetup() {
        sendToSavedCheckbox = (CheckBox)findViewById(R.id.send_to_reg_checkbox);
        sendtoOtherCheckbox = (CheckBox)findViewById(R.id.send_to_other_checkbox);
        saveReportCheckbox = (CheckBox)findViewById(R.id.save_report_checkbox);
    }

    public void send(View inputButton) {
        String email = otherEmailInput.getText().toString();
        if(sendtoOtherCheckbox.isChecked()) {
            if (!email.equals("")) {
                if(Mailer.isEmailValid(email)) {
                    Toast.makeText(this,
                            "Your weight and balance report has been sent to " + email + ".",
                            Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, "Email invalid.", Toast.LENGTH_LONG).show();
                    return;
                }
            } else {
                Toast.makeText(this, "Empty email field.", Toast.LENGTH_LONG).show();
                return;
            }
        }
        Intent intent = new Intent(ActivityReport.this, ActivityFlightList.class);
        startActivity(intent);
    }
}
