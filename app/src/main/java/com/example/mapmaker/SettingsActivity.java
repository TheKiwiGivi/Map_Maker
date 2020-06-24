package com.example.mapmaker;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

public class SettingsActivity extends AppCompatActivity {

    //edittexts
    private EditText et_height;
    private EditText et_width;

    //map dimensions
    private int currentHeight = 0;
    private int currentWidth = 0;

    //save button
    private Button btn_savePrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //gets default values from 'MainActivity'
        currentHeight = getIntent().getIntExtra(MainActivity.SETTING_HEIGHT, 0);
        currentWidth = getIntent().getIntExtra(MainActivity.SETTING_WIDTH, 0);

        //finds and sets edittexts
        et_height = findViewById(R.id.et_height);
        et_height.setHint(Integer.toString(currentHeight));

        et_width = findViewById(R.id.et_width);
        et_width.setHint(Integer.toString(currentWidth));

        //finds and sets button
        btn_savePrefs = findViewById(R.id.btn_savePrefs);
        btn_savePrefs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //new default dimensions
                int tempHeight = 0;
                int tempWidth = 0;
                try {
                    tempHeight = Integer.parseInt(et_height.getText().toString());
                    tempWidth = Integer.parseInt(et_width.getText().toString());
                } catch (NumberFormatException e) {
                    //not integer
                }

                //dimensions needs to be atleast 1 1 to be updated
                if(tempHeight > 0 && tempWidth > 0) {
                    //values updated and returns to 'MainActivity'
                    Intent back = new Intent(SettingsActivity.this, MainActivity.class);
                    back.putExtra(MainActivity.SETTING_HEIGHT, tempHeight);
                    back.putExtra(MainActivity.SETTING_WIDTH, tempWidth);
                    setResult(Activity.RESULT_OK, back);
                    finish();
                }
                else {
                    //error message
                    Toast.makeText(SettingsActivity.this, "Map dimensions are invalid.", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

}
