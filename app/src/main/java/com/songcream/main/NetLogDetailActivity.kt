package com.songcream.main

import android.app.Activity
import android.os.Bundle
import com.songcream.aidl.R
import com.songcream.logcapture.LogBean
import kotlinx.android.synthetic.main.activity_network_detail.*
import kotlinx.android.synthetic.main.itemview_network.*
import java.text.SimpleDateFormat

/**
 * Created by gengsong on 2018/7/1.
 */
class NetLogDetailActivity: Activity() {
    var logBean:LogBean?=null;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_network_detail)
        logBean=intent?.getParcelableExtra("logBean")
        textView_netDetail_url.setText(logBean?.url)
        textView_request.setText(logBean?.body)
        textView_response.setText(logBean?.response)
        val simpleDateFormat=SimpleDateFormat("HH:mm:ss")
        try {
            textView_time.setText(simpleDateFormat.format(logBean?.time))
        }catch (e:Exception){}
    }
}