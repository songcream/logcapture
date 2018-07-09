package com.songcream.logcapture;

import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by gengsong on 2018/7/9.
 */

public class LocalLogUtil {
    public static final String Log_D=" *:Debug";
    public static final String Log_E=" *:Error";
    public static final String Log_I=" *:Info";
    public static final String Log_W=" *:Warn";
    public static final String Log_V=" *:Verbose";
//    private static final String logPath= Environment.getExternalStorageDirectory().getAbsolutePath()+"/logClub.txt";
    private LocalLogThread localLogThread;

    public void startLog(){
//        File file=new File(logPath);
//        if(file.exists()){
//            file.delete();
//        }
        try {
//            file.createNewFile();
//            String path=logPath;
//            Process exec = Runtime.getRuntime().exec("logcat -f "+ logPath+logType);
            stopLog();
            Process exec = Runtime.getRuntime().exec("logcat -v time *:V");
            localLogThread=new LocalLogThread(exec.getInputStream());
            localLogThread.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stopLog(){
        if(localLogThread!=null){
            localLogThread.stopLog();
        }
    }

    private class LocalLogThread extends Thread {
        private File file;
        private InputStream inputStream;
        private boolean stop=false;

        public LocalLogThread(File file) {
            this.file=file;
        }

        public LocalLogThread(InputStream inputStream) {
            this.inputStream=inputStream;
        }

        @Override
        public void run() {
            super.run();
            BufferedReader reader = null;
            String line = null;
            try {
                if(inputStream==null) {
                    inputStream = new FileInputStream(file);
                }
                reader = new BufferedReader(new InputStreamReader(inputStream,"UTF-8"));
                LogBean logBean=new LogBean();
                while (!stop) {
                    line = reader.readLine();
                    logBean.setLocalLog(line);
                    CommucateService.notifyLog(logBean);
//                    Log.e("log","from other===="+line);      //这里不能调用log，否则会造成死循环
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if(inputStream!=null) {
                        inputStream.close();
                        inputStream=null;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        public void stopLog(){
            stop=true;
        }
    }

    public static ArrayList<LogBean> filterLocalLog(ArrayList<LogBean> dataList,String findString,int logLevel){
        ArrayList<LogBean> resultList=new ArrayList<>();
        Pattern p = Pattern.compile(findString);
        for(LogBean logBean:dataList){
            String log=logBean.getLocalLog();
            if(isInLevel(logLevel,log)){
                boolean hasString=false;
                if(!TextUtils.isEmpty(findString)){
                    SpannableString s = new SpannableString(log);
                    Matcher m = p.matcher(s);
                    while (m.find()){
                        hasString=true;
                        int start = m.start();
                        int end = m.end();
                        s.setSpan(new ForegroundColorSpan(Color.RED), start, end,
                                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                }else {
                    hasString=true;
                }
                if(hasString) resultList.add(logBean);
            }
        }
        return resultList;
    }

    public static boolean isInLevel(int logLevel,String log){
        switch (logLevel){
            case 0:
                return true;
            case 1:
                if(log.contains("D/") || log.contains("I/") || log.contains("W/") || log.contains("E/")) {
                    return true;
                }else{
                    return false;
                }
            case 2:
                if(log.contains("I/") || log.contains("W/") || log.contains("E/")) {
                    return true;
                }else{
                    return false;
                }
            case 3:
                if(log.contains("W/") || log.contains("E/")) {
                    return true;
                }else{
                    return false;
                }
            case 4:
                if(log.contains("E/")) {
                    return true;
                }else{
                    return false;
                }
        }
        return false;
    }
}
