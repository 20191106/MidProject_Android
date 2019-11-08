package com.example.midproject;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class DbSetting {
    public static void clearDB(Context con) {
        //only for test
        SQLiteDatabase db = con.openOrCreateDatabase("my_db.db", Context.MODE_PRIVATE, null);
        db.execSQL("DELETE FROM myDb");
        db.execSQL("DROP TABLE myDb");
        db.close();
    }

    public static void dbInit(Context con) {
        SQLiteDatabase db = con.openOrCreateDatabase("my_db.db", Context.MODE_PRIVATE, null);

        //4개의 컬럼을 가진 테이블을 만든다
        db.execSQL("CREATE TABLE IF NOT EXISTS myDb("
                + "_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "year INT,"
                + "month INT,"
                + "day INT,"
                + "context TEXT" + ");");

        db.close();
    }

    public static void insertDB(int year, int month, int day, String context, Context con) {
        SQLiteDatabase db = con.openOrCreateDatabase("my_db.db", Context.MODE_PRIVATE, null);

        //데이터 집어 넣기
        db.execSQL("INSERT INTO myDb (year, month, day, context) VALUES ('" + year + "','" + month + "','" + day + "','" + context + "');");

        db.close();
    }
}
