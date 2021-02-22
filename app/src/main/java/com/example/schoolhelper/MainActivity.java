package com.example.schoolhelper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;

public class MainActivity extends AppCompatActivity {

    //Вот так вот
    //Shared preferences
    String MOSKVENOK_CARD_ID_PREF = "card_id";

    // variables
    EditText main_activity_edit_text;
    Button main_activity_button_enter;
    RadioButton main_activity_radio_button_teacher;
    RadioButton main_activity_radio_button_student;
    Boolean is_teacher = false;
    


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // finding view
        main_activity_edit_text = findViewById(R.id.main_activity_edit_text);
        main_activity_button_enter = findViewById(R.id.main_activity_button_enter);
        main_activity_radio_button_student = findViewById(R.id.main_activity_radio_button_student);
        main_activity_radio_button_teacher = findViewById(R.id.main_activity_radio_button_teacher);

        // on click listeners

        // teacher
        main_activity_radio_button_teacher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (main_activity_radio_button_teacher.isChecked()){
                    is_teacher = true;
                    main_activity_radio_button_student.setChecked(false);
                } else {
                    main_activity_radio_button_teacher.setChecked(true);
                    main_activity_radio_button_student.setChecked(false);
                }
            }
        });

        // student
        main_activity_radio_button_student.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = main_activity_edit_text.getText().toString();
                SharedPreferences preferences = getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString(MOSKVENOK_CARD_ID_PREF, str);
                editor.apply();
                if (main_activity_radio_button_student.isChecked()){
                    is_teacher = false;
                    main_activity_radio_button_teacher.setChecked(false);
                } else {
                    main_activity_radio_button_student.setChecked(true);
                    main_activity_radio_button_teacher.setChecked(false);
                }
            }
        });

        // enter button
        main_activity_button_enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                if (is_teacher) {
                    intent = new Intent(MainActivity.this, TeacherMainPage.class);
                } else {
                    intent = new Intent(MainActivity.this, StudentMainPage.class);
                }
                startActivity(intent);
            }
        });

    }
}
