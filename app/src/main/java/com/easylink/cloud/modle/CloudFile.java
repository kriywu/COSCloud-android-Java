package com.easylink.cloud.modle;

public class CloudFile {
    public String type; // 文件类型 TEXT MP3 MP4 ...
    public long size; // 文件大小
    public String name;
    public String key;
    public String lastModify;

    public CloudFile(String key, String name , String type){
        this.key = key;
        this.name = name;
        this.type = type;
    }
}
