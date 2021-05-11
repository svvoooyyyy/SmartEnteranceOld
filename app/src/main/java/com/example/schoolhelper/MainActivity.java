package com.example.schoolhelper;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {


    //Shared preferences
    public static final String PREF_USER_MODE = "user_mode";
    // Вот так вот
    private static final int ACTIVITY_BACK_CODE = 123;
    public static final int RESULT_BACK = 321;
    public static final int RESULT_NULL = 0;

    // variables
    Button mainActivityButtonEnter;
    RadioButton mainActivityRadioButtonTeacher;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // finding view
        mainActivityButtonEnter = findViewById(R.id.main_activity_button_enter);
        mainActivityRadioButtonTeacher = findViewById(R.id.main_activity_radio_button_teacher);


        // получение данных
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        int userMode = preferences.getInt(PREF_USER_MODE, -1);
        Log.e("TAG", "onCreate: " + preferences.getLong(StudentSettingsActivity.PREF_ID, -1L) );
        switch (userMode) {
            case -1:
                break;
            case 0: { // ученик
                Intent intent = new Intent(MainActivity.this, StudentMainPage.class);
                startActivityForResult(intent, ACTIVITY_BACK_CODE);
                break;
            }
            case 1: { // учитель
                Intent intent = new Intent(MainActivity.this, TeacherMainPage.class);
                startActivityForResult(intent, ACTIVITY_BACK_CODE);
                break;
            }
            default: // тип записи не выбран
        }

        // enter button
        mainActivityButtonEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;

                // сохранение данных
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = preferences.edit();

                if (mainActivityRadioButtonTeacher.isChecked()) {
                    editor.putInt(PREF_USER_MODE, 1);
                    intent = new Intent(MainActivity.this, TeacherMainPage.class);
                } else {
                    editor.putInt(PREF_USER_MODE, 0);
                    intent = new Intent(MainActivity.this, StudentMainPage.class);
                }
                editor.apply();
                startActivityForResult(intent, ACTIVITY_BACK_CODE);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_BACK && requestCode == ACTIVITY_BACK_CODE) {
            finish();
        }
    }
}
