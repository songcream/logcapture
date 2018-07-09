package com.songcream.logcapture;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.text.TextUtils;

/**
 * Created by gengsong on 2018/6/29.
 */

public class CommucateService extends Service {
    private static RemoteCallbackList<ILogListener> localLogCallbacks=new RemoteCallbackList<>();
    private static RemoteCallbackList<ILogListener> netLogCallbacks=new RemoteCallbackList<>();
    private ICommucateService.Stub iCommucateService=new ICommucateService.Stub() {
        @Override
        public void registerLocalReceiver(ILogListener listener) throws RemoteException {
            if(listener!=null){
                localLogCallbacks.register(listener);
            }
        }

        @Override
        public void unRegisterLocalReceiver(ILogListener listener) throws RemoteException {
            if(listener!=null){
                localLogCallbacks.unregister(listener);
            }
        }

        @Override
        public void registerNetReceiver(ILogListener listener) throws RemoteException {
            if(listener!=null){
                netLogCallbacks.register(listener);
            }
        }

        @Override
        public void unRegisterNetReceiver(ILogListener listener) throws RemoteException {
            if(listener!=null){
                netLogCallbacks.unregister(listener);
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
        RemoteCallbackList<ILogListener> remoteCallbackList;
        if(!TextUtils.isEmpty(logBean.getLocalLog())){
            remoteCallbackList=localLogCallbacks;
        }else if(!TextUtils.isEmpty(logBean.getUrl())){
            remoteCallbackList=netLogCallbacks;
        }else{
            return;
        }
        count = remoteCallbackList.beginBroadcast();
        if (count == 0) {
            remoteCallbackList.finishBroadcast();
            return;
        }
        try {
            for (int i = 0; i < count; i++) {
                remoteCallbackList.getBroadcastItem(i).message(logBean);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        remoteCallbackList.finishBroadcast();
    }
}
