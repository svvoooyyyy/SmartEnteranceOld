package com.example.schoolhelper;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;

public class MainActivity extends AppCompatActivity {

    //Вот так вот
    //Shared preferences
    public static final String PREF_USER_MODE = "user_mode";

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
        SharedPreferences preferences = getPreferences(Context.MODE_PRIVATE);
        int userMode = preferences.getInt(PREF_USER_MODE, -1);
        switch (userMode) {
            case 0: { // ученик
                Intent intent = new Intent(MainActivity.this, StudentMainPage.class);
                startActivity(intent);
                break;
            }
            case 1: { // учитель
                Intent intent = new Intent(MainActivity.this, TeacherMainPage.class);
                startActivity(intent);
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
                SharedPreferences preferences = getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();



                if (mainActivityRadioButtonTeacher.isChecked()) {
                    editor.putInt(PREF_USER_MODE, 1);
                    intent = new Intent(MainActivity.this, TeacherMainPage.class);
                } else {
                    editor.putInt(PREF_USER_MODE, 0);
                    intent = new Intent(MainActivity.this, StudentMainPage.class);
                }
                editor.apply();
                startActivity(intent);
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {//кнопка назад в actionBar
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
