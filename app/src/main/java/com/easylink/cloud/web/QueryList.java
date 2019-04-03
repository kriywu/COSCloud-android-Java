package com.easylink.cloud.web;

import android.os.AsyncTask;

import com.easylink.cloud.absolute.iQueryList;
import com.easylink.cloud.modle.CloudFile;
import com.easylink.cloud.modle.Constant;

import java.lang.ref.WeakReference;
import java.util.List;

public class QueryList extends AsyncTask<Void, Void, List<CloudFile>> {
    private static WeakReference<iQueryList> mCallBack;
    private Builder builder;


    private QueryList(Builder builder) {
        this.builder = builder;
    }

    @Override
    protected List<CloudFile> doInBackground(Void... voids) {
        if (builder.delimiter == null) {
            return Client.getClient().queryAllFile(builder.bucket);
        }

        if (builder.flag == 1) {
            return Client.getClient().getPath(builder.bucket, builder.prefix, builder.delimiter);
        }
        return Client.getClient().getContentAndPath(builder.bucket, builder.prefix, builder.delimiter);
    }

    @Override
    protected void onPostExecute(List<CloudFile> fs) {
        super.onPostExecute(fs);
        if (mCallBack.get() == null) return;
        mCallBack.get().updateList(fs);
    }


    public static class Builder {
        private String bucket = Constant.bucket;
        private String prefix = "";
        private Character delimiter = '/';
        private int flag = 0;

        public Builder(iQueryList callback) {
            mCallBack = new WeakReference<>(callback);
        }

        public Builder setBucket(String bucket) {
            this.bucket = bucket;
            return this;
        }

        public Builder setPrefix(String prefix) {
            this.prefix = prefix;
            return this;
        }

        public Builder setDelimiter(Character delimiter) {
            this.delimiter = delimiter;
            return this;
        }

        public Builder setFlag(int flag) {
            this.flag = flag;
            return this;
        }

        public QueryList build() {
            return new QueryList(this);
        }

    }
}
