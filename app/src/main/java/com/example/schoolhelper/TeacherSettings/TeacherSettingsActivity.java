package com.example.schoolhelper.TeacherSettings;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
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

import com.example.schoolhelper.MainActivity;
import com.example.schoolhelper.R;
import com.example.schoolhelper.TeacherMainPage;
import com.example.schoolhelper.data_base.DataBaseOpenHelper;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;


public class TeacherSettingsActivity extends AppCompatActivity implements ImportDialogInterface {


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

                Uri uri = data.getData();
                if (getFileName(uri).trim().endsWith(".xls")) {
                    readDataFromXlsFile(uri);
                } else {
                    Toast.makeText(this, "Необходим фйл формата .xls", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    // -------------------------- сканирование файла --------------------------

    LinkedList<Student> students;

    @Override
    public void importAccept() {
        DataBaseOpenHelper db = new DataBaseOpenHelper(this);
        db.clearAuthorizedStudentsList();
        for (Student student : students) {
            db.createAuthorizedStudent(
                    student.moskvenokId,
                    student.name,
                    student.group
            );
        }
    }

    private void readDataFromXlsFile(Uri uri) {

        StringBuilder myExcelData = new StringBuilder();
        FileInputStream fileInputStream = null;
        students = new LinkedList<>();
        try {

            // читаем xls из файла
            fileInputStream = (FileInputStream) getContentResolver().openInputStream(uri);//  new FileInputStream(filePath);
            HSSFWorkbook book = new HSSFWorkbook(fileInputStream);

            int number = book.getNumberOfSheets();
            myExcelData.append(number);
            if (number > 0) {
                HSSFSheet sheet = book.getSheetAt(0);

                int successCounter = 0;
                int errorCounter = 0;
                StringBuilder importLog = new StringBuilder();

                int rowNumber = 1;
                HSSFRow row = sheet.getRow(rowNumber);
                while (row != null) {


                    ParseResult result = parseRow(row, rowNumber);

                    if (result.getErrorsCount() != 0) {
                        errorCounter++;
                        importLog.append("Ошибка в строке: ").append((rowNumber + 1)).append(" [\n").append(result.getErrorCodes()).append("];\n");
                    } else {
                        successCounter++;
                        // прповеряем id на уникальность
                        Iterator<Student> iterator = students.iterator();
                        boolean errorFlag = false;
                        int tempRowCounter = 0;
                        while (!errorFlag && iterator.hasNext()) {
                            tempRowCounter++;
                            Student student = iterator.next();
                            if (student.moskvenokId == result.moskvenokId) {
                                errorFlag = true;
                                importLog.append("Ошибка в строке:").append((rowNumber + 1)).append("[\n")
                                        .append("\t0006: Такой id москвенка уже был (строка: ").append(tempRowCounter).append(")\n];\n");
                            }
                        }

                        if (!errorFlag)
                            students.add(new Student(
                                    result.moskvenokId,
                                    result.name,
                                    result.form
                            ));
                    }
                    // к следующей
                    rowNumber++;
                    row = sheet.getRow(rowNumber);
                }

                // Обработали все строки
                importLog.append("Импорт завершён \n\tОшибок: ").append(errorCounter)
                        .append("\n\tПринято записей: ").append(successCounter);

                Log.i("TAG", importLog.toString());

                ImportConfirmDialogFragment dialogFragment = new ImportConfirmDialogFragment();
                Bundle args = new Bundle();
                args.putString(ImportConfirmDialogFragment.ARGS_LOG_TEXT, importLog.toString());
                dialogFragment.setArguments(args);
                dialogFragment.show(getSupportFragmentManager(), "acceptDialog");

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


    private ParseResult parseRow(HSSFRow row, int rowPoz) {

        ParseResult result = new ParseResult();

        {// ячейка id
            HSSFCell idCell = row.getCell(0);
            if (idCell == null) {
                result.addError("\t0001: Неправильный формат ячейки (строка:" + (rowPoz + 1) + ", колонка:1)\n");
            } else {
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
                            result.addError("неправильный id:\"" + idText + '\"');
                        }
                        break;
                    default:
                        result.addError("\t0002: Неправильный формат ячейки (строка:" + (rowPoz + 1) + ", колонка:1)\n");
                }

                if (0 < id && id < 9999999999L) {
                    result.moskvenokId = id;
                } else {
                    result.addError("\t0003: Неправильный формат ячейки (строка:" + (rowPoz + 1) + ", колонка:1)\n");
                }
            }
        }

        {// имя
            HSSFCell nameCell = row.getCell(1);
            if (nameCell == null) {
                result.name = "-";
            } else {
                switch (nameCell.getCellType()) {
                    case 0://NUMERIC(0)
                        result.name = Double.toString(nameCell.getNumericCellValue());
                        break;
                    case 1://STRING
                        result.name = nameCell.getStringCellValue().trim();
                        break;
                    case -1:// _NONE(-1)
                    case 2:// FORMULA(2)
                    case 3:// BLANK(3)
                    case 4:// BOOLEAN(4)
                    case 5:// ERROR(5)
                        result.form = "-";// todo
                        break;
                    default:
                        result.addError("\t0004: Неправильный формат ячейки (строка:" + (rowPoz + 1) + ", колонка:2)\n");
                }
            }
        }

        {// класс
            HSSFCell classCell = row.getCell(2);
            if (classCell == null) {
                result.form = "-";
            } else {
                Log.e("TAG", "classCell.getCellType()=" + classCell.getCellType());
                switch (classCell.getCellType()) {
                    case 0://NUMERIC(0)
                        result.form = Double.toString(classCell.getNumericCellValue());
                        break;
                    case 1://STRING
                        result.form = classCell.getStringCellValue().trim();
                        break;
                    case -1:// _NONE(-1)
                    case 2:// FORMULA(2)
                    case 3:// BLANK(3)
                    case 4:// BOOLEAN(4)
                    case 5:// ERROR(5)
                        result.form = "-";// todo
                        break;
                    default:
                        result.addError("\t0005: Неправильный формат ячейки (строка:" + (rowPoz + 1) + ", колонка:3)\n");
                }
            }
        }

        return result;
    }
}


class ParseResult {

    private int error = 0;
    private StringBuilder errorCodes;
    long moskvenokId;
    String name;
    String form;

    public ParseResult(long moskvenokId, String name, String form) {
        this.moskvenokId = moskvenokId;
        this.name = name;
        this.form = form;
        errorCodes = new StringBuilder();
    }

    public ParseResult() {
        errorCodes = new StringBuilder();
    }

    public void addError(String errorCode) {
        error++;
        this.errorCodes.append(errorCode).append('\n').append(' ');
    }

    public int getErrorsCount() {
        return error;
    }

    public String getErrorCodes() {
        return errorCodes.toString();
    }
}

class Student {
    long moskvenokId;
    String name;
    String group;
//    String surname;
//    String last_name;

    @NonNull
    @Override
    public String toString() {
        return "{id =" + moskvenokId + ", name = " + name + ", group = " + group + "}";
    }

    /*
     * пришлось сделать id москвенок string так как в int он не влазит
     * */

    public Student(long id, String name, String group) {
        this.moskvenokId = id;
        this.name = name;
        this.group = group;
    }
}