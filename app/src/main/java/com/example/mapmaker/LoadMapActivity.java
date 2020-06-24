package com.example.mapmaker;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class LoadMapActivity extends AppCompatActivity {

    //all file names
    private String fileNames[];
    //file counter
    private int totalMaps = 0;
    //currently selected map
    private String selectedMap;
    //loads currently selected map
    private Button btn_select;
    //displays currently selected map
    private TextView tw_currentMap;

    //recyclerview
    private RecyclerView mRecyclerView;
    private MapAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    //list of all items in recyclerview
    private ArrayList<MapItem> mapList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_map);

        btn_select = findViewById(R.id.btn_select);
        btn_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //returns to 'MainActivity' with currently selected map
                Intent back = new Intent(LoadMapActivity.this, MainActivity.class);
                back.putExtra(MainActivity.SELECTED_MAP, selectedMap);

                setResult(Activity.RESULT_OK, back);
                finish();
            }
        });

        //updates vars
        totalMaps = getIntent().getIntExtra(MainActivity.FILE_COUNTER, 0);
        fileNames = getIntent().getStringArrayExtra(MainActivity.ALL_FILES);

        tw_currentMap = findViewById(R.id.tw_currentSelected);

        //creates arraylist for map
        createMapList();
        //creates recyclerview
        buildRecyclerView();
    }

    //creates arraylist for recyclerview
    public void createMapList(){
        //recyclerview
        mapList = new ArrayList<>();

        //adds all items to view. no sorting
        for(int x = 0; x < totalMaps; x++) {
            mapList.add(new MapItem(R.drawable.ic_android, fileNames[x], "map"));
        }
    }

    //creates recyclerview
    public void buildRecyclerView(){
        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mAdapter = new MapAdapter(mapList);

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(new MapAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                //changes all items to blue text
                changeAllItems();
                //adds red text to currently selected for visual effect
                changeItem(position, Color.RED);
                //update animation
                updateCurrentSelected(position);
            }

            @Override
            public void onDeleteClick(int position) {
                //removes unwanted item
                removeItem(position);
            }
        });
    }

    //updates which item has been selected
    public void updateCurrentSelected(int position){
        selectedMap = mapList.get(position).getText1();
        tw_currentMap.setText("Currently selected: "+selectedMap);
    }

    //changes text color of all items
    public void changeAllItems(){
        for(int i = 0; i < totalMaps; i++){
            mapList.get(i).changeText1Color(Color.BLUE);
            mAdapter.notifyDataSetChanged();
        }
    }

    //not used, but might be useful if ever expanding on current app
    public void insertItem(int position){
        mapList.add(position, new MapItem(R.drawable.ic_android, "some name", "some info"));
        mAdapter.notifyItemInserted(position);
    }

    //removes item
    public void removeItem(int position){
        mapList.remove(position);
        //remove animation. not that useful because we are returning to main to handle deletion
        mAdapter.notifyItemRemoved(position);

        Intent back = new Intent(LoadMapActivity.this, MainActivity.class);
        //specifies that only finishing to update files, then returning
        back.putExtra(MainActivity.RETURN, true);
        back.putExtra(MainActivity.REMOVED_MAP, position);
        setResult(Activity.RESULT_OK, back);
        finish();
    }

    //changes item to have specified text color
    public void changeItem(int position, int color){
        mapList.get(position).changeText1Color(color);
        mAdapter.notifyItemChanged(position);
    }
}
