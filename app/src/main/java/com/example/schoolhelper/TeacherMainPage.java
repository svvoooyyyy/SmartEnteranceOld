package com.example.schoolhelper;

import androidx.appcompat.app.AppCompatActivity;

import android.nfc.tech.NfcBarcode;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.pnuema.java.barcode.Barcode;

public class TeacherMainPage extends AppCompatActivity {
    ImageView activity_teacher_image_view_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_main_page);



        activity_teacher_image_view_back = findViewById(R.id.activity_teacher_image_view_back);
        activity_teacher_image_view_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}