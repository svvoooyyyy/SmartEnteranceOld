package com.example.schoolhelper;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.util.Locale;

public class StudentMainPage extends AppCompatActivity {



    private static final int ACTIVITY_BACK_CODE = 10;
    public static final int RESULT_CLEAR_DATA = 41;
    public static final int RESULT_UPDATE = 42;

    ImageView qrCode;
    TextView codeText;
    TextView nameText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_main_page);
        Toolbar toolbar = findViewById(R.id.base_toolbar);
        setSupportActionBar(toolbar);


        codeText = findViewById(R.id.activity_student_text_code);
        nameText = findViewById(R.id.activity_student_text_name);
        qrCode = findViewById(R.id.qr_code);


        findViewById(R.id.activity_student_button_scan).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(StudentMainPage.this, StudentSettingsActivity.class);
                startActivityForResult(intent, ACTIVITY_BACK_CODE);
            }
        });


        // проставляем значения
        updateFields();


        setResult(MainActivity.RESULT_BACK, null);
    }

    void updateFields() {

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        // имя
        String name = preferences.getString(StudentSettingsActivity.PREF_NAME,
                getResources().getString(R.string.no_name_text));
        nameText.setText(name);

        // код
        long code = preferences.getLong(StudentSettingsActivity.PREF_ID, -1);
        if (code >= 0 && code <= 9999999999L) {
            codeText.setText(String.format(Locale.getDefault(), "%010d", code));

            // creating qr code
            MultiFormatWriter writer = new MultiFormatWriter();
            try {
                BitMatrix matrix = writer.encode(String.format(Locale.getDefault(), "%010d", code), BarcodeFormat.QR_CODE,
                        getResources().getDimensionPixelSize(R.dimen.qr_code_image_size), getResources().getDimensionPixelSize(R.dimen.qr_code_image_size));
                BarcodeEncoder encoder = new BarcodeEncoder();
                Bitmap bitmap = encoder.createBitmap(matrix);
                qrCode.setImageBitmap(bitmap);
                InputMethodManager manager = (InputMethodManager) getSystemService(
                        Context.INPUT_METHOD_SERVICE
                );

            } catch (WriterException e) {
                e.printStackTrace();
            }
        } else {
            qrCode.setImageResource(R.drawable.no_code);
            codeText.setText(getResources().getText(R.string.no_code_text));
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ACTIVITY_BACK_CODE) {
            if (resultCode == RESULT_CLEAR_DATA) {
                setResult(MainActivity.RESULT_NULL, null);
                finish();
            } else if (resultCode == RESULT_UPDATE) {
                updateFields();
            }
        }

    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

}
