package com.sanganan.app.common;

import android.graphics.Color;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by root on 14/11/16.
 */

public class RandomColorAllocator {
    int color[] = {Color.parseColor("#DBA55B"), Color.parseColor("#DA7589"), Color.parseColor("#053B57"), Color.parseColor("#6599FF"), Color.parseColor("#D26A5D"), Color.parseColor("#86959F")};


    public ArrayList<Integer> listColor = new ArrayList<>();
    Random random = new Random();

    public RandomColorAllocator(int size){
        for(int i=0;i<size;i++){
            int randomValue = random.nextInt(6);
            listColor.add(color[randomValue]);
        }
    }

}
