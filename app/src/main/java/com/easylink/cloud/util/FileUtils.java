package com.easylink.cloud.util;

import com.easylink.cloud.modle.Constant;

public class FileUtils {

    public static String getFileType(String path) {
        path = path.toLowerCase();
        if (path.endsWith(".doc") || path.endsWith(".docx") || path.endsWith(".xls") || path.endsWith(".xlsx")
                || path.endsWith(".ppt") || path.endsWith(".pptx") || path.endsWith(".pdf")) {
            return Constant.EXTRA_DOC;
        } else if (path.endsWith(".apk") || path.endsWith(".exe")) {
            return Constant.EXTRA_APK;
        } else if (path.endsWith(".zip") || path.endsWith(".rar") || path.endsWith(".tar") || path.endsWith(".gz") || path.endsWith(".7z")) {
            return Constant.EXTRA_RAR;
        } else {
            return "";
        }
    }
}
