package com.easylink.cloud.util;

import java.util.Date;

public class StaticHelper {
    public static String geneTaskID(String bucket, String prefix, String path, Date date) {
        return bucket + "?" + prefix + "?" + path + "?" + date.getTime();
    }
}
