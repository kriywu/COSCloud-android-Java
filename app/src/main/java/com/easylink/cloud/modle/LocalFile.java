package com.easylink.cloud.modle;

import java.util.Objects;

public class LocalFile implements Comparable {
    public String path;
    public float size;

    public LocalFile(String path, float size) {
        this.path = path;
        this.size = size;
    }

    public float getSizeFormat() {
        int mb = (int) (1000 * size / (1024 * 1024.0f));
        return mb / 1000f;
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
