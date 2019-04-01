package com.easylink.cloud.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class BitmapUtil {
    public static Bitmap decodeBitmapFromFile(String file, int width, int height) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(file, options);

        options.inSampleSize = calculateInSampleSize(options, width, height);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(file, options);
    }

    private static int calculateInSampleSize(BitmapFactory.Options options, int width, int height) {
        final int outHeight = options.outHeight;
        final int outWidth = options.outWidth;

        if (outHeight < height || outWidth < width) {
            return 1;
        }
        return Math.min(outHeight / height, outWidth / width);
    }
}