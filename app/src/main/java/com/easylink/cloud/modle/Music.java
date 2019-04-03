package com.easylink.cloud.modle;

public class Music extends LocalFile {
    public String name;
    public String album;
    public String artist;
    public String duration;
    public String id;


    public Music(String name, String path, String artist, float size) {
        super(path, size);
        this.name = name;
        this.artist = artist;
    }

}
