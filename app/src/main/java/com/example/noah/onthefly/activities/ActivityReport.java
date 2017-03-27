package com.example.noah.onthefly.activities;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.noah.onthefly.R;
import com.example.noah.onthefly.models.Flight;
import com.example.noah.onthefly.util.Grapher;
import com.example.noah.onthefly.util.Mailer;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ActivityReport extends AppCompatActivity {
    private final int WRITE_PERMISSION_REQUEST_CODE = 1;
    private final String WRITE_PERMISSION = android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
    CheckBox sendToSavedCheckbox;
    CheckBox sendtoOtherCheckbox;
    EditText otherEmailInput;
    CheckBox saveReportCheckbox;
    Flight flight;
    Bitmap graphImage;
    String reportFileName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        flight = (Flight) getIntent().getSerializableExtra("flight");
        otherEmailInput = (EditText)findViewById(R.id.user_input_email);
        checkboxSetup();
        displayAndCreate();
    }

    protected void checkboxSetup() {
        sendToSavedCheckbox = (CheckBox)findViewById(R.id.send_to_reg_checkbox);
        sendtoOtherCheckbox = (CheckBox)findViewById(R.id.send_to_other_checkbox);
        saveReportCheckbox = (CheckBox)findViewById(R.id.save_report_checkbox);
        saveReportCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    if(ContextCompat.checkSelfPermission(getBaseContext(), WRITE_PERMISSION) !=
                            getPackageManager().PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions((Activity)saveReportCheckbox.getContext(),
                                new String[]{WRITE_PERMISSION}, WRITE_PERMISSION_REQUEST_CODE);

                    }
                }
            }
        });
    }
    
    private void displayAndCreate() {
        ImageView graph = (ImageView) findViewById(R.id.graph);
        Grapher grapher = new Grapher();
        int size = getWindowManager().getDefaultDisplay().getWidth();
        graphImage = grapher.drawGraph(this, flight, size);
        graph.setImageBitmap(graphImage);
        //insert more stuff

        Date date = new Date();
        reportFileName = getFilesDir() + "report.pdf";
        try {
            FileOutputStream fos = new FileOutputStream(reportFileName);
            Document report = new Document();
            PdfWriter.getInstance(report, fos);
            report.open();
            report.add(new Chunk(""));
            report.addTitle("Weight and Balance Report");
            report.addSubject(date.toString());
            report.addAuthor("On the Fly - Android");
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            graphImage.compress(Bitmap.CompressFormat.PNG, 100, stream);
            Image pdfGraph = Image.getInstance(stream.toByteArray());
            float scaler = ((report.getPageSize().getWidth() - report.leftMargin()
                    - report.rightMargin()) / pdfGraph.getWidth()) * 100;
            pdfGraph.scalePercent(scaler);
            report.add(pdfGraph);
            //insert more stuff
            report.close();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "PDF creation failed.", Toast.LENGTH_SHORT).show();
        }
    }

    public void send(View inputButton) {
        String email = otherEmailInput.getText().toString();
        
        if(sendtoOtherCheckbox.isChecked()) {
            if (!email.equals("")) {
                if(Mailer.isEmailValid(email)) {
                    Mailer mailer = new Mailer();
                    try {
                        mailer.addAttachment(reportFileName);
                        mailer.setTo(email);
                        mailer.send();
                        Toast.makeText(this,
                                "Your weight and balance report has been sent to " + email + ".",
                                Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(this,
                                "Unable to Send report.",
                                Toast.LENGTH_LONG).show();
                    }
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
        File pdfFolder = new File( Environment.getExternalStorageDirectory(), "On The Fly Reports");
        if (!pdfFolder.exists()) {
            if(pdfFolder.mkdirs()) {
                Log.i("PDF Creation ", "Pdf Directory created");
            } else {
                Log.i("PDF Creation ", "Directory Creation Failed");
            }
        }
    }
}
