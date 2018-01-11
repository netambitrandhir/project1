package com.sanganan.app.interfaces;

import android.support.v4.app.Fragment;

/**
 * Created by aksoni on 1/7/2016.
 */
public interface ToolbarListner {
    void onButtonClick(Fragment fragment, Boolean isCommingBack);
    void onButtonClickNoBack(Fragment fragment);
}
