package com.example.schoolhelper;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;


public class TeacherSettingsActivity extends AppCompatActivity {


    // Открытый файл
    Workbook openedBook;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_settings);

        System.setProperty("org.apache.poi.javax.xml.stream.XMLInputFactory", "com.fasterxml.aalto.stax.InputFactoryImpl");
        System.setProperty("org.apache.poi.javax.xml.stream.XMLOutputFactory", "com.fasterxml.aalto.stax.OutputFactoryImpl");
        System.setProperty("org.apache.poi.javax.xml.stream.XMLEventFactory", "com.fasterxml.aalto.stax.EventFactoryImpl");


//        findViewById(R.id.activity_main_button_generate).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                // генерируем новый файл EXEL
//                openedBook = new HSSFWorkbook();
//                Sheet sheet = openedBook.createSheet("MyData");
//
//                // Нумерация начинается с нуля
//                Row row = sheet.createRow(0);
//                // Мы запишем имя и дату в два столбца
//                Cell name = row.createCell(0);
//                name.setCellValue("John");
//                Cell birthdate = row.createCell(1);
//                DataFormat format = openedBook.createDataFormat();
//                CellStyle dateStyle = openedBook.createCellStyle();
//                dateStyle.setDataFormat(format.getFormat("(ss.MM.hh) dd.mm.yyyy"));
//                birthdate.setCellStyle(dateStyle);
//                // Нумерация лет начинается с 1900-го
//                birthdate.setCellValue(new Date());
//                // Меняем размер столбца
//                //sheet.autoSizeColumn(1);
//            }
//        });
//
//        findViewById(R.id.activity_main_button_save).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                // Записываем всё в файл
//                saveEXELDataInXlsFile(openedBook);
//                try {
//                    if (openedBook != null)
//                        openedBook.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//
//
//        findViewById(R.id.activity_main_button_export).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                // отправляем файл
//                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
//                sharingIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//                sharingIntent.setType("*/*");
//                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "theme");
//                //sharingIntent.putExtra(Intent.EXTRA_TEXT, "aaaaaaaaaaaaaaa fuck");
//
//                // находим свой каталог для файла
//                File sdPath = new File(TeacherSettingsActivity.this.getFilesDir().getAbsolutePath());
//                // формируем объект File, который находится в sdPath
//                File sdFile = new File(sdPath, FILE_NAME);
//
//                sharingIntent.putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(
//                        TeacherSettingsActivity.this, "com.texnar13.exel2.my_my_provider", sdFile
//                ));
//                startActivity(Intent.createChooser(sharingIntent, "Отправить через: "));
//
////                sharingIntent = new Intent(Intent.ACTION_SENDTO);
////                sharingIntent.setData(Uri.parse("mailto:addresses@com.ru"));
////                //sharingIntent.setData(Uri.parse("mailto:")); // пустая почта
////                sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "subject");// тема письма
////                sharingIntent.putExtra(Intent.EXTRA_TEXT,"message text"); // текст письма
////
////
////                sharingIntent.putExtra(Intent.EXTRA_EMAIL,
////                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + SAVE_DATA_DIRECTORY + '/' + FILE_NAME
////
////                        );
//
//            }
//        });
//
//    }
//
//
//    // сохранение EXEL в файл
//    private void saveEXELDataInXlsFile(Workbook data) {
//
//
//        FileOutputStream fileOutputStream = null;
//        try {
//            fileOutputStream = openFileOutput(FILE_NAME, MODE_PRIVATE);
//            data.write(fileOutputStream);
//
//            Toast.makeText(this, "Файл сохранен", Toast.LENGTH_SHORT).show();
//        } catch (IOException e) {
//            Toast.makeText(this, "Ошибка сохранения", Toast.LENGTH_SHORT).show();
//            e.printStackTrace();
//        } finally {
//            try {
//                if (fileOutputStream != null)
//                    fileOutputStream.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//
//
//    }
//
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
    }


    // !!!!!!!!!!  как в эксель
    // скачать отсюда https://github.com/centic9/poi-on-android/releases
    // poishadow-all.jar

    // потом импортировать по этой инструкции
    // https://www.youtube.com/watch?v=XmMaHCwhSs0


//
//    // получение сырого файла
//    private File getEXELFIle(){
//
//        FileInputStream fileInputStream = null;
//        try {
//            File file =
//            return openFileInput();
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
//    }
    // // плучаем данные из сохраненного файла
    // openedBook = getEXELDataFromXlsFile();
    // if (openedBook != null) {}
    // } else {
    //     Toast.makeText(TeacherSettingsActivity.this, "Сначала сохраните файл", Toast.LENGTH_SHORT).show();
    // }
    //
    // application/vnd.ms-excel   ("application/zip"); //if it's a zip otherwise whatever you file formap is.
    //intent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + zipDestinationString));

    // https://tproger.ru/translations/how-to-read-write-excel-file-java-poi-example/

    // https://metanit.com/java/android/13.1.php

    // https://stackoverflow.com/questions/48937516/reading-parsing-ms-project-mpp-java-android-5-0


    // https://stackoverflow.com/questions/28978581/how-to-make-intent-settype-for-pdf-xlsx-and-txt-file-android

    // https://stackoverflow.com/questions/16645100/how-to-send-attached-file-using-intent-in-android
}
