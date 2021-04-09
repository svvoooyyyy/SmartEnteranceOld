package com.example.schoolhelper;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

public class StudentMainPage extends AppCompatActivity {
    ImageView qr_code;
    String code_value = "0123456789";

    Menu menu;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.standart_menu, menu);
        this.menu = menu;
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.activity_settings:
                Intent intent = new Intent(StudentMainPage.this, StudentSettingsActivity.class);
                startActivity(intent);
                break;
        }

        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_main_page);

        Toolbar toolbar = findViewById(R.id.base_toolbar);
        setSupportActionBar(toolbar);

        qr_code = findViewById(R.id.qr_code);

        // creating qr code
        MultiFormatWriter writer = new MultiFormatWriter();
        try {
            BitMatrix matrix = writer.encode(code_value, BarcodeFormat.QR_CODE, 400, 400);
            BarcodeEncoder encoder = new BarcodeEncoder();
            Bitmap bitmap = encoder.createBitmap(matrix);
            qr_code.setImageBitmap(bitmap);
            InputMethodManager manager = (InputMethodManager) getSystemService(
                    Context.INPUT_METHOD_SERVICE
            );

        } catch (WriterException e) {
            e.printStackTrace();
        }
        setResult(MainActivity.RESULT_BACK, null);
    }


}