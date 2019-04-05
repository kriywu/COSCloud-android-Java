package com.easylink.cloud.modle;

import android.os.Parcel;
import android.os.Parcelable;

import org.litepal.crud.LitePalSupport;

import java.io.File;
import java.util.Date;
import java.util.Objects;

//extends LitePalSupport
public class Task extends LitePalSupport implements Comparable<Task>, Parcelable {

    public String ID; // prefix+?+path+?+currentTime
    public String key;
    public String path;
    public String name;
    public Date date;

    public boolean isSuccess;
    public boolean isPause;
    public boolean isCanceled;
    public boolean isResume;
    public boolean isFailed;
    public int progress;

    public Task(String key) {
        this.ID = key;
    }

    public Task(String key, String path) {
        this.key = key;
        this.path = path;
        name = path.substring(path.lastIndexOf(File.separator));
        date = new Date();
        this.ID = key;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task that = (Task) o;
        return Objects.equals(ID, that.ID);
    }

    @Override
    public int hashCode() {

        return Objects.hash(ID);
    }

    protected Task(Parcel in) {
        ID = in.readString();
        key = in.readString();
        path = in.readString();
        name = in.readString();
        isSuccess = in.readByte() != 0;
        isPause = in.readByte() != 0;
        isCanceled = in.readByte() != 0;
        isResume = in.readByte() != 0;
    }

    public static final Creator<Task> CREATOR = new Creator<Task>() {
        @Override
        public Task createFromParcel(Parcel in) {
            return new Task(in);
        }

        @Override
        public Task[] newArray(int size) {
            return new Task[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(ID);
        dest.writeString(key);
        dest.writeString(path);
        dest.writeString(name);
        dest.writeByte((byte) (isSuccess ? 1 : 0));
        dest.writeByte((byte) (isPause ? 1 : 0));
        dest.writeByte((byte) (isCanceled ? 1 : 0));
        dest.writeByte((byte) (isResume ? 1 : 0));
    }

    @Override
    public int compareTo(Task o) {
        return 0;
    }
}
