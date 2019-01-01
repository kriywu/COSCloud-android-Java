package com.easylink.cloud.modle;

import android.os.Parcel;
import android.os.Parcelable;

import org.litepal.annotation.Column;
import org.litepal.crud.LitePalSupport;

import java.io.File;
import java.util.Date;
import java.util.Objects;

//extends LitePalSupport
public class FetchTask extends LitePalSupport implements Comparable<FetchTask>, Parcelable {

    @Column(nullable = false, unique = true)
    public String ID; // prefix+?+path+?+currentTime

    @Column(nullable = false, defaultValue = "unknown")
    public String bucket;
    public String prefix;
    public String path;
    public Date date;

    public String name; // 文件名
    public float progress;  // 进度
    public boolean isSuccess;  // 是否完成

    public FetchTask(String ID) {
        this.ID = ID;
        String[] arr = ID.split("\\?");
        bucket = arr[0];
        prefix = arr[1];
        path = arr[2];
        date = new Date(Long.valueOf(arr[3]));
        name = new File(path).getName();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FetchTask that = (FetchTask) o;
        return Objects.equals(ID, that.ID);
    }

    @Override
    public int hashCode() {

        return Objects.hash(ID);
    }

    // 优先选择最早未完成的任务显示
    @Override
    public int compareTo(FetchTask o) {
        if (!isSuccess && o.isSuccess) {
            return -1;
        } else if (isSuccess && !o.isSuccess) {
            return 1;
        } else {
            return date.compareTo(o.date);
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(ID);
        if (isSuccess) {
            dest.writeInt(1);
        } else {
            dest.writeInt(0);
        }

    }

    public static final Parcelable.Creator<FetchTask> CREATOR = new Creator<FetchTask>() {

        @Override
        public FetchTask createFromParcel(Parcel source) {
            FetchTask task = new FetchTask(source.readString());
            if (source.readInt() == 1) task.isSuccess = true;
            else task.isSuccess = false;
            return task;
        }

        @Override
        public FetchTask[] newArray(int size) {
            return new FetchTask[size];
        }
    };

    public void updata(FetchTask task){
        progress = task.progress;
        isSuccess = task.isSuccess;
    }
}
