package com.songcream.server;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.songcream.logcapture.CommucateService;
import com.songcream.logcapture.LogBean;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by gengsong on 2018/6/28.
 */

public class MainActivity extends Activity {
    private Button buttonSend;
    private EditText editTextContent;
    private String[] logParams = new String[]{"logcat","-s","adb logcat *: W"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        buttonSend=findViewById(R.id.button);
        editTextContent=findViewById(R.id.editText);
//        startService(new Intent(this,CommucateService.class));

        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                LogBean logBean=new LogBean();
//                logBean.setTime(System.currentTimeMillis());
//                CommucateService.notifyLog(logBean);

                String baseCommand = "logcat -v time";
                baseCommand += " MyApp:I "; // Info for my app
                baseCommand += " *:S "; // Silence others

                try {
                    Process exec = Runtime.getRuntime().exec("logcat -v time *:S | grep \"Hw\"");
                    InputStream inputStream=exec.getInputStream();
                    LocalLogThread localLogThread=new LocalLogThread(inputStream);
                    localLogThread.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private class LocalLogThread extends Thread{
        private InputStream inputStream;
        private boolean stop=false;

        public LocalLogThread(InputStream inputStream) {
            this.inputStream=inputStream;
        }
        @Override
        public void run() {
            super.run();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream),1024);
            String line = null;
            LogBean logBean=new LogBean();
            try {
                while (!stop) {
                    line = reader.readLine();
                    logBean.setLocalLog(line);
                    Log.e("log","from other===="+line);
                    Log.d("log","from local");
                    CommucateService.notifyLog(logBean);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        public void stopLog(){
            stop=true;
        }
    }
}
