// ICommucation.aidl
package com.songcream.logcapture;

import com.songcream.logcapture.ILogListener;

// Declare any non-default types here with import statements
interface ICommucateService {
    void registerLocalReceiver(ILogListener listener);
    void unRegisterLocalReceiver(ILogListener listener);

    void registerNetReceiver(ILogListener listener);
    void unRegisterNetReceiver(ILogListener listener);
}
