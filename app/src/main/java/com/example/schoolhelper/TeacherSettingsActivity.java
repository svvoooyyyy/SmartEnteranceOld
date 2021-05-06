package com.example.schoolhelper;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.documentfile.provider.DocumentFile;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;


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


        findViewById(R.id.teacher_settings_import_data_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                // диалог разрешения на запись файлов
                if (ContextCompat.checkSelfPermission(TeacherSettingsActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) ==
                        PackageManager.PERMISSION_DENIED) {// если нет разрешения запрашиваем его

                    Toast.makeText(TeacherSettingsActivity.this,
                            "#Чтобы считать данные, нужно разрешение на чтение файлов#",
                            Toast.LENGTH_SHORT
                    ).show();
                    /*private static String[] PERMISSIONS_STORAGE = {
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                        };
                    */
                    ActivityCompat.requestPermissions(TeacherSettingsActivity.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1234);
                } else {// если разрешение есть

                    // проверяем доступность SD
                    if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                        Toast.makeText(TeacherSettingsActivity.this, "SD-карта не доступна ", Toast.LENGTH_LONG).show();
                        return;
                    }

                    Intent intent;
                    intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
                    intent.setType("*/*");
                    startActivityForResult(Intent.createChooser(intent, "Select file to upload "), PICKFILE_RESULT_CODE);
                }
            }
        });

        // some sheets works there \/

        System.setProperty("org.apache.poi.javax.xml.stream.XMLInputFactory", "com.fasterxml.aalto.stax.InputFactoryImpl");
        System.setProperty("org.apache.poi.javax.xml.stream.XMLOutputFactory", "com.fasterxml.aalto.stax.OutputFactoryImpl");
        System.setProperty("org.apache.poi.javax.xml.stream.XMLEventFactory", "com.fasterxml.aalto.stax.EventFactoryImpl");


    }

    private String getFileName(Uri uri) {
        DocumentFile documentFile = DocumentFile.fromSingleUri(this, uri);
        String fileName = documentFile.getName();
        return fileName;
    }

    private static final int PICKFILE_RESULT_CODE = 1;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICKFILE_RESULT_CODE) {
            if (resultCode == RESULT_OK) {

                if (data == null || data.getData() == null) {
                    Log.i("TEST", "File uri not found {}");
                    return;
                }

                StringBuilder myExcelData = new StringBuilder();
                FileInputStream fileInputStream = null;
                try {

                    Uri uri = data.getData();
                    if (getFileName(uri).trim().endsWith(".xls")) {

                        // читаем xls из файла
                        fileInputStream = (FileInputStream) getContentResolver().openInputStream(uri);
//                                new FileInputStream(filePath);
                        HSSFWorkbook book = new HSSFWorkbook(fileInputStream);

                        int number = book.getNumberOfSheets();
                        myExcelData.append(number);
                        if (number > 0) {
                            HSSFSheet sheet = book.getSheetAt(0);
                            //HSSFRow row = sheet.getRow(0);


                            int rowNumber = 1;
                            HSSFRow row = sheet.getRow(rowNumber);
                            while (row != null) {


                                HSSFCell idCell = row.getCell(0);
                                if (idCell != null) {

                                    long id = -1;
                                    switch (idCell.getCellType()) {

                                        case 0://NUMERIC(0)
                                            id = (long) idCell.getNumericCellValue();
                                            break;
                                        case 1://STRING
                                            String idText = idCell.getStringCellValue().trim();
                                            try {
                                                id = Long.parseLong(idText);
                                            } catch (NumberFormatException e) {
                                                e.printStackTrace();
                                                Toast.makeText(this, "Пропущен неправильный id:\"" + idText + '\"', Toast.LENGTH_SHORT).show();
                                            }
                                            break;
                                        default:
                                    }


                                    if (id < 9999999999L && id > 0) {
                                        HSSFCell nameCell = row.getCell(1);
                                        HSSFCell classCell = row.getCell(2);

                                        try {
                                            Log.e("TAG", "id = " + id +
                                                    "  name = " + (nameCell == null ? '-' : nameCell.getStringCellValue()) +
                                                    "  class = " + (classCell == null ? '-' : classCell.getStringCellValue())
                                            );
                                        } catch (java.lang.IllegalStateException e) {
                                            e.printStackTrace();
                                            Toast.makeText(this, "Пропущена неправильная ячейка, где id=\"" + id + '\"', Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        Log.e("TAG", "Пропущен неправильный id:\"" + id + '\"');
                                        Toast.makeText(this, "Пропущен неправильный id:\"" + id + '\"', Toast.LENGTH_SHORT).show();
                                    }
                                }


                                // к следующей
                                rowNumber++;
                                row = sheet.getRow(rowNumber);
                            }


                        }


                        Toast.makeText(this, myExcelData, Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(this, "Необходим фйл формата .xls", Toast.LENGTH_SHORT).show();
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Ошибка загрузки", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                } finally {
                    // закрываем поток чтения
                    try {
                        if (fileInputStream != null) fileInputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
