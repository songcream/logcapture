// ILogReceiver.aidl
package com.songcream.logcapture;

import com.songcream.logcapture.LogBean;
interface ILogListener {
    void message(in LogBean logBean);
}
