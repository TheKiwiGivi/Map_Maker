package com.example.mapmaker;

import android.graphics.Color;

public class MapItem {
    private int mImageResource;
    private String mText1;
    private String mText2;
    private int textColor;

    public MapItem(int imageResource, String text1, String text2){
        mImageResource = imageResource;
        mText1 = text1;
        mText2 = text2;
        textColor = Color.BLUE;
    }
    public void changeText1Color(int color){
        textColor = color;
    }

    public int getImageResource(){
        return mImageResource;
    }
    public String getText1(){
        return mText1;
    }
    public String getText2(){
        return mText2;
    }

    public int getText1Color(){
        return textColor;
    }
}
