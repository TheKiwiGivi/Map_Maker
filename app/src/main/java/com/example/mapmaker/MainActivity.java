package com.example.mapmaker;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {

   //string consts
   public static final String MAP = "map";
   public static final String SAVE_FILE = "savefile";
   public static final String WIDTH = "width";
   public static final String HEIGHT = "height";
   public static final String PREFS_WIDTH = "prefswidth";
   public static final String PREFS_HEIGHT = "prefsheight";
   public static final String ALL_FILES = "allfiles";
   public static final String FILE_COUNTER = "filecounter";
   public static final String LOADED_MAP = "loadedmap";
   public static final String SELECTED_MAP = "selectedmap";
   public static final String UPDATE = "update";
   public static final String RETURN = "return";
   public static final String REMOVED_MAP = "removedmap";
   public static final String SETTING_HEIGHT = "settingheight";
   public static final String SETTING_WIDTH = "settingwidth";
   public static final String CREATE_HEIGHT = "createheight";
   public static final String CREATE_WIDTH = "createwidth";
   public static final String SHARED_PREFERENCES = "sharedpreferences";

    //for debugging
    private static final String TAG = "MainActivity";

    //checks if app was just opened or just returning from another
    private boolean justEntered = true;

    //used to load specific map
    private String loadedMap = "";

    //buttons
    private Button btn_createMap;
    private Button btn_loadMap;
    private Button btn_settings;

    //default map size when creating new map
    private int defaultHeight = 5;
    private int defaultWidth = 5;

    //intent codes
    public int createMapCode = 0;
    public int loadMapCode = 1;
    public int settingsCode = 2;

    //shows map default sizes
    private TextView tw_dimensions;

    //all file(map) names
    private String fileNames[];
    //amount of maps
    private int fileCounter = 0;

    //file containing all file names used
    private String saveFile = "map_names2";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //loading shared preferences
        loadData();

        //set default map sizes
        tw_dimensions = findViewById(R.id.tw_dimensions);
        showDimensions();

        //init and loading maps
        fileNames = new String[50];
        loadFiles();

        //button starts activity to create new map
       btn_createMap = findViewById(R.id.btn_createMap);
       btn_createMap.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Intent intent = new Intent(MainActivity.this, CreateMapActivity.class);

               intent.putExtra(CREATE_HEIGHT, defaultHeight);
               intent.putExtra(CREATE_WIDTH, defaultWidth);

               startActivityForResult(intent, createMapCode);
           }
       });

        //button starts activity to load pre-made maps
        btn_loadMap = findViewById(R.id.btn_loadMap);
        btn_loadMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LoadMapActivity.class);
                intent.putExtra(ALL_FILES, fileNames);
                intent.putExtra(FILE_COUNTER, fileCounter);

                startActivityForResult(intent, loadMapCode);
            }
        });

        //button starts activity to change preferences
        btn_settings = findViewById(R.id.btn_settings);
        btn_settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                intent.putExtra(SETTING_HEIGHT, defaultHeight);
                intent.putExtra(SETTING_WIDTH, defaultWidth);
                startActivityForResult(intent, settingsCode);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //making sure only handling results from wanted activities
        if (requestCode != createMapCode && requestCode != loadMapCode && requestCode != settingsCode) return;
        //checks if okay
        if (resultCode != RESULT_OK) return;

        //handling result from 'CreateMapActivity'
        if(requestCode == createMapCode){
            //new file name
            String fileName = data.getStringExtra(SAVE_FILE);
            //file info
            int map[] = data.getIntArrayExtra(MAP);
            //map dimensions
            int height = data.getIntExtra(HEIGHT, 1);
            int width = data.getIntExtra(WIDTH, 1);

            //checks if wanting to create new file
            if(!data.getBooleanExtra(UPDATE, false)) {
                //create .txt file
                createFile(fileName, map, height, width);
            } 
            else {
                //updating pre-made file
                //this is done by removing the file and replacing it with new info
                String temp[] = new String[50];
                int tempCounter = 0;
                int copies = 0;
                for(int i = 0; i < fileCounter; i++){

                    if(!fileNames[i].contentEquals(fileName)){
                        temp[tempCounter++] = fileNames[i];
                    } else {
                        //in case multiple files with same name, removes all
                        copies++;
                    }
                }
                //decrease amount of files by amount of copies
                fileCounter -= copies;
                fileNames = temp;
                deleteFile(fileName);
                createFile(fileName, map, height, width);
            }
        }
        //handling result from 'LoadMapActivity'
        else if (requestCode == loadMapCode){

            //if a request for deleting a file, returns here to let 'MainActivity' handle the logic, then returns
            if(data.getBooleanExtra(RETURN, false)){

                //index for file to be removed
                int position = data.getIntExtra(REMOVED_MAP, 0);

                //same logic as for updating file, except not re-adding it
                String temp[] = new String[50];
                int tempCounter = 0;

                for(int i = 0; i < fileCounter; i++){
                    if(i != position){
                        temp[tempCounter++] = fileNames[i];
                    }
                }
                deleteFile(fileNames[position]);
                fileNames = temp;
                fileCounter--;

                //return to 'LoadMapActivity'
                Intent intent = new Intent(MainActivity.this, LoadMapActivity.class);
                intent.putExtra(ALL_FILES, fileNames);
                intent.putExtra(FILE_COUNTER, fileCounter);

                startActivityForResult(intent, loadMapCode);
            }
            else {
                //map has been selected, so we proceed to 'CreateMapActivity' to edit it
                loadedMap = data.getStringExtra(SELECTED_MAP);

                Intent intent = new Intent(MainActivity.this, CreateMapActivity.class);
                intent.putExtra(LOADED_MAP, loadedMap);
                intent.putExtra(CREATE_HEIGHT, defaultHeight);
                intent.putExtra(CREATE_WIDTH, defaultWidth);
                startActivityForResult(intent, createMapCode);
            }
        }
        //handling result from 'SettingsActivity'
        else {
            //sets new default values
            defaultHeight = data.getIntExtra(SETTING_HEIGHT, 1);
            defaultWidth = data.getIntExtra(SETTING_WIDTH, 1);

            //save preferences
            saveData();
            //displays new dimensions
            showDimensions();
        }
        //loading all files
        loadFiles();
    }

    //creates a new file with given info
    private void createFile(String fileName, int map[], int height2, int width2){
        //set file name
        fileNames[fileCounter++] = fileName;
        //the files start with the dimensions
        String fileContents = Integer.toString(height2) + " " + Integer.toString(width2) + "\n";

        //adds all the tiles to the content of the file
        for(int i = 0; i < height2; i++){
            for(int j = 0; j < width2; j++){
                int nr = (i*width2)+j;
                fileContents += Integer.toString(map[nr]) + " ";
            }
            fileContents += "\n";
        }
        //used to write to the file
        FileOutputStream outputStream;

        try {
            //private to not let other apps use it. probably not that important
            outputStream = openFileOutput(fileName, Context.MODE_PRIVATE);
            outputStream.write(fileContents.getBytes());

            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //create/read from/update file containing all map names
    public void loadFiles(){
        //if not first time entered, write all file names to save file
        if(!justEntered) {
            FileOutputStream outputStream2;
            try {
                outputStream2 = openFileOutput(saveFile, Context.MODE_PRIVATE);
                String nr = Integer.toString(fileCounter);
                outputStream2.write(nr.getBytes());
                String k = "\n";
                outputStream2.write(k.getBytes());

                //wiring all file names to save file
                for (int i = 0; i < fileCounter; i++) {
                    outputStream2.write(fileNames[i].getBytes());
                    outputStream2.write(k.getBytes());
                }
                outputStream2.close();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //else read from / make save file
        else {
            justEntered = false;

            try {
                FileInputStream fis = openFileInput(saveFile);
                InputStreamReader isr = new InputStreamReader(fis);
                BufferedReader br = new BufferedReader(isr);
                String text;
                text = br.readLine();
                fileCounter = Integer.parseInt(text);

                for (int i = 0; i < fileCounter; i++) {
                    text = br.readLine();
                    fileNames[i] = text;
                }

                fis.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                //if save file does not exist, create it (empty)
                FileOutputStream outputStream;
                try {
                    outputStream = openFileOutput(saveFile, Context.MODE_PRIVATE);
                    String info = "0";
                    outputStream.write(info.getBytes());
                    outputStream.close();
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    //save prefs
    public void saveData(){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putInt(PREFS_HEIGHT, defaultHeight);
        editor.putInt(PREFS_WIDTH, defaultWidth);
        editor.commit();
    }

    //load prefs
    public void loadData(){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFERENCES, MODE_PRIVATE);

        defaultHeight = sharedPreferences.getInt(PREFS_HEIGHT, 1);
        defaultWidth = sharedPreferences.getInt(PREFS_WIDTH, 1);
    }

    //show default map dimensions
    public void showDimensions(){
        tw_dimensions.setText("Current dimensions: "+defaultHeight+"-"+defaultWidth+"");
    }
}
