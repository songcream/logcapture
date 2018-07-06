// ICommucation.aidl
package com.songcream.logcapture;

import com.songcream.logcapture.ILogListener;

// Declare any non-default types here with import statements
interface ICommucateService {
    void registerReceiver(ILogListener listener);
    void unRegisterReceiver(ILogListener listener);
}
