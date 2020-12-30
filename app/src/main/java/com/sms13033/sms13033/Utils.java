package com.sms13033.sms13033;

import android.view.View;

public class Utils {

    public static void preventTwoClick(final View view){
        view.setEnabled(false);
        view.postDelayed(new Runnable() {
            public void run() {
                view.setEnabled(true);
            }
        }, 500);
    }
}
