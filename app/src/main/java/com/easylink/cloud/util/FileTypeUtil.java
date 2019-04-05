package com.easylink.cloud.util;


import com.easylink.cloud.R;
import com.easylink.cloud.modle.Constant;

public class FileTypeUtil {
    // dir1/dir2/
    public static String recognizeName(String path) {
        if (path.contains("/"))
            return path.substring(path.lastIndexOf('/') + 1);
        return path;
    }

    public static String recognizeDirName(String path) {
        if (path.contains("/")) {
            return path.substring(path.lastIndexOf('/') + 1);
        }
        return path;
    }

    public static String recognizeType(String path) {
        String postFix = path.substring(path.lastIndexOf('.') + 1).toLowerCase(); // 后缀
        switch (postFix) {
            case "png":
            case "gif":
            case "bmp":
            case "jpg":
            case "jpeg":
                return Constant.PHOTO;
            case "avi":
            case "mp4":
                return Constant.VIDEO;
            case "mp3":
            case "mpeg":
            case "wma":
                return Constant.MUSIC;
            case "apk":
                return Constant.APK;
            case "zip":
            case "rar":
                return Constant.ZIP;
            case "txt":
            case "csv":
                return Constant.TEXT;
            case "pdf":
                return Constant.PDF;
            case "doc":
            case "docx":
                return Constant.DOC;
            case "xls":
            case "xlsx":
                return Constant.EXCEL;
            case "ppt":
            case "pptx":
                return Constant.PPT;
            default:
                return Constant.FILE;

        }

    }

    public static int getIconByFileType(String type) {
        switch (type) {
            case Constant.DIR:
                return R.drawable.icon_folder;
            case Constant.PHOTO:
                return R.drawable.icon_photo;
            case Constant.VIDEO:
                return R.drawable.icon_mv;
            case Constant.MUSIC:
                return R.drawable.icon_music;
            case Constant.APK:
                return R.drawable.icon_apk;
            case Constant.RAR:
                return R.drawable.icon_rar;
            case Constant.TEXT:
                return R.drawable.icon_text;
            case Constant.DOC:
                return R.drawable.icon_word;
            case Constant.EXCEL:
                return R.drawable.icon_excel;
            case Constant.PPT:
                return R.drawable.icon_ppt;
            case Constant.PDF:
                return R.drawable.icon_pdf;
            default:
                return R.drawable.icon_file;

        }
    }
}
