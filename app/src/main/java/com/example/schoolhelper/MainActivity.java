package com.example.schoolhelper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;

public class MainActivity extends AppCompatActivity {

    //Вот так вот
    //Shared preferences
    public static final String PREF_USER_MODE = "user_mode";

    // variables
    Button main_activity_button_enter;
    RadioButton main_activity_radio_button_teacher;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // finding view
        main_activity_button_enter = findViewById(R.id.main_activity_button_enter);
        main_activity_radio_button_teacher = findViewById(R.id.main_activity_radio_button_teacher);

        // enter button
        main_activity_button_enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                if (main_activity_radio_button_teacher.isChecked()) {
                    intent = new Intent(MainActivity.this, TeacherMainPage.class);
                } else {
                    intent = new Intent(MainActivity.this, StudentMainPage.class);
                }
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
