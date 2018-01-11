package com.sanganan.app.interfaces;

import com.sanganan.app.model.TabData;

import java.util.ArrayList;

/**
 * Created by Randhir Patel on 11/7/17.
 */

public interface ICallTOHomePage {
    void callBack(int position, ArrayList<TabData> datalist);
}
