package com.easylink.cloud.web;

import android.content.Context;
import android.os.AsyncTask;

import com.easylink.cloud.absolute.iQueryList;
import com.easylink.cloud.modle.CloudFile;
import com.easylink.cloud.modle.Constant;

import java.util.List;

public class QueryList extends AsyncTask<Void, Void, List<CloudFile>> {
    private static iQueryList mCallBack;
    private static Context mContext;
    private Builder builder;


    private QueryList(Builder builder) {
        this.builder = builder;
    }

    @Override
    protected List<CloudFile> doInBackground(Void... voids) {
        if (builder.isPath == 1) {
            return Client.getClient(mContext).getPath(builder.bucket, builder.prefix, builder.delimiter);
        }
        return Client.getClient(mContext).getContentAndPath(builder.bucket, builder.prefix, builder.delimiter);
    }

    @Override
    protected void onPostExecute(List<CloudFile> fs) {
        super.onPostExecute(fs);
        mCallBack.updateList(fs);
    }


    public static class Builder {
        private String bucket = Constant.bucket;
        private String prefix = "";
        private char delimiter = '/';
        private int isPath = 0;

        public Builder(Context context, iQueryList callback) {
            mContext = context;
            mCallBack = callback;
        }

        public Builder setBucket(String bucket) {
            this.bucket = bucket;
            return this;
        }

        public Builder setPrefix(String prefix) {
            this.prefix = prefix;
            return this;
        }

        public Builder setDelimiter(char delimiter) {
            this.delimiter = delimiter;
            return this;
        }

        public Builder setFlag(int isPath) {
            this.isPath = isPath;
            return this;
        }

        public QueryList build() {
            return new QueryList(this);
        }

    }
}
