package com.example.schoolhelper;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class StudentSettingsActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_settings);

        Button delete_button = findViewById(R.id.settings_delete_user_mode_button);
        delete_button.setOnClickListener(new View.OnClickListener() {
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
    }
}