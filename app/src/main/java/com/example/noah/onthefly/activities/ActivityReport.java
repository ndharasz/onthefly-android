package com.example.noah.onthefly.activities;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.noah.onthefly.R;
import com.example.noah.onthefly.models.Flight;
import com.example.noah.onthefly.models.Passenger;
import com.example.noah.onthefly.util.Grapher;
import com.example.noah.onthefly.util.Mailer;
import com.itextpdf.text.Chunk;
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
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Locale;

public class ActivityReport extends AppCompatActivity {
    CheckBox sendToSavedCheckbox;
    CheckBox sendtoOtherCheckbox;
    EditText otherEmailInput;
    CheckBox saveReportCheckbox;
    Flight flight;

    private String fpath;

    private String editFPath;


    Bitmap flightGraph;
    byte[] bytes;
    String TAG = "Account Creation";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        flight = (Flight) getIntent().getSerializableExtra("flight");
        ImageView graph = (ImageView) findViewById(R.id.graph);
        Grapher grapher = new Grapher();


        int size = getWindowManager().getDefaultDisplay().getWidth();
        graph.setImageBitmap(grapher.drawGraph(this, flight, size));
        flightGraph = grapher.getBmp();


        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        checkboxSetup();
        otherEmailInput = (EditText) findViewById(R.id.user_input_email);

    }

    protected void checkboxSetup() {
        sendToSavedCheckbox = (CheckBox) findViewById(R.id.send_to_reg_checkbox);
        sendtoOtherCheckbox = (CheckBox) findViewById(R.id.send_to_other_checkbox);
        saveReportCheckbox = (CheckBox) findViewById(R.id.save_report_checkbox);
    }

    protected void returnHome(View button) {
        if (!saveReportCheckbox.isChecked()) {
            File file = new File(editFPath);
            file.delete();
        }
        Intent intent = new Intent(ActivityReport.this, ActivityFlightList.class);
        startActivity(intent);

    }

    public void send(View inputButton) {
        String email = otherEmailInput.getText().toString();
        if (sendtoOtherCheckbox.isChecked()) {
            if (!email.equals("")) {
                if (Mailer.isEmailValid(email)) {
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

        Document pdf = createPDF();



        AlertDialog.Builder builder = new AlertDialog.Builder(this, android.R.style.Theme_Holo_Dialog);

        if (saveReportCheckbox.isChecked()) {
            builder.setTitle("PDF Created and Saved Locally");
        } else {
            builder.setTitle("PDF Created");
        }
        builder.setMessage("Your PDF has been generated.")
                .setCancelable(false)
                .setPositiveButton("Show Me", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //if (saveReportCheckbox.isChecked()) {
                            File file = new File(fpath);
                            Intent target = new Intent(Intent.ACTION_VIEW);
                            target.setDataAndType(Uri.fromFile(file), "application/pdf");
                            target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                            Intent intent = Intent.createChooser(target, "Open File");
                            try {
                                startActivity(intent);
                            } catch (ActivityNotFoundException e) {
                            }
                        //}
                    }
                }).setNegativeButton("Return to Flights", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (!saveReportCheckbox.isChecked()) {
                    Log.d(TAG, "deleting " + fpath);
                    File file = new File(editFPath);
                    file.delete();
                }
                Intent intent = new Intent(ActivityReport.this, ActivityFlightList.class);
                startActivity(intent);
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
        Button home = (Button) findViewById(R.id.home);
        home.setBackgroundColor(Color.RED);
    }

    private Document createPDF() {
        try {
            Log.d("PDF Creation ", "Starting...");
            Date date = new Date();
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(date);
            Document document = new Document(PageSize.A4, 10f, 10f, 10f, 10f);
            //if (saveReportCheckbox.isChecked()) {
                File folder = new File(Environment.getExternalStorageDirectory().getPath() + "/OnTheFlyPDFs/");
                folder.mkdirs();
                fpath = Environment.getExternalStorageDirectory().getPath() + "/OnTheFlyPDFs/" + timeStamp + ".pdf";
                editFPath = Environment.getExternalStorageDirectory().getPath() + File.separator + "OnTheFlyPDFs" + File.separator + timeStamp + ".pdf";
                Log.d(TAG, "File Creation:" + fpath);
                final File file = new File(fpath);
                if (!file.exists()) {
                    file.createNewFile();
                    Log.d(TAG, "File Created:" + fpath);
                }
                PdfWriter.getInstance(document, new FileOutputStream(file));
            //} else {
            //    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
              //  PdfWriter.getInstance(document, outputStream);
                //bytes = outputStream.toByteArray();
            //}
            document.open();


            float lineSpacing = 23f;
            document.add(new Paragraph(new Phrase(lineSpacing, "Weight and Balance Report",
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 24f))));
            document.add(new Paragraph(new Phrase(lineSpacing, "Created by On The Fly:",
                    FontFactory.getFont(FontFactory.HELVETICA, 20f))));
            document.add(new Paragraph(new Phrase(lineSpacing, date.toString(),
                    FontFactory.getFont(FontFactory.COURIER_BOLD, 20f))));
            if (ActivityLogin.getName() != null) {
                document.add(new Paragraph(new Phrase(lineSpacing,
                        "Flight Information Provided by: " + ActivityLogin.getName(),
                        FontFactory.getFont(FontFactory.TIMES_ITALIC, 16f))));
            } else {
                document.add(new Paragraph(new Phrase(lineSpacing, "Flight Information Provided by: "
                        + ActivityLogin.getUserEmail(), FontFactory.getFont(FontFactory.TIMES_ITALIC, 16f))));
            }

            Bitmap n = (Bitmap.createScaledBitmap(flightGraph, 300, 300, false));
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
        table.addCell("");

        cell = new PdfPCell(new Phrase("Total Weight of Passengers"));
        cell.setColspan(1);
        table.addCell(cell);
        double totalWeight = 0;
        Collection<Passenger> c = flight.getPassengers().values();
        for (Passenger pass : c) {
            totalWeight += pass.getWeight();
        }
        table.addCell("" + totalWeight);

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
        table.addCell("");

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
        table.addCell("");

        cell = new PdfPCell(new Phrase("Zero Fuel Weight"));
        cell.setColspan(1);
        table.addCell(cell);
        table.addCell("");

        return table;
    }
}