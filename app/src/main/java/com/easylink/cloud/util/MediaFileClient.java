package com.easylink.cloud.util;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import com.easylink.cloud.modle.LocalFile;
import com.easylink.cloud.modle.Music;

import java.util.ArrayList;
import java.util.List;

public class MediaFileClient {
    private static MediaFileClient client;
    private Context context;

    private MediaFileClient(Context context) {
        this.context = context;
    }

    public static MediaFileClient getInstance(Context context) {
        if (client == null) {
            return new MediaFileClient(context);
        } else {
            return client;
        }
    }

    public List<Music> getMusics() {
        List<Music> musics = new ArrayList<>();
        Cursor c = null;

        c = context.getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                null,
                null,
                null,
                MediaStore.Audio.Media.DEFAULT_SORT_ORDER);

        if (c == null) return musics;
        while (c.moveToNext()) {
            String path = c.getString(c.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));// 路径
            String name = c.getString(c.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)); // 歌曲名
            //String album = c.getString(c.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)); // 专辑
            String artist = c.getString(c.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)); // 作者
            float size = c.getLong(c.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE));// 大小
            //int duration = c.getInt(c.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));// 时长
            //int time = c.getInt(c.getColumnIndexOrThrow(MediaStore.Audio.Media._ID));// 歌曲的id
            musics.add(new Music(name, path, artist, size));
        }

        c.close();
        return musics;
    }

    public List<LocalFile> getPhotos() {
        List<LocalFile> photos = new ArrayList<>();
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null,
                null,
                null,
                MediaStore.Images.Media.DATE_MODIFIED + " desc ");

        while (cursor.moveToNext()) {
            String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            float size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE));// 大小
            photos.add(new LocalFile(path, size));
        }
        cursor.close();
        return photos;
    }

    public List<LocalFile> getVideo() {
        List<LocalFile> videos = new ArrayList<>();
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                null,
                null,
                null,
                MediaStore.Video.Media.DATE_MODIFIED);

        while (cursor.moveToNext()) {
            String path = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
            float size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE));// 大小
            videos.add(new LocalFile(path, size));
        }
        cursor.close();
        return videos;
    }

    public List<LocalFile> getFilesByType(String type) {
        List<LocalFile> files = new ArrayList<LocalFile>();
        // 扫描files文件库
        Cursor c = context.getContentResolver().query(MediaStore.Files.getContentUri("external"), new String[]{"_id", "_data", "_size"}, null, null, null);
        int dataindex = c.getColumnIndex(MediaStore.Files.FileColumns.DATA);
        int sizeindex = c.getColumnIndex(MediaStore.Files.FileColumns.SIZE);

        while (c.moveToNext()) {
            String path = c.getString(dataindex);
            if (FileUtils.getFileType(path).equals(type)) {
                long size = c.getLong(sizeindex);
                files.add(new LocalFile(path, size));
            }
        }

        return files;
    }
}
