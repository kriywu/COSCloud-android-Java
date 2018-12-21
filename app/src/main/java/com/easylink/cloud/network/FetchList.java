package com.easylink.cloud.network;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.easylink.cloud.absolute.FetchListCallBack;
import com.easylink.cloud.control.Client;
import com.easylink.cloud.modle.EFile;
import com.easylink.cloud.util.Constant;

import java.util.List;

public class FetchList extends AsyncTask<Void, Void, List<EFile>> {
    private FetchListCallBack callBack;
    private Context context;
    private String bucket = Constant.bucket;
    private String prefix = "";
    private char delimiter = '/';

    public FetchList(Context context, FetchListCallBack callBack) {
        this.callBack = callBack;
        this.context = context;
    }

    @Override
    protected List<EFile> doInBackground(Void... voids) {
        // 获取当前bucket的文件列表
        return Client.getClient(context).getCurrentFiles(bucket, prefix, delimiter);
    }

    @Override
    protected void onPostExecute(List<EFile> fs) {
        super.onPostExecute(fs);
        callBack.update(fs);
    }


    public FetchList setBucket(String bucket) {
        this.bucket = bucket;
        return this;
    }

    public FetchList setPrefix(String prefix) {
        this.prefix = prefix;
        return this;
    }

    public FetchList setDelimiter(char delimiter) {
        this.delimiter = delimiter;
        return this;
    }

}
