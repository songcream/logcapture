package com.songcream.logcapture;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;

/**
 * Created by gengsong on 2018/6/29.
 */

public class CommucateService extends Service {
    private static RemoteCallbackList<ILogListener> callbackList=new RemoteCallbackList<>();
    private ICommucateService.Stub iCommucateService=new ICommucateService.Stub() {
        @Override
        public void registerReceiver(ILogListener listener) throws RemoteException {
            if(listener!=null) {
                callbackList.register(listener);
            }
        }

        @Override
        public void unRegisterReceiver(ILogListener listener) throws RemoteException {
            if(listener!=null){
                callbackList.unregister(listener);
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return iCommucateService;
    }

    public static void notifyLog(LogBean logBean){
        int count = 0;
        count = callbackList.beginBroadcast();
        if (count == 0) {
            return;
        }
        try {
            for (int i = 0; i < count; i++) {
                callbackList.getBroadcastItem(i).message(logBean);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        } finally {
            callbackList.finishBroadcast();
        }
    }
}
