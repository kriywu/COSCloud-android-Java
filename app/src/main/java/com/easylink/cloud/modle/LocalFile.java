package com.easylink.cloud.modle;

import java.util.Objects;

public class LocalFile implements Comparable {
    private String path;

    public LocalFile(String path){
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LocalFile localFile = (LocalFile) o;
        return Objects.equals(path, localFile.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(path);
    }

    @Override
    public int compareTo(Object o) {
        return 0;
    }
}
