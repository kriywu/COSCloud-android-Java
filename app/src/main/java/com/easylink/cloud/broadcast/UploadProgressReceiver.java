package com.easylink.cloud.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.easylink.cloud.absolute.iFlashData;
import com.easylink.cloud.modle.Constant;
import com.easylink.cloud.modle.FetchTask;

import java.util.List;

public class UploadProgressReceiver extends BroadcastReceiver {
    private List<FetchTask> list;
    private iFlashData callback;
    private static UploadProgressReceiver receiver;

    private UploadProgressReceiver(iFlashData callback, List<FetchTask> list) {
        this.list = list;
        this.callback = callback;
    }

    public static UploadProgressReceiver getReceiver(iFlashData callback, List<FetchTask> list) {
        if (receiver == null) {
            receiver = new UploadProgressReceiver(callback, list);
        }
        return receiver;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        FetchTask task = intent.getParcelableExtra(Constant.EXTRA_FETCH_TASK);

        // 如果列表中存在，更新
        // 如果列表中不存在，添加
        if (list.contains(task)) {
            list.get(list.indexOf(task)).updata(task); // 更新进度和状态
        } else {
            list.add(0, task);
        }
        callback.flash();
    }
}