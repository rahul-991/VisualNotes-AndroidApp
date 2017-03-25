package io.krumbs.sdk.starter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.Toast;

import java.sql.Blob;
import java.util.ArrayList;

/**
 * Created by Rahul on 2/24/2016.
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    Context context;
    public static final String DATABASE_NAME = "login.db";
    //LOGIN TABLE
    public static final String LOGIN_TABLE_NAME = "login_table";
    public static final String USER_NAME = "USERNAME";
    public static final String PASSWORD = "PASSWORD";
    //USER_PHOTO TABLE
    public static final String PHOTO_TABLE_NAME = "photo_table";
    public static final String PHOTO_URL = "PHOTO_URL";
    public static final String AUDIO_URL = "AUDIO_URL";
    public static final String USER_TYPE = "USER_TYPE";
    public static final String DESCRIPTION = "DESCRIPTION";
    public static final String USERNAME = "USER_NAME";
    public static final String TIME_OF_PIC= "DATE";
    public static final String TIMES_CLICKED = "TIMES_CLICKED";
    public static final String INTENT_CAT = "INTENT_CAT";
    public static final String LOCATION = "LOCATION";
    public static final String EXACT_DATE = "EXACT_DATE";
    public static final String SEARCH_ABLE_DATA = "SEARCH_ABLE_DATA";
    public static final String EMOJI_ID = "EMOJI_ID";
    public static final String JSON_OBJECT = "JSON_OBJECT";
    public DatabaseHelper(Context context){
        super(context, DATABASE_NAME, null, 41);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + LOGIN_TABLE_NAME +
                "(USERNAME TEXT PRIMARY KEY)");
        db.execSQL("create table " + PHOTO_TABLE_NAME +
                "(PHOTO_URL TEXT PRIMARY KEY, AUDIO_URL TEXT, USER_TYPE TEXT, DESCRIPTION TEXT,USER_NAME TEXT, DATE TEXT, TIMES_CLICKED INTEGER DEFAULT 0, INTENT_CAT TEXT, LOCATION TEXT, EXACT_DATE TEXT, SEARCH_ABLE_DATA TEXT, EMOJI_ID TEXT, JSON_OBJECT)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //DROP TABLE LOGIN
        db.execSQL("DROP TABLE IF EXISTS " + LOGIN_TABLE_NAME);
        //DROP TABLE IMAGE
        db.execSQL("DROP TABLE IF EXISTS " + PHOTO_TABLE_NAME);
        onCreate(db);

    }

    public boolean insertData(String name){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(USER_NAME, name);

        long result = db.insert(LOGIN_TABLE_NAME, null, contentValues);
        if(result==-1){
            Log.i("KRUMBS-Hello", "HEY");
            return false;
        }

        return true;
    }
    public boolean insertPhotoData(String photo_url, String audio_url , String user_type, String description, String username, String date, int number, String intent_category, String location, String exact_date, String search_able_data,String emoji_id, String jsonObject){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(USERNAME, username);
        contentValues.put(PHOTO_URL,photo_url);
        contentValues.put(AUDIO_URL, audio_url);
        contentValues.put(USER_TYPE, user_type);
        contentValues.put(DESCRIPTION,description);
        contentValues.put(TIME_OF_PIC, date);
        contentValues.put(TIMES_CLICKED,number);
        contentValues.put(INTENT_CAT,intent_category);
        contentValues.put(LOCATION,location);
        contentValues.put(EXACT_DATE,exact_date);
        contentValues.put(SEARCH_ABLE_DATA,search_able_data);
        contentValues.put(EMOJI_ID, emoji_id);
        contentValues.put(JSON_OBJECT,jsonObject);
        long result = db.insert(PHOTO_TABLE_NAME, null, contentValues);
        if(result==-1){
            Log.i("KRUMBS-Hello", "HEY");

            return false;
        }
        return true;
    }
    public ArrayList<String> returnUserNames(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("select USERNAME from login_table", null);
        ArrayList<String> s = new ArrayList<String>();
        while(cursor.moveToNext()){
            s.add(cursor.getString(0));
        }
        return s;
    }

    public void updateTimesClicked(String query){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("select TIMES_CLICKED from photo_table WHERE PHOTO_URL = ?", new String[]{query}, null);
        int getTIMESCLICKED=0;
        while(cursor.moveToNext()){
            getTIMESCLICKED=cursor.getInt(0);
        }
        getTIMESCLICKED++;
        Log.i("KRUMBS-CC", String.valueOf(getTIMESCLICKED));
        Cursor c= db.rawQuery("UPDATE photo_table SET TIMES_CLICKED = ? WHERE PHOTO_URL = ?",new String[]{String.valueOf(getTIMESCLICKED),query}, null);
        c.moveToFirst();
        c.close();
    }



    public String returnDate(String url){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("select EXACT_DATE from photo_table WHERE PHOTO_URL = ?", new String[]{url},null);
        String s = "";
        while(cursor.moveToNext()){
            s=cursor.getString(0);
        }
        return s;

    }
    public String returnEmoji(String url){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("select EMOJI_ID from photo_table WHERE PHOTO_URL = ?", new String[]{url},null);
        String s = "";
        while(cursor.moveToNext()){
            s=cursor.getString(0);
        }
        return s;

    }
    public String returnCatName(String url){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("select USER_TYPE from photo_table WHERE PHOTO_URL = ?", new String[]{url},null);
        String s = "";
        while(cursor.moveToNext()){
            s=cursor.getString(0);
        }
        return s;

    }
    public String returnExactLocation(String url){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("select LOCATION from photo_table WHERE PHOTO_URL = ?", new String[]{url},null);
        String s = "";
        while(cursor.moveToNext()){
            s=cursor.getString(0);
        }
        return s;

    }
    public ArrayList<String> allJsonObjects(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("select JSON_OBJECT from photo_table",null);
        ArrayList<String> s = new ArrayList<>();
        while(cursor.moveToNext()){
            s.add(cursor.getString(0));
        }
        return s;
    }
    public String allURLSFROMUSERNAME(String url){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("select USER_NAME from photo_table WHERE PHOTO_URL = ?", new String[]{url},null);
        String s="";
        while(cursor.moveToNext()){
            s=cursor.getString(0);
        }
        return s;
    }
}