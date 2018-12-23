package com.easylink.cloud.absolute;

import com.easylink.cloud.modle.LocalFile;

public interface iPickPhoto {
    void pick(LocalFile path);

    void unPick(LocalFile path);

    boolean isPick(LocalFile path);

}
