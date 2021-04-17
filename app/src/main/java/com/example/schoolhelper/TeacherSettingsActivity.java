package com.example.schoolhelper;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import org.apache.poi.ss.usermodel.Workbook;


public class TeacherSettingsActivity extends AppCompatActivity {


    // Открытый файл
    Workbook openedBook;

    Button importDataButton;

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_settings);
        Toolbar toolbar = findViewById(R.id.base_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        importDataButton = findViewById(R.id.teacher_settings_import_data_button);

        // выход из режима учителя
        findViewById(R.id.teacher_settings_delete_user_mode_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = preferences.edit();
                editor.putInt(MainActivity.PREF_USER_MODE, -1);
                editor.apply();

                setResult(TeacherMainPage.RESULT_CLEAR_DATA);
                finish();
            }
        });

        // some sheets works there \/

        System.setProperty("org.apache.poi.javax.xml.stream.XMLInputFactory", "com.fasterxml.aalto.stax.InputFactoryImpl");
        System.setProperty("org.apache.poi.javax.xml.stream.XMLOutputFactory", "com.fasterxml.aalto.stax.OutputFactoryImpl");
        System.setProperty("org.apache.poi.javax.xml.stream.XMLEventFactory", "com.fasterxml.aalto.stax.EventFactoryImpl");


//    // получение EXEL из файла
//    private Workbook getEXELDataFromXlsFile() {
//
//        FileInputStream fileInputStream = null;
//        try {
//            // читаем xls из файла
//            fileInputStream = openFileInput(FILE_NAME);
//            return new HSSFWorkbook(fileInputStream);
//
//        } catch (IOException e) {
//            Toast.makeText(this, "Ошибка загрузки", Toast.LENGTH_SHORT).show();
//            e.printStackTrace();
//        } finally {
//            // закрываем поток чтения
//            try {
//                if (fileInputStream != null) fileInputStream.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//        return null;


//        setResult(TeacherMainPage.RESULT_UPDATE); todo ване
//        finish();
    }
}
