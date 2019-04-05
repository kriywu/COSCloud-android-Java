package com.easylink.cloud.util;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.easylink.cloud.MyApplication;
import com.easylink.cloud.modle.Task;

import java.util.List;

public class TableUploadTaskCRUD {
    private static TableUploadTaskCRUD tableUploadTaskCRUD;
    private DBHelper dbHelper;

    private TableUploadTaskCRUD() {
        dbHelper = new DBHelper(MyApplication.getContext(), "upload_history.db", null, 1);
    }

    public static TableUploadTaskCRUD getInstant() {
        if (tableUploadTaskCRUD == null) {
            synchronized (TableUploadTaskCRUD.class) {
                if (tableUploadTaskCRUD == null) {
                    tableUploadTaskCRUD = new TableUploadTaskCRUD();
                }
            }
        }
        return tableUploadTaskCRUD;
    }

    public void queryUploadTask(List<Task> tasks) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query(DBHelper.UPLOAD_HISTORY, null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                String id = cursor.getString(cursor.getColumnIndex("ID"));
                String name = cursor.getString(cursor.getColumnIndex("name"));
                String path = cursor.getString(cursor.getColumnIndex("path"));
                int progress = cursor.getInt(cursor.getColumnIndex("progress"));
                boolean isSucceed = cursor.getInt(cursor.getColumnIndex("isSucceed")) != 0;
                boolean isFailed = cursor.getInt(cursor.getColumnIndex("isFailed")) != 0;
                boolean isCanceled = cursor.getInt(cursor.getColumnIndex("isCanceled")) != 0;
                Task task = new Task(id, path);
                task.name = name;
                task.progress = progress;
                task.isCanceled = isCanceled;
                task.isFailed = isFailed;
                task.isSuccess = isSucceed;
                tasks.add(task);
            } while (cursor.moveToNext());
            cursor.close();
        }
    }

    public void insertUploadTask(Task task) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("id", task.ID);
        values.put("name", task.name);
        values.put("path", task.path);
        values.put("progress", task.progress);
        values.put("isSucceed", task.isSuccess ? 1 : 0);
        values.put("isFailed", task.isFailed ? 1 : 0);
        values.put("isCanceled", task.isCanceled ? 1 : 0);
        db.insert(DBHelper.UPLOAD_HISTORY, null, values);
        db.close();
    }

    public void removeUploadTask(Task task){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(DBHelper.UPLOAD_HISTORY,"ID = ?",new String[]{task.ID});
        db.close();
    }
}
