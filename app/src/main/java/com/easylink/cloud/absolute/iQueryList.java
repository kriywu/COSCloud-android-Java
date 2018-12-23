package com.easylink.cloud.absolute;

import com.easylink.cloud.modle.CloudFile;

import java.util.List;

public interface iQueryList {
    void updateList(List<CloudFile> files);
    void updatePath(String path);
}
