package com.example.noah.onthefly.activities;
import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.noah.onthefly.R;
import com.example.noah.onthefly.models.Flight;
import com.example.noah.onthefly.models.WeightAndBalance;
import com.example.noah.onthefly.util.CalculationManager;
import com.example.noah.onthefly.util.Grapher;
import com.example.noah.onthefly.util.Mailer;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ActivityReport extends AppCompatActivity {
    private final int WRITE_PERMISSION_REQUEST_CODE = 1;
    private final String WRITE_PERMISSION = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    CheckBox sendToSavedCheckbox;
    CheckBox sendtoOtherCheckbox;
    EditText otherEmailInput;
    CheckBox saveReportCheckbox;
    Flight flight;
    Bitmap graphImage;
    String internalPath;
    String externalPath;
    String fileName;
    CalculationManager calculationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        if(ContextCompat.checkSelfPermission(getBaseContext(), WRITE_PERMISSION) !=
                getPackageManager().PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity)saveReportCheckbox.getContext(),
                    new String[]{WRITE_PERMISSION}, WRITE_PERMISSION_REQUEST_CODE);

        }
        flight = (Flight) getIntent().getSerializableExtra("flight");
        otherEmailInput = (EditText)findViewById(R.id.user_input_email);
        checkboxSetup();
        displayReport();
    }

    protected void checkboxSetup() {
        sendToSavedCheckbox = (CheckBox)findViewById(R.id.send_to_reg_checkbox);
        sendtoOtherCheckbox = (CheckBox)findViewById(R.id.send_to_other_checkbox);
    }
    
    private void displayReport() {
        calculationManager = new CalculationManager(ActivityReport.this, flight);
        calculationManager.calculate();

        LinearLayout wrapper = (LinearLayout) getLayoutInflater().inflate(
                R.layout.layout_report_table, null);
        ImageView graph = (ImageView) wrapper.findViewById(R.id.graph);
        Grapher grapher = new Grapher();
        int size = getWindowManager().getDefaultDisplay().getWidth();
        graphImage = grapher.drawGraph(this, flight, size);
        graph.setImageBitmap(graphImage);
        TableLayout table = (TableLayout) wrapper.findViewById(R.id.report_table);
        ((TextView)table.findViewById(R.id.ac_num)).setText(flight.getPlane());
        ((TextView)table.findViewById(R.id.bew)).setText("");
        ((TextView)table.findViewById(R.id.total_pass_weight)).setText("" + flight.getTotalPassengerWeight());
        ((TextView)table.findViewById(R.id.front_cargo)).setText("" + flight.getFrontBaggageWeight());
        ((TextView)table.findViewById(R.id.aft_cargo)).setText("" + flight.getAftBaggageWeight());
        ((TextView)table.findViewById(R.id.start_fuel)).setText("" + flight.getStartFuel());
        ((TextView)table.findViewById(R.id.fuel_burn)).setText("" + flight.getFuelFlow());
        ((TextView)table.findViewById(R.id.taxi_fuel)).setText("" + flight.getTaxiFuelBurn());
        ((TextView)table.findViewById(R.id.takeoff_weight)).setText("" + calculationManager.getTakeoffWeight());
        ((TextView)table.findViewById(R.id.zero_fuel_weight)).setText("" + calculationManager.getZeroFuelWeight());

        ((RelativeLayout)findViewById(R.id.report_wrapper)).addView(wrapper);
    }

    public void send(View inputButton) {
        createPDF();
        saveToExt();
        if(sendToSavedCheckbox.isChecked()) {
            sendTo(FirebaseAuth.getInstance().getCurrentUser().getEmail());
        }
        if(sendtoOtherCheckbox.isChecked()) {
            sendTo(otherEmailInput.getText().toString());
        }
        Intent intent = new Intent(ActivityReport.this, ActivityFlightList.class);
        startActivity(intent);
    }

    private void sendTo(String email) {
        if (!email.equals("")) {
            if(Mailer.isEmailValid(email)) {
                Mailer mailer = new Mailer(ActivityReport.this);
                try {
                    mailer.addAttachment(externalPath + fileName);
                    mailer.setTo(email);
                    if (mailer.send()) {
                        Toast.makeText(this,
                                "Your weight and balance report has been sent to " + email + ".",
                                Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(this, "Unable to send report.", Toast.LENGTH_LONG).show();
                    }
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

    private void saveToExt() {
        File internalFile = new File(internalPath, fileName);
        try {
            externalPath = Environment.getExternalStorageDirectory() + "/OnTheFlyPDFs/";
            File externalFolder = new File(externalPath);
            if (!externalFolder.exists()) {
                externalFolder.mkdirs();
            }
            File externalFile = new File(externalFolder, fileName);
            if (!externalFile.exists()) {
                externalFile.createNewFile();
            }
            FileChannel inChannel = new FileInputStream(internalFile).getChannel();
            FileChannel outChannel = new FileOutputStream(externalFile).getChannel();
            inChannel.transferTo(0, inChannel.size(), outChannel);
            inChannel.close();
            outChannel.close();
        } catch (Exception e) {
            Toast.makeText(this, "Could not save File", Toast.LENGTH_SHORT).show();
        }
        internalFile.delete();
    }

    private Document createPDF() {
        try {
            Date date = new Date();
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(date);
            internalPath = getFilesDir().getPath();
            fileName = "report_"+timestamp+".pdf";
            final File reportFile = new File(internalPath, fileName);
            reportFile.createNewFile();
            Document document = new Document(PageSize.A4, 10f, 10f, 10f, 10f);
            PdfWriter.getInstance(document, new FileOutputStream(reportFile));
            document.open();

            float lineSpacing = 23f;
            document.add(new Paragraph(new Phrase(lineSpacing, "Weight and Balance Report",
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 24f))));
            document.add(new Paragraph(new Phrase(lineSpacing, "Created by On The Fly:",
                    FontFactory.getFont(FontFactory.HELVETICA, 20f))));
            document.add(new Paragraph(new Phrase(lineSpacing, date.toString(),
                    FontFactory.getFont(FontFactory.COURIER_BOLD, 20f))));
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            String usrEmail = user.getEmail();
            String usrName = user.getDisplayName();
            if (usrName != null) {
                document.add(new Paragraph(new Phrase(lineSpacing,
                        "Flight Information Provided by: " + usrName,
                        FontFactory.getFont(FontFactory.TIMES_ITALIC, 16f))));
            } else {
                document.add(new Paragraph(new Phrase(lineSpacing, "Flight Information Provided by: "
                        + usrEmail, FontFactory.getFont(FontFactory.TIMES_ITALIC, 16f))));
            }

            Bitmap n = (Bitmap.createScaledBitmap(graphImage, 300, 300, true));
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            n.compress(Bitmap.CompressFormat.PNG, 10, stream);
            Image image = Image.getInstance(stream.toByteArray());
            Paragraph cog = new Paragraph(new Phrase(40f, "CoG Relationship", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20f)));
            cog.setAlignment(Element.ALIGN_CENTER);
            document.add(cog);
            image.setAlignment(Element.ALIGN_CENTER);
            document.add(image);

            Paragraph stats = new Paragraph(new Phrase(40f, "Flight Statistics", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20f)));
            stats.setAlignment(Element.ALIGN_CENTER);
            document.add(stats);
            document.add(new Paragraph(new Phrase(30f, "",
                    FontFactory.getFont(FontFactory.COURIER, 20f))));
            document.add(createTable());
            Paragraph flySafe = new Paragraph(new Phrase(40f, "Fly Safely!",
                    FontFactory.getFont(FontFactory.TIMES_ITALIC, 20f)));
            flySafe.setAlignment(Element.ALIGN_CENTER);
            document.add(flySafe);

            Bitmap l = BitmapFactory.decodeResource(this.getResources(),
                    R.drawable.app_logo);
            Bitmap icon = (Bitmap.createScaledBitmap(l, 100, 100, false));
            ByteArrayOutputStream s = new ByteArrayOutputStream();
            icon.compress(Bitmap.CompressFormat.PNG, 10, s);
            Image logo = Image.getInstance(s.toByteArray());
            logo.setAlignment(Element.ALIGN_CENTER);
            document.add(logo);

            document.close();
            return document;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public PdfPTable createTable() {
        PdfPTable table = new PdfPTable(2);
        table.setPaddingTop(20);
        PdfPCell cell;

        cell = new PdfPCell(new Phrase("Aircraft Tail Number"));
        cell.setColspan(1);
        table.addCell(cell);
        table.addCell(flight.getPlane());

        cell = new PdfPCell(new Phrase("BEW"));
        cell.setColspan(1);
        table.addCell(cell);
        table.addCell("420.69");

        cell = new PdfPCell(new Phrase("Total Weight of Passengers"));
        cell.setColspan(1);
        table.addCell(cell);
        table.addCell("" + flight.getTotalPassengerWeight());

        cell = new PdfPCell(new Phrase("Front Cargo Hold"));
        cell.setColspan(1);
        table.addCell(cell);
        table.addCell("" + flight.getFrontBaggageWeight());

        cell = new PdfPCell(new Phrase("Aft Cargo Hold"));
        cell.setColspan(1);
        table.addCell(cell);
        table.addCell("" + flight.getAftBaggageWeight());

        cell = new PdfPCell(new Phrase("Fuel Units"));
        cell.setColspan(1);
        table.addCell(cell);
        table.addCell("Gallons");

        cell = new PdfPCell(new Phrase("Fuel Tank"));
        cell.setColspan(1);
        table.addCell(cell);
        table.addCell(""+flight.getStartFuel());

        cell = new PdfPCell(new Phrase("Fuel Burn"));
        cell.setColspan(1);
        table.addCell(cell);
        table.addCell("" + flight.getFuelFlow());

        cell = new PdfPCell(new Phrase("Taxi Fuel Burn"));
        cell.setColspan(1);
        table.addCell(cell);
        table.addCell("" + flight.getTaxiFuelBurn());

        cell = new PdfPCell(new Phrase("Max Takeoff Weight"));
        cell.setColspan(1);
        table.addCell(cell);
        table.addCell(""+calculationManager.getTakeoffWeight());

        cell = new PdfPCell(new Phrase("Zero Fuel Weight"));
        cell.setColspan(1);
        table.addCell(cell);
        table.addCell(""+calculationManager.getZeroFuelWeight());

        return table;
    }
}
