package com.sms13033.sms13033;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SaveSharedPreferences {

    static final String FIRSTTIME= "";
    static final String FULLNAME= "";

    static SharedPreferences getSharedPreferences(Context ctx) {
        return PreferenceManager.getDefaultSharedPreferences(ctx);
    }

    //method that determines if it's the first time user opens the app
    //we need that method in order to fill the listview with the standard 6 messages
    public static void  setFirstTime(Context ctx){

        String firstTime="No";
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(FIRSTTIME, firstTime);
        editor.commit();

    }

    //returns if its the first time user opens the app
    public  static  String getFirstTimeOrNot(Context ctx){

        return  getSharedPreferences(ctx).getString(FIRSTTIME,"");

    }

    //saving the fullname of the user
    public static void  setFullname(Context ctx,String fullname){


        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(FULLNAME, fullname);
        editor.commit();

    }


    //returning the fullname of the user
    public  static  String getFullname(Context ctx){

        return  getSharedPreferences(ctx).getString(FULLNAME,"");

    }

}
