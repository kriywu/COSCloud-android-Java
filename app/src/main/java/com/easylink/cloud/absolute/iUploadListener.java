package com.easylink.cloud.absolute;

public interface iUploadListener {
    void onProgress(String key, int progress);

    void onSuccess(String key);

    void onFailed(String key);
}
