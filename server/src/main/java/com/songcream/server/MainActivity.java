package com.songcream.server;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.songcream.logcapture.CommucateService;
import com.songcream.logcapture.LogBean;

/**
 * Created by gengsong on 2018/6/28.
 */

public class MainActivity extends Activity {
    private Button buttonSend;
    private EditText editTextContent;

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
                LogBean logBean=new LogBean();
//                logBean.setTime(System.currentTimeMillis());
                CommucateService.notifyLog(logBean);
            }
        });
    }
}
