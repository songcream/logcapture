package com.songcream.logcapture;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.SimpleDateFormat;

/**
 * Created by gengsong on 2018/6/29.
 */

public class LogBean implements Parcelable{
    private String time;
    private String url;
    private String body;
    private String response;
    private int code;
    private String localLog;

    public LogBean(){}

    protected LogBean(Parcel in) {
        super();
        time = in.readString();
        url = in.readString();
        body = in.readString();
        response = in.readString();
        code=in.readInt();
        localLog=in.readString();
    }

    public static final Creator<LogBean> CREATOR = new Creator<LogBean>() {
        @Override
        public LogBean createFromParcel(Parcel in) {
            return new LogBean(in);
        }

        @Override
        public LogBean[] newArray(int size) {
            return new LogBean[size];
        }
    };

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setTime(long time){
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("HH:mm:ss");
        try {
            setTime(simpleDateFormat.format(time));
        }catch (Exception e){

        }
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(time);
        dest.writeString(url);
        dest.writeString(body);
        dest.writeString(response);
        dest.writeInt(code);
        dest.writeString(localLog);
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getLocalLog() {
        return localLog;
    }

    public void setLocalLog(String localLog) {
        this.localLog = localLog;
    }
}
