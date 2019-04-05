package com.easylink.cloud.absolute;

public interface iDownloadListener {
    void onProgress(String key, int progress);

    void onSuccess(String key);

    void onFailed(String key);

    void onPaused(String key);

    void onCanceled(String key);
}
