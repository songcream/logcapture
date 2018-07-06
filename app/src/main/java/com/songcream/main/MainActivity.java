package com.songcream.main;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.songcream.aidl.R;
import com.songcream.logcapture.ICommucateService;
import com.songcream.logcapture.ILogListener;
import com.songcream.logcapture.LogBean;

import java.util.List;

/**
 * Created by gengsong on 2018/6/28.
 */

public class MainActivity extends Activity {
    private Button buttonSend;
    private EditText editTextContent;
    private ICommucateService iCommucateService;
    private ServiceConnection serviceConnection=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            iCommucateService=ICommucateService.Stub.asInterface(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            iCommucateService=null;
        }

        @Override
        public void onBindingDied(ComponentName name) {
            ComponentName a=name;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        buttonSend=findViewById(R.id.button);
        editTextContent=findViewById(R.id.editText);
        Intent intent=new Intent();
        intent.setComponent(new ComponentName("com.huawei.honorclub.android", "com.songcream.logcapture.CommucateService"));
//        startService(intent);
        bindService(intent,serviceConnection, Context.BIND_AUTO_CREATE);

        final ILogListener.Stub iLogListener=new ILogListener.Stub() {
            @Override
            public void message(final LogBean logBean) throws RemoteException {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        editTextContent.setText(logBean.getTime()+"");
                    }
                });
            }
        };

        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(iCommucateService!=null){
                    try {
                        iCommucateService.registerReceiver(iLogListener);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(serviceConnection);
    }
}
