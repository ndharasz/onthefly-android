package com.example.noah.onthefly.activities;
import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.noah.onthefly.R;
import com.example.noah.onthefly.models.Flight;
import com.example.noah.onthefly.util.Grapher;
import com.example.noah.onthefly.util.Mailer;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ActivityReport extends AppCompatActivity {
    CheckBox sendToSavedCheckbox;
    CheckBox sendtoOtherCheckbox;
    EditText otherEmailInput;
    CheckBox saveReportCheckbox;
    Flight flight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        flight = (Flight) getIntent().getSerializableExtra("flight");
        ImageView graph = (ImageView) findViewById(R.id.graph);
        Grapher grapher = new Grapher();
        int size = getWindowManager().getDefaultDisplay().getWidth();
        graph.setImageBitmap(grapher.drawGraph(this, flight, size));

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
    private void Save() {
        try {
            Log.d("PDF Creation ", "Starting...");

            File pdfFolder = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOCUMENTS), "pdfdemo");

            if (!pdfFolder.exists()) {
                pdfFolder.mkdir();
                Log.i("PDF Creation ", "Pdf Directory created");
            }

            Date date = new Date();
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(date);

            FileOutputStream fos = new FileOutputStream(new File(getFilesDir(), "WeightBalance" + timeStamp + ".pdf"));

            Document document = new Document();
            PdfWriter.getInstance(document, fos);
            document.open();
            document.add(new Chunk(""));
            document.addTitle("Weight and Balance Report");
            document.addSubject(date.toString());
            document.addAuthor("On the Fly - Android");
            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
