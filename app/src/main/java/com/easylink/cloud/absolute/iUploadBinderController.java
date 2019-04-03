package com.easylink.cloud.absolute;

public interface iUploadBinderController {
    void pause(String key);
    void resume(String key);
    void canceled(String key);
}
