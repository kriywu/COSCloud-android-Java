package com.easylink.cloud.util;


import android.graphics.Bitmap;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.util.HashMap;
import java.util.Map;

public class QR {
    public static Bitmap createBitmap(String url, int width, int height) {
        // 检查是否为null
        if (url == null || url.equals("") || url.length() == 1) return null;

        Map map = new HashMap();
        map.put(EncodeHintType.CHARACTER_SET, "utf-8");

        BitMatrix bitMatrix = null;

        // 将url解析成矩阵
        try {
            bitMatrix = new QRCodeWriter().encode(url, BarcodeFormat.QR_CODE, width, height, map);
        } catch (WriterException e) {
            return null;
        }

        // 设置颜色
        int[] pixels = new int[width * height];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                pixels[y * width + x] = bitMatrix.get(x, y) ? 0xff000000 : 0xffffffff; // 颜色
            }
        }

        return Bitmap.createBitmap(pixels,width,height,Bitmap.Config.RGB_565);
    }
}
