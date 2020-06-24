package com.example.mapmaker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.PersistableBundle;
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
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class CreateMapActivity extends AppCompatActivity {

    //map info
    private int mapHeight = 400;
    private int mapWidth = 400;
    private int height = 1;
    private int width = 1;
    private ImageButton[][] buttons;


    private int[] buttonVal;
    private int[] prevVal;

    //refresh/change size button
    private Button btn_refresh;
    //save/load button
    private Button btn_save;

    //change tile buttons
    private Button btn_spawnPoint;
    private Button btn_empty;
    private Button btn_wall;
    private Button btn_torch;
    private Button btn_chest;
    private Button btn_enemy;
    private Button btn_spike;

    //map dimension edittexts
    private EditText etMapX;
    private EditText etMapY;

    //check if loaded or new map
    private boolean loaded = false;

    //used to change spawn location
    int lastId = 0;
    //amount of buttons
    private int buttonCounter = 0;
    //where spawn point is
    private int currentSpawnPointId = -1;
    //tile currently selected for placing
    private String selectedTile = "1";
    //loaded map name
    private String loadedMap = "";

    //strings
    private static final String TAG = "MainActivity";
    private static final String SELECTED_TILE = "selectedtile";
    private static final String MAP_STATE = "mapstate";
    private static final String MAP_HEIGHT = "mapheight";
    private static final String MAP_WIDTH = "mapwidth";

    //layout for the map
    TableLayout tableLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_create_map);

        //set default dimensions (might change)
        height = getIntent().getIntExtra(MainActivity.CREATE_HEIGHT, 1);
        width = getIntent().getIntExtra(MainActivity.CREATE_WIDTH, 1);

        //checks if loading pre-made
        if(getIntent().getStringExtra(MainActivity.LOADED_MAP) != null) {
            loaded = true;
            loadedMap = getIntent().getStringExtra(MainActivity.LOADED_MAP);
        }
        //dimensions
        etMapX = findViewById(R.id.etMapX);
        etMapY = findViewById(R.id.etMapY);

        //all tiles
        //empty
        btn_empty = findViewById(R.id.btn_empty);
        btn_empty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedTile = "0";
                removeBorder();
                giveBorder(v);
            }
        });

        //wall
        btn_wall = findViewById(R.id.btn_wall);
        btn_wall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedTile = "1";
                removeBorder();
                giveBorder(v);
            }
        });

        //spawn point
        btn_spawnPoint = findViewById(R.id.btn_spawnPoint);
        btn_spawnPoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedTile = "6";
                removeBorder();
                giveBorder(v);
            }
        });

        //chest
        btn_chest = findViewById(R.id.btn_chest);
        btn_chest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedTile = "7";
                removeBorder();
                giveBorder(v);
            }
        });

        //enemy
        btn_enemy = findViewById(R.id.btn_enemy);
        btn_enemy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedTile = "8";
                removeBorder();
                giveBorder(v);
            }
        });

        //spike
        btn_spike = findViewById(R.id.btn_spike);
        btn_spike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedTile = "9";
                removeBorder();
                giveBorder(v);
            }
        });

        //the torch button works differently, changes direction each time placed
        btn_torch = findViewById(R.id.btn_torch);
        btn_torch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button b = (Button)v;
                String currentState = b.getText().toString();

                //change direction, update tile and button text
                if(selectedTile == "2" || selectedTile == "3" || selectedTile == "4" ||selectedTile == "5") {
                    if(selectedTile == "2"){
                        selectedTile = "3";
                        b.setText("Torch Right");
                    }
                    else if(selectedTile == "3"){
                        selectedTile = "4";
                        b.setText("Torch Down");
                    }
                    else if(selectedTile == "4"){
                        selectedTile = "5";
                        b.setText("Torch Left");

                    }
                    else if(selectedTile == "5"){
                        selectedTile = "2";
                        b.setText("Torch Up");
                    }
                }
                //coming from another button does not change the current direction
                else {
                    switch(currentState.toLowerCase()){
                        case "torch up": selectedTile = "2"; break;
                        case "torch right": selectedTile = "3"; break;
                        case "torch down": selectedTile = "4"; break;
                        case "torch left": selectedTile = "5"; break;
                    }
                    //removes all button borders and assigns it to current
                    removeBorder();
                    giveBorder(v);
                }
            }
        });
        removeBorder();
        //default selected tile is wall
        giveBorder(btn_wall);

        //refresh or change map dimensions
        btn_refresh = findViewById(R.id.btn_refresh);
        btn_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //clearing map
                int tempX = 0;
                int tempY = 0;
                boolean refreshing = false;
                try {
                    tempX = Integer.parseInt(etMapX.getText().toString());
                    tempY = Integer.parseInt(etMapY.getText().toString());
                } catch (NumberFormatException e) {
                    //not integer
                    refreshing = true;
                }

                //removes all buttons
                tableLayout.removeAllViews();
                if(tempX > 0 && tempY > 0){
                    width = tempX;
                    height = tempY;
                     Toast.makeText(CreateMapActivity.this, "Resizing with dimensions: "+width+"-"+height+".", Toast.LENGTH_SHORT).show();
                } else {
                    //if not dimensions specified, just clearing map (making everything empty)
                    if(refreshing) {
                          Toast.makeText(CreateMapActivity.this, "Refreshing", Toast.LENGTH_SHORT).show();
                    } else {
                         Toast.makeText(CreateMapActivity.this, "Not a valid map size", Toast.LENGTH_SHORT).show();
                    }
                }

                //refreshing
                refreshMap();
            }
        });

        //layout for map
        tableLayout = findViewById(R.id.tableLayoutMap);

        //if loading pre-made map, save new becomes update existing
        btn_save = findViewById(R.id.btn_save);
        if(loaded){
            btn_save.setText("Update");
        }
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(loaded){
                    //send update results
                    Intent back = new Intent(CreateMapActivity.this, MainActivity.class);
                    back.putExtra(MainActivity.UPDATE, true);
                    back.putExtra(MainActivity.SAVE_FILE, loadedMap);
                    back.putExtra(MainActivity.MAP, buttonVal);
                    back.putExtra(MainActivity.WIDTH, width);
                    back.putExtra(MainActivity.HEIGHT, height);

                    setResult(Activity.RESULT_OK, back);
                    finish();
                }
                else {
                    //creating new map. alertdialog tells you to type map name then creates new file
                    AlertDialog.Builder builder = new AlertDialog.Builder(CreateMapActivity.this);
                    View myView = getLayoutInflater().inflate(R.layout.save_file, null);
                    final EditText et_file__name = myView.findViewById(R.id.et_file_name);

                    builder.setView(myView);
                    final AlertDialog dialog = builder.create();
                    dialog.show();

                    //confirm save file button
                    Button btn_confirmSave = myView.findViewById(R.id.btn_confirm_save);
                    btn_confirmSave.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //default name is 'map'. if user adds something, it becomes 'map_x'
                            String fileName = "map";
                            if (!et_file__name.getText().toString().isEmpty()) {
                                fileName += "_" + et_file__name.getText().toString();
                                Toast.makeText(CreateMapActivity.this, "File " + fileName + " saved.", Toast.LENGTH_LONG).show();
                            }
                            fileName += ".txt";
                            dialog.hide();

                            //return file info to 'MainActivity'
                            Intent back = new Intent(CreateMapActivity.this, MainActivity.class);
                            back.putExtra(MainActivity.UPDATE, false);
                            back.putExtra(MainActivity.SAVE_FILE, fileName);
                            back.putExtra(MainActivity.MAP, buttonVal);
                            back.putExtra(MainActivity.WIDTH, width);
                            back.putExtra(MainActivity.HEIGHT, height);

                            setResult(Activity.RESULT_OK, back);
                            //make sure dialog gets stopped
                            dialog.dismiss();
                            finish();
                        }
                    });
                }
            }
        });
        //refreshing
        refreshMap();
        if(!loadedMap.isEmpty()) {
            //remove current map and load from file
            tableLayout.removeAllViews();
            loadMap();
        }
    }

    //saving current map info when screen flips
    @Override
    public void onSaveInstanceState(Bundle outState) {
        //save current selected tile
        outState.putString(SELECTED_TILE, selectedTile);
        //save current dimensions
        outState.putInt(MAP_HEIGHT, height);
        outState.putInt(MAP_WIDTH, width);

        //save map tiles
        outState.putIntArray(MAP_STATE, buttonVal);
        super.onSaveInstanceState(outState);
    }

    //loading map after screen has flipped
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        //set map info
        height = savedInstanceState.getInt(MAP_HEIGHT);
        width = savedInstanceState.getInt(MAP_WIDTH);
        tableLayout.removeAllViews();
        //is run to initialize vars for current map size
        refreshMap();

        //updating map tiles
        int[] mapState = savedInstanceState.getIntArray(MAP_STATE);

        for(int i = 0; i < height; i++) {
            for(int j = 0; j < width; j++){
                selectedTile = Integer.toString(mapState[(i * height) + j]);
                changeButtonImage(buttons[i][j], false);
            }
        }
        selectedTile = savedInstanceState.getString(SELECTED_TILE);
        removeBorder();
        giveBorderToCurrent();

    }

    //gives a button border
    private void giveBorder(View btn){
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.RECTANGLE);
        drawable.setStroke(8, Color.RED);

        btn.setBackgroundDrawable(drawable);
    }

    //removes border of all buttons
    private void removeBorder(){
        GradientDrawable drawable = new GradientDrawable();
        btn_torch.setBackgroundDrawable(drawable);
        btn_empty.setBackgroundDrawable(drawable);
        btn_spawnPoint.setBackgroundDrawable(drawable);
        btn_wall.setBackgroundDrawable(drawable);
        btn_chest.setBackgroundDrawable(drawable);
        btn_enemy.setBackgroundDrawable(drawable);
        btn_spike.setBackgroundDrawable(drawable);
    }

    //refreshing / initializing map
    private void refreshMap(){
        //reset all map info vars to current map dimensions
        buttonCounter = 0;
        buttons = new ImageButton[height][width];
        buttonVal = new int[height*width];
        prevVal = new int[height*width];
        //made to fit screen
        Double tempWidth = (mapWidth*2.5)/width;
        Double tempHeight = (mapHeight*2.25)/height;
        int w = tempWidth.intValue();
        int h = tempHeight.intValue();

        //the map is a table of imagebuttons
        for(int i = 0; i < height; i++) {
            //new line
            TableRow tr = new TableRow(this);

            for(int j = 0; j < width; j++) {
                //new button
                buttons[i][j] = new ImageButton(this);
                //all tiles initialized to empty (0)
                buttonVal[buttonCounter] = 0;

                //adding the 'empty' image
                Drawable dr = getResources().getDrawable(R.drawable.empty);
                Bitmap bitmap = ((BitmapDrawable) dr).getBitmap();
                Drawable d = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, mapWidth/width, mapHeight/height, true));

                //updating button values
                buttons[i][j].setImageDrawable(d);
                buttons[i][j].setId(buttonCounter++);
                buttons[i][j].setLayoutParams(new TableRow.LayoutParams(w, h));

                //if clicked, changes image
                buttons[i][j].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int cId = v.getId();
                        ImageButton ib = findViewById(cId);
                        changeButtonImage(ib, false);
                    }
                });
                //adds button to tablerow
                tr.addView(buttons[i][j]);
            }
            //adds tablerow to table
            tableLayout.addView(tr);
        }
    }

    //changes the image of a button to selected tile value
    public void changeButtonImage(ImageButton b, boolean removeSpawn){
        //current id
        int cId = b.getId();
        //last id used to change spawn point
        lastId = cId;
        String tile = selectedTile;

        //used to check if spawn point needs to be changed
        if(removeSpawn){
            tile = Integer.toString(prevVal[cId]);
        }
        //always keeps track of where last tile was placed, in order to change spawn point
        if(buttonVal[cId] != 6) {
            prevVal[cId] = buttonVal[cId];
        }


        try {
            buttonVal[cId] = Integer.parseInt(tile);
        } catch (NumberFormatException e) {
            //not integer
        }

        Drawable dr;

        //gets wanted image
        switch(tile) {
            case "0": dr = getResources().getDrawable(R.drawable.empty); break;
            case "1": dr = getResources().getDrawable(R.drawable.wall); break;
            case "2": dr = getResources().getDrawable(R.drawable.torch_up); break;
            case "3": dr = getResources().getDrawable(R.drawable.torch_right); break;
            case "4": dr = getResources().getDrawable(R.drawable.torch_down); break;
            case "5": dr = getResources().getDrawable(R.drawable.torch_left); break;
            case "6": if(!removeSpawn)checkSpawn(cId); dr = getResources().getDrawable(R.drawable.spawnpoint); break;
            case "7": dr = getResources().getDrawable(R.drawable.chest); break;
            case "8": dr = getResources().getDrawable(R.drawable.enemy); break;
            case "9": dr = getResources().getDrawable(R.drawable.spike); break;
            default: dr = getResources().getDrawable(R.drawable.empty);
        }

        //sets drawable
        Bitmap bitmap = ((BitmapDrawable) dr).getBitmap();
        //width and height to match screen
        Drawable d = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, mapWidth/width, mapHeight/height, true));
        b.setImageDrawable(d);
    }

    //logic for changing spawn point location
    public void checkSpawn(int buttonId){
        //if no current spawn point
        if(currentSpawnPointId == -1){
            currentSpawnPointId = buttonId;
            return;
        }
        //updates button to remove spawn point since there can only be one
       ImageButton b = findViewById(currentSpawnPointId);
       currentSpawnPointId = buttonId;
       changeButtonImage(b, true);
    }

    //loading pre-made map
    public void loadMap(){
        try {
            FileInputStream fis = openFileInput(loadedMap);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();

            //logic for reading map file. a little confusing so no need to understand how it works.
            //it just reads map dimensions and tiles from a file and sets it to variables used to create new map
            char o = ' ';
            char o2 = '\n';
            char k;

            int height2;
            String heightTemp = "";

            k = (char)br.read();
            heightTemp += k;
            k = (char)br.read();
            while(k != o && k != o2){
                heightTemp+=k;
                k = (char)br.read();
            }
            height2 = Integer.parseInt(heightTemp);
            height = height2;
            sb.append(height2).append(" ");

            int width2;
            String widthTemp = "";

            k = (char)br.read();
            widthTemp += k;
            k = (char)br.read();
            while(k != o && k != o2){
                widthTemp+=k;
                k = (char)br.read();
            }
            width2 = Integer.parseInt(widthTemp);
            width = width2;
            sb.append(width2).append("\n");

            refreshMap();

            char ik = ' ';

            for(int i = 0; i < height2; i++) {
                for(int j = 0; j < width2; j++) {

                    int ki = br.read();
                    while (ki == ' ' || ki == '\n') {
                        ki = br.read();
                    }
                    ik = (char) ki;
                    sb.append(ik).append(" ");
                    selectedTile = Character.toString(ik);
                    Log.d(TAG, "loadMap: "+selectedTile);

                    ImageButton ib = buttons[i][j];

                    changeButtonImage(ib, false);
                }
                sb.append("\n");
            }

            fis.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        selectedTile = "1";
    }

    //gives border to currently selected tile
    public void giveBorderToCurrent(){
        switch(selectedTile){
            case "0": giveBorder(btn_empty); break;
            case "1": giveBorder(btn_wall); break;
            case "6": giveBorder(btn_spawnPoint); break;
            case "7": giveBorder(btn_chest); break;
            case "8": giveBorder(btn_enemy); break;
            case "9": giveBorder(btn_spike); break;
            default: giveBorder(btn_torch);
        }
    }
}
