package com.example.schoolhelper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.google.zxing.Result;

public class TeacherMainPage extends AppCompatActivity {
    private CodeScanner mCodeScanner;
    public int students_length = 5;
    Student[] students = new Student[students_length];

    String TAG = "tag";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_main_page);

        // Реализация раюоты с камерой

        if (ContextCompat.checkSelfPermission(TeacherMainPage.this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_DENIED) {

            Toast.makeText(this, "#Чтобы сохранить данные, нужно разрешение на запись файлов#", Toast.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 123);
            finish();

        } else {// если разрешение есть
            CodeScannerView scannerView = findViewById(R.id.scanner_view);
            mCodeScanner = new CodeScanner(this, scannerView);
            mCodeScanner.setDecodeCallback(new DecodeCallback() {
                @Override
                public void onDecoded(@NonNull final Result result) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(TeacherMainPage.this, result.getText(), Toast.LENGTH_SHORT).show();

                            if (result.getText().length() == 10){
                                for (int i = 0; i < students_length; i++) {
                                    if (students[i] != null){
                                        if (students[i].checkId(result.getText())){
                                            Log.e(TAG, String.format("ученик %s %s %s под номером %s вошел в школу.", students[i].name, students[i].surname, students[i].last_name, students[i].id), "1");
                                        }
                                    }
                                }
                            }
                        }
                    });
                }
            });
            scannerView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mCodeScanner.startPreview();
                }
            });
        }

        // остальное

        students[0] = new Student("0123456789", "Вася", "Пупкин", "Алексеевич");
        for (int i = 1; i < 5; i++) {
            students[i] = null;
        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        mCodeScanner.startPreview();
    }

    @Override
    protected void onPause() {
        mCodeScanner.releaseResources();
        super.onPause();
    }


}

class Student {
    String id;
    String name;
    String surname;
    String last_name;

    /*
     * пришлось сделать id москвенок string так как в int он не влазит
     * */

    public Student(String id, String name, String surname, String last_name) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.last_name = last_name;
    }

    public boolean checkId(String id) {
        return this.id.equals(id);
    }
}


