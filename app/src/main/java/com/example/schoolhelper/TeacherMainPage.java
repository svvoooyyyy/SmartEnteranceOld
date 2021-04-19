package com.example.schoolhelper;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.google.zxing.Result;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.formula.functions.T;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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


    private ArrayList<Student> authorisedStudentsData;
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
                // todo wtf
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

        // получаем данные
        authorisedStudentsData = new ArrayList<>(5);
        authorisedStudentsData.add(new Student(0000000000L, "Вася1 Пупкин Алексеевич", "7a"));
        authorisedStudentsData.add(new Student(0000000001L, "Вася2 Пупкин Алексеевич", "7a"));
        authorisedStudentsData.add(new Student(0000000002L, "Вася3 Пупкин Алексеевич", "7a"));
        authorisedStudentsData.add(new Student(123456789L, "Вася4 Пупкин Алексеевич", "7a"));
        studentsList = new LinkedList<>();


        // настройка view
        learnersOut = findViewById(R.id.activity_teacher_main_page_learners_out);


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

        SaveThread thread = new SaveThread();
        thread.start();

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

        int pos = -1;
        {
            int currentPos = 0;
            Iterator<Student> iterator = authorisedStudentsData.iterator();
            while (iterator.hasNext() && pos == -1) {
                Student temp = iterator.next();
                if (temp.id == id) pos = currentPos;
                currentPos++;
            }
        }

        View view = getLayoutInflater().inflate(R.layout.out_list_element, learnersOut);
        TextView nameTextView = ((TextView) view.findViewById(R.id.out_list_element_text_name));
        nameTextView.setText("name");
        TextView idTextView = ((TextView) view.findViewById(R.id.out_list_element_text_id));
        idTextView.setText("id");

        view.findViewById(R.id.out_list_element_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(TeacherMainPage.this, "fsgsdhdft", Toast.LENGTH_LONG).show();
            }
        });


        // создание записи
        final EnteredUnit enteredUnit;
        if (pos == -1) {
            nameTextView.setText(String.format("%010d", id));
            enteredUnit = new EnteredUnit(System.currentTimeMillis(), null, id, nameTextView);
            Toast.makeText(
                    TeacherMainPage.this,
                    "ученик не найден в базе",
                    Toast.LENGTH_SHORT
            ).show();

        } else {
            enteredUnit = new EnteredUnit(System.currentTimeMillis(), authorisedStudentsData.get(pos), id, nameTextView);
            nameTextView.setText(authorisedStudentsData.get(pos).name);


        }
        studentsList.add(enteredUnit);


        // нажатие на текст
        nameTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // удаляем элемент отовсюду
                learnersOut.removeView(enteredUnit.textView);
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
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Сводка за");//TODO
        //sharingIntent.putExtra(Intent.EXTRA_TEXT, "");

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
            Cell[] cells = new Cell[6];
            for (int i = 0; i < cells.length; i++) cells[i] = row.createCell(i);
            cells[0].setCellValue("Дата");
            cells[1].setCellValue("Время");
            cells[2].setCellValue("ФИО");
            cells[3].setCellValue("Группа");
            cells[4].setCellValue("Событие");
            cells[5].setCellValue("Корпус посещения1");
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
            Cell[] cells = new Cell[6];
            for (int i = 0; i < cells.length; i++) cells[i] = row.createCell(i);
            cells[0].setCellValue(dateFormat.format(point));
            cells[1].setCellValue(timeFormat.format(point));
            if (current.student == null) {
                cells[2].setCellValue("-");
                cells[3].setCellValue("-");
            } else {
                cells[2].setCellValue(current.student.name);
                cells[3].setCellValue(current.student.group);
                //DataFormat format = openedBook.createDataFormat();
                //        CellStyle dateStyle = openedBook.createCellStyle();
                //        dateStyle.setDataFormat(format.getFormat("(ss.MM.hh) dd.mm.yyyy"));
                //        birthdate.setCellStyle(dateStyle);
            }
            cells[4].setCellValue("вход");
            cells[5].setCellValue("ГБОУ Школа № 1852");// TODO


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

class Student {
    long id;
    String name;
    String group;
//    String surname;
//    String last_name;

    /*
     * пришлось сделать id москвенок string так как в int он не влазит
     * */

    public Student(long id, String name, String group) {
        this.id = id;
        this.name = name;
        this.group = group;
    }
}

class EnteredUnit {
    long unixTimePoint;
    Student student;
    long enteredId;
    TextView textView;

    public EnteredUnit(long unixTimePoint, Student student, long enteredId, TextView textView) {
        this.unixTimePoint = unixTimePoint;
        this.student = student;
        this.enteredId = enteredId;
        this.textView = textView;
    }

}


