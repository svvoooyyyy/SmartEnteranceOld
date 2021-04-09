package com.example.schoolhelper;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class StudentSettingsActivity extends AppCompatActivity {

    Button deleteButton;
    Button saveNameButton;
    Button saveSurnameButton;
    Button saveLastNameButton;
    Button saveIdButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_settings);

        saveNameButton = findViewById(R.id.student_settings_save_user_name_button);
        saveSurnameButton = findViewById(R.id.student_settings_save_user_surname_button);
        saveLastNameButton = findViewById(R.id.student_settings_save_user_last_name_button);
        saveIdButton = findViewById(R.id.student_settings_save_user_id_button);
        deleteButton= findViewById(R.id.student_settings_delete_user_mode_button);

//        сброс пользовательского режима
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences preferences = getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.remove(MainActivity.PREF_USER_MODE);
                editor.putInt(MainActivity.PREF_USER_MODE, -1);
                Log.e("TAG", "onClick: "+preferences.getInt(MainActivity.PREF_USER_MODE, -100));
                editor.apply();
                Log.e("TAG", "onClick: "+preferences.getInt(MainActivity.PREF_USER_MODE,-100));
            }
        });

        // сохранение имени по нажатию на кнопку
        saveNameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        // сохранение фамилии по нажатию на кнопку
        saveSurnameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        // сохранение отчества по нажатию на кнопку
        saveNameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        // сохранение id москвёнка по нажатию на кнопку
        saveNameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


    }
}