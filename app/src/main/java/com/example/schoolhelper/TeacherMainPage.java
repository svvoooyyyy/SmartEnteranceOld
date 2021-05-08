package com.example.schoolhelper;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.example.schoolhelper.TeacherSettings.TeacherSettingsActivity;
import com.example.schoolhelper.data_base.Contract;
import com.example.schoolhelper.data_base.DataBaseOpenHelper;
import com.google.zxing.Result;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Locale;

public class TeacherMainPage extends AppCompatActivity {


    private static final int ACTIVITY_BACK_CODE = 20;
    public static final int RESULT_CLEAR_DATA = 81;
    public static final int RESULT_UPDATE = 82;
    // имя файла
    public static final String FILE_NAME = "saved_data.xls";
    private static final String TAG = "tag";
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
    private final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());


    //private ArrayList<Student> authorisedStudentsData;
    private LinkedList<EnteredUnit> studentsList;


    private CodeScanner mCodeScanner;
    private LinearLayout learnersOut;
    Menu menu;

    // тосты из сторонних потоков
    Handler toastHandler;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.teacher_menu, menu);
        this.menu = menu;
        return super.onCreateOptionsMenu(menu);
    }


    boolean isSaveProcess = false;

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.activity_settings: {
                Intent intent = new Intent(TeacherMainPage.this, TeacherSettingsActivity.class);
                startActivityForResult(intent, ACTIVITY_BACK_CODE);
                break;
            }
            case R.id.activity_share: {
                if (!isSaveProcess) {
                    isSaveProcess = true;
                    shareContent();
                    isSaveProcess = false;
                }
                break;
            }
            case R.id.activity_clear: {
                // удаляем записи о вошедших учениках

                studentsList.clear();

                DataBaseOpenHelper db = new DataBaseOpenHelper(this);
                db.clearEnteredStudentsList();
                db.close();

                learnersOut.removeAllViews();

                break;
            }
        }

        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_main_page);
        Toolbar toolbar = findViewById(R.id.base_toolbar);
        setSupportActionBar(toolbar);

        // тосты из сторонних потоков
        toastHandler = new Handler(getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                if (msg.what == 112)
                    Toast.makeText(TeacherMainPage.this, (String) msg.obj, Toast.LENGTH_SHORT).show();
            }
        };

        studentsList = new LinkedList<>();


        // настройка view
        learnersOut = findViewById(R.id.activity_teacher_main_page_learners_out);


        // Реализация работы с камерой

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

                            long code = -1;
                            boolean errorFlag = false;

                            try {
                                code = Long.parseLong(result.getText().trim());
                                if (code < 0 || code > 9999999999L) errorFlag = true;

                            } catch (NumberFormatException e) {
                                errorFlag = true;
                            }

                            if (errorFlag) {
                                Toast.makeText(
                                        TeacherMainPage.this,
                                        "неверный QR код: \"" + result.getText() + "\"",
                                        Toast.LENGTH_LONG
                                ).show();
                            } else if (code != -1) {
                                enterStudent(code);
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


        // пробегаемся по введенным ученикам
        DataBaseOpenHelper db = new DataBaseOpenHelper(this);
        Cursor enteredStudents = db.getEnteredStudents();
        studentsList.clear();
        while (enteredStudents.moveToNext()) {
            // контейнер элемента
            RelativeLayout container = new RelativeLayout(this);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            learnersOut.addView(container, 0, layoutParams);

            View view = getLayoutInflater().inflate(R.layout.out_list_element, container);
            RelativeLayout relativeLayout = view.findViewById(R.id.out_relative_layout);
            TextView nameTextView = view.findViewById(R.id.out_list_element_text_name);
            nameTextView.setText("name");// todo в текстовые файлы
            TextView idTextView = view.findViewById(R.id.out_list_element_text_id);
            idTextView.setText("id");// todo в текстовые файлы


            // находим сведения об ученике
            long enteredAttitudeId = enteredStudents.getLong(enteredStudents.getColumnIndex(
                    Contract.TableEnteredLearners.KEY_AUTHORIZED_PROFILE_ID
            ));
            long enteredMoskvenokId = enteredStudents.getLong(enteredStudents.getColumnIndex(
                    Contract.TableEnteredLearners.COLUMN_ENTERED_MOSKVENOK_ID
            ));
            Cursor student = db.getAuthorizedStudentById(enteredAttitudeId);


            // заполняем данными
            final EnteredUnit enteredUnit;
            if (student.getCount() == 0) {
                nameTextView.setText("Ученик не найден в базе");// todo в текстовые файлы
                nameTextView.setTextColor(getResources().getColor(R.color.colorError));
                idTextView.setText(String.format(Locale.getDefault(), "%010d", enteredMoskvenokId));
                enteredUnit = new EnteredUnit(
                        System.currentTimeMillis(),
                        enteredAttitudeId,
                        enteredMoskvenokId,
                        "-", "-",
                        container
                );
            } else {
                student.moveToFirst();
                enteredUnit = new EnteredUnit(System.currentTimeMillis(),
                        enteredAttitudeId,
                        enteredMoskvenokId,
                        student.getString(student.getColumnIndex(Contract.TableAuthorizedLearners.COLUMN_NAME)),
                        student.getString(student.getColumnIndex(Contract.TableAuthorizedLearners.COLUMN_FORM)),
                        container
                );
                String name = student.getString(student.getColumnIndex(Contract.TableAuthorizedLearners.COLUMN_NAME));
                nameTextView.setText(
                        (name.length() > 15) ? (name.substring(0, 20) + "...") : (name)
                );
                idTextView.setText(String.format(Locale.getDefault(), "%010d", enteredMoskvenokId));
            }
            studentsList.add(enteredUnit);
        }
        enteredStudents.close();

        // остальное
        setResult(MainActivity.RESULT_BACK, null);

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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ACTIVITY_BACK_CODE) {
            if (resultCode == RESULT_CLEAR_DATA) {
                setResult(MainActivity.RESULT_NULL, null);
                finish();
            } else if (resultCode == RESULT_UPDATE) {
                //updateFields(); todo ване
            }
        }

    }

    // -------------------------- вспомогательные методы --------------------------


    // ввод студента
    private void enterStudent(long id) {

        // контейнер элемента
        RelativeLayout container = new RelativeLayout(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        learnersOut.addView(container, 0, layoutParams);

        View view = getLayoutInflater().inflate(R.layout.out_list_element, container);
        TextView nameTextView = view.findViewById(R.id.out_list_element_text_name);
        nameTextView.setText("name");// todo в текстовые файлы
        TextView idTextView = view.findViewById(R.id.out_list_element_text_id);
        idTextView.setText("id");// todo в текстовые файлы


        DataBaseOpenHelper db = new DataBaseOpenHelper(this);
        Cursor students = db.getAuthorizedStudentByMoskvenokId(id);

        // создание записи
        final EnteredUnit enteredUnit;
        if (students.getCount() == 0) {
            nameTextView.setText("Ученик не найден в базе");// todo в текстовые файлы
            nameTextView.setTextColor(getResources().getColor(R.color.colorError));
            idTextView.setText(String.format(Locale.getDefault(), "%010d", id));
            // создаем запись о том что ввели ученика
            long enteredAttitudeId = db.createEnteredStudent(-1, id, new Date());
            // сохраняем ссылку на запись в лист
            enteredUnit = new EnteredUnit(
                    System.currentTimeMillis(),
                    enteredAttitudeId,
                    id,
                    "-", "-",
                    container
            );
        } else {
            students.moveToFirst();
            // создаем запись о том что ввели ученика и сохраняем в нее id записи ученика
            long enteredAttitudeId = db.createEnteredStudent(
                    students.getLong(students.getColumnIndex(Contract.TableAuthorizedLearners.KEY_STUDENT_PROFILE_ID)),
                    id, new Date());
            // сохраняем ссылку на запись в лист
            enteredUnit = new EnteredUnit(
                    System.currentTimeMillis(),
                    enteredAttitudeId,
                    id,
                    students.getString(students.getColumnIndex(Contract.TableAuthorizedLearners.COLUMN_NAME)),
                    students.getString(students.getColumnIndex(Contract.TableAuthorizedLearners.COLUMN_FORM)),
                    container
            );
            String name = students.getString(students.getColumnIndex(Contract.TableAuthorizedLearners.COLUMN_NAME));
            nameTextView.setText(
                    (name.length() > 15) ? (name.substring(0, 20) + "...") : (name)
            );
            idTextView.setText(String.format(Locale.getDefault(), "%010d", id));
        }
        studentsList.add(enteredUnit);
        students.close();
        db.close();

        // нажатие на текст
        view.findViewById(R.id.out_list_element_button_delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // удаляем запись о том что ученик вошел
                // из разметки
                learnersOut.removeView(enteredUnit.relativeLayout);
                // из бд
                DataBaseOpenHelper db = new DataBaseOpenHelper(TeacherMainPage.this);
                db.deleteEnteredStudentByRecordId(enteredUnit.enteredAttitudeId);
                db.close();
                // из листа
                studentsList.remove(enteredUnit);
            }
        });
    }


    // поделиться
    private void shareContent() {

        // сохраняем текущие данные в файл
        saveEXELDataInXlsFile();

        // отправляем файл
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        sharingIntent.setType("*/*");
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Сводка за " + dateFormat.format(new Date()));
        //sharingIntent.putExtra(Intent.EXTRA_TEXT, ""); текст сообщения

        // находим свой каталог для файла
        File sdPath = new File(this.getFilesDir().getAbsolutePath());
        // формируем объект File, который находится в sdPath
        File sdFile = new File(sdPath, FILE_NAME);

        sharingIntent.putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(
                this, "com.example.schoolhelper.export_data_provider", sdFile
        ));
        startActivity(Intent.createChooser(sharingIntent, "Отправить через: "));

    }


    // поток сохранения данных
    private class SaveThread extends Thread {
        @Override
        public void run() {
            super.run();

            saveEXELDataInXlsFile();

        }
    }

    private void saveEXELDataInXlsFile() {
        // генерируем новый файл EXEL
        Workbook openedBook = new HSSFWorkbook();
        Sheet sheet = openedBook.createSheet("Entrance Helper");


        {// шапка таблицы
            Row row = sheet.createRow(0);
            Cell[] cells = new Cell[7];
            for (int i = 0; i < cells.length; i++) cells[i] = row.createCell(i);
            cells[0].setCellValue("Id москвёнок");
            cells[1].setCellValue("Дата");
            cells[2].setCellValue("Время");
            cells[3].setCellValue("ФИО");
            cells[4].setCellValue("Группа");
            cells[5].setCellValue("Событие");
            cells[6].setCellValue("Корпус посещения");
        }

        // тело таблицы
        int excelRowPoz = 1;
        Iterator<EnteredUnit> iterator = studentsList.iterator();
        while (iterator.hasNext()) {
            // событие прохода
            EnteredUnit current = iterator.next();

            Date point = new Date(current.unixTimePoint);


            // строка
            Row row = sheet.createRow(excelRowPoz);
            Cell[] cells = new Cell[7];
            for (int i = 0; i < cells.length; i++) cells[i] = row.createCell(i);

            cells[0].setCellType(CellType.STRING);
            cells[0].setCellValue(current.moskvenokId);

            cells[1].setCellValue(dateFormat.format(point));
            cells[2].setCellValue(timeFormat.format(point));
            if (current == null) {
                cells[3].setCellValue("-");
                cells[4].setCellValue("-");
            } else {
                cells[3].setCellValue(current.name);
                cells[4].setCellValue(current.group);
                //DataFormat format = openedBook.createDataFormat();
                //        CellStyle dateStyle = openedBook.createCellStyle();
                //        dateStyle.setDataFormat(format.getFormat("(ss.MM.hh) dd.mm.yyyy"));
                //        birthdate.setCellStyle(dateStyle);
            }
            cells[5].setCellValue("вход");// TODO в тексты
            cells[6].setCellValue("ГБОУ Школа № 1852");// TODO в тексты / корпус??


            excelRowPoz++;
        }

        // Записываем всё в файл
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = openFileOutput(FILE_NAME, MODE_PRIVATE);
            openedBook.write(fileOutputStream);

            Message message = new Message();
            message.what = 112;
            message.obj = "Данные сохранены";
            toastHandler.sendMessage(message);
        } catch (IOException e) {

            Message message = new Message();
            message.what = 112;
            message.obj = "Ошибка сохранения";
            toastHandler.sendMessage(message);
            e.printStackTrace();
        } finally {
            try {
                if (fileOutputStream != null)
                    fileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            if (openedBook != null)
                openedBook.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}

class EnteredUnit {
    long unixTimePoint;

    long enteredAttitudeId;

    long moskvenokId;
    String name;
    String group;

    RelativeLayout relativeLayout;


    public EnteredUnit(long unixTimePoint, long enteredAttitudeId, long moskvenokId, String name,
                       String group, RelativeLayout relativeLayout) {
        this.enteredAttitudeId = enteredAttitudeId;
        this.unixTimePoint = unixTimePoint;
        this.moskvenokId = moskvenokId;
        this.name = name;
        this.group = group;
        this.relativeLayout = relativeLayout;
    }

}


