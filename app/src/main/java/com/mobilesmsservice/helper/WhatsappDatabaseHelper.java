package com.mobilesmsservice.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

public class WhatsappDatabaseHelper extends SQLiteOpenHelper {
    String COL_1 = "Phone",COL_2="Message",COL_3="Time";
    public WhatsappDatabaseHelper(@Nullable Context context) {
        super(context, "WhatsappDB", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE Messages (ID INTEGER PRIMARY KEY AUTOINCREMENT,Phone TEXT,Message TEXT,Time TIME)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS Messages");
        onCreate(db);
    }
    public void insertMessage(String phone, String message){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_1,phone);
        cv.put(COL_2,message);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        cv.put(COL_3,dateFormat.format(date));
        db.insert("Messages",null,cv);
        db.close();
    }
    public JSONArray getAllData(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor  cursor = db.rawQuery("select * from Messages",null);
        try {
            JSONArray tableData = new JSONArray();
            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    int phone_index = cursor.getColumnIndex(COL_1);
                    int message_index = cursor.getColumnIndex(COL_2);
                    int date_index = cursor.getColumnIndex(COL_3);
                    String phone = cursor.getString(phone_index);
                    String message = cursor.getString(message_index);
                    String date = cursor.getString(date_index);
                    JSONObject data = new JSONObject();
                    data.put("phone",phone);
                    data.put("message",message);
                    data.put("date",date);
                    tableData.put(data);
                    cursor.moveToNext();
                }
            }
            db.close();
            return tableData;
        }catch (Exception e){

        }
        db.close();
        return new JSONArray();
    }
}
