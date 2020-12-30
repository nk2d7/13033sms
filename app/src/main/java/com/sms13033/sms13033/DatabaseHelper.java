package com.sms13033.sms13033;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "AvailableMessages.db";
    public static final String CONTACTS_TABLE_NAME="messages";
    public static final String CONTACTS_COLUMN_ID = "id";
    public static final String CONTACTS_COLUMN_MESSAGE = "message";

    private SQLiteDatabase myDatabase;
    private final Context myContext;
    Cursor c;
    int first;


    public DatabaseHelper(@Nullable Context context) {

        super(context,DATABASE_NAME , null, 1);
        this.myContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL(
                "create table messages " +
                        "(id INTEGER primary key,message TEXT)"
        );

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS messages");
        onCreate(db);
    }


    //returns the number of rows a table has
    public int getRowsCount(String tableName) {

        SQLiteDatabase db = this.getWritableDatabase();
        String countQuery = "SELECT  * FROM "+tableName;
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();

        return count;

    }

    //insert a new record on db(auto increment id we get from method getRowsCount,x,y,data+time)
    public boolean insertRecord(String message){

        SQLiteDatabase db = this.getWritableDatabase();
        int count = getRowsCount("messages")+1;//finding the id value of our new record


        String sql="INSERT INTO messages (id,message)"+"VALUES ('"+count+"','"+message+"')";

        try
        {
            db.execSQL(sql);
            return true;

        }
        catch (Exception e)
        {
            Log.e("ERROR", e.toString());
            return  false;
        }

    }

    //getting all the records that exist in db
    public ArrayList<String> getMessages(){

        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<String> myStringValues = new ArrayList<String>();

        Cursor  cursor = db.rawQuery("select * from messages",null);
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                String message= cursor.getString(cursor.getColumnIndex("id"))+")  "+cursor.getString(cursor.getColumnIndex("message"));

                myStringValues.add(message);
                cursor.moveToNext();
            }
        }

        return myStringValues;
    }

}
