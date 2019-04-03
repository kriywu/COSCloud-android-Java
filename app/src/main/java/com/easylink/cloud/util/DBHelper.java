package com.easylink.cloud.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DBHelper extends SQLiteOpenHelper {
    private Context context;
    public static final String UPLOAD_HISTORY = "upload_history";
    public static final String CREATE_UPLOAD_HISTORY = "create table " + UPLOAD_HISTORY
            + "(ID text,"
            + "name text,"
            + "path text,"
            + "progress integer,"
            + "isSucceed int,"
            + "isFailed int,"
            + "isCanceled int)";

    public DBHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        this.context = context;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_UPLOAD_HISTORY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
