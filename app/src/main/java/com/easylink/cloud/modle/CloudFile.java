package com.easylink.cloud.modle;

public class CloudFile {
    private String state; // 文件类型 TEXT MP3 MP4 ...
    private long size; // 文件大小
    private String name;
    private String key;
    private String lastModify;

    public CloudFile(String key, String name , String state){
        this.key = key;
        this.name = name;
        this.state = state;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getLastModify() {
        return lastModify;
    }

    public void setLastModify(String lastModify) {
        this.lastModify = lastModify;
    }
}
