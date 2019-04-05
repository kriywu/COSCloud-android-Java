package com.easylink.cloud.modle;

import android.view.ViewConfiguration;

import com.easylink.cloud.MyApplication;

public class Constant {
    /**
     * 下面5个是我COS相关的参数
     * 你可以免费申请腾讯云COS服务从而获取自己的5个参数
     */
    public static String appId = "1253943763";
    public static String secretId = "AKIDyssnYArt1nU1NXHe1sbCcqsrl9PU5K0Z";
    public static String secretKey = "b4LRM6e2EIDTVO6cw52IQNAlV8nHagbL";
    public static String region = "ap-beijing";
    public static String bucket = "cos-1253943763";

    /**
     * configure
     */
    public static final int TOUCH_SLOP = ViewConfiguration.get(MyApplication.getContext()).getScaledTouchSlop();

    /**
     * 文件类型，目前还只是区分文件夹和非文件夹
     */
    public static final String DIR = "dir"; // 文件夹
    public static final String FILE = "file"; // 文件
    public static final String PHOTO = "photo"; // 照片  png gif ...
    public static final String VIDEO = "video"; // 视频  flv mp4
    public static final String MUSIC = "music";// mp3
    public static final String RAR = "rar";// rar
    public static final String ZIP = "zip";
    public static final String TEXT = "text";
    public static final String APK = "apk";
    public static final String DOC = "doc";
    public static final String EXCEL = "excel";
    public static final String PPT = "ppt";
    public static final String PDF = "pdf";

    final public static String EXTRA_PATHS = "extra_paths";
    final public static String EXTRA_PREFIX = "extra_prefix";
    final public static String EXTRA_KEY = "extra_key";

    final public static int IMAGE_REQUEST_CODE = 1;
    final public static int VIDEO_REQUEST_CODE = 2;

    final public static String EXTRA_ID = "extra_id";

    final public static String UPLOAD_TYPE = "upload_type";
    final public static String EXTRA_VIDEO = "extra_video";
    final public static String EXTRA_MUSIC = "extra_music";
    final public static String EXTRA_PHOTO = "extra_photo";
    final public static String EXTRA_DOC = "doc";
    final public static String EXTRA_RAR = "rar";
    final public static String EXTRA_APK = "apk";

    final public static String BROADCAST_UPLOAD_PROGRESS = "com.easylink.cloud.upload_progress";
    final public static String EXTRA_FETCH_TASK = "extra_task";
}
