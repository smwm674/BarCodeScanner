package com.zydellotechnolgies.barcodescanner.utils;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class utils {
    public static void hideKeyboard(Activity baseContext) {
        View currentFocus = baseContext.getCurrentFocus();
        if (currentFocus != null) {
            InputMethodManager imm = (InputMethodManager) baseContext.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(currentFocus.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
}
