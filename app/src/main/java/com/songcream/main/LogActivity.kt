package com.songcream.main

import android.os.Bundle
import android.app.Activity
import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.widget.Toast
import com.songcream.aidl.R
import com.songcream.logcapture.ICommucateService
import com.songcream.logcapture.ILogListener
import com.songcream.logcapture.LogBean
import com.songcream.util.SystemUtil

import kotlinx.android.synthetic.main.activity_log.*

class LogActivity : FragmentActivity() {
    val fragmentList=ArrayList<Fragment>()
    var networkFragment:NetworkFragment?=null;
    var iCommucateService:ICommucateService?=null;
    var iLogListener:ILogListener.Stub?=null;
    val serviceConnection=object :ServiceConnection{
        override fun onServiceDisconnected(name: ComponentName?) {
            iCommucateService?.unRegisterReceiver(iLogListener)
            iCommucateService=null
            Toast.makeText(this@LogActivity,"服务已断开",Toast.LENGTH_SHORT).show()
        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            iCommucateService=ICommucateService.Stub.asInterface(service)
            iCommucateService?.registerReceiver(iLogListener)
            Toast.makeText(this@LogActivity,"服务已经连接上",Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log)
        SystemUtil.setStatusTransparent(this,true)
        tabLayout.addTab(tabLayout.newTab())
        tabLayout.addTab(tabLayout.newTab())
        networkFragment= NetworkFragment()
        fragmentList.add(networkFragment!!)
        fragmentList.add(NetworkFragment())
        floatingButton_clear.setOnClickListener({
            networkFragment?.data?.clear()
            networkFragment?.adapter?.notifyDataSetChanged()
        })
        floatingButton_connect.setOnClickListener({
            if(iCommucateService==null){
                val intent=Intent()
                intent.component = ComponentName("com.huawei.honorclub.android", "com.songcream.logcapture.CommucateService")
                bindService(intent,serviceConnection, Context.BIND_AUTO_CREATE)
            }else{
                Toast.makeText(this,"服务已经连接上",Toast.LENGTH_SHORT).show()
            }
        })
        iLogListener=object :ILogListener.Stub(){
            override fun message(logBean: LogBean?) {
                networkFragment?.data?.add(logBean!!)
                networkFragment?.adapter?.notifyDataSetChanged()
            }
        }
        viewPager.adapter=LogPagerAdapter(supportFragmentManager);
        tabLayout.setupWithViewPager(viewPager)
        tabLayout.getTabAt(0)?.setText("network")
        tabLayout.getTabAt(1)?.setText("local")

        val intent=Intent()
        intent.component = ComponentName("com.huawei.honorclub.android", "com.songcream.logcapture.CommucateService")
        bindService(intent,serviceConnection, Context.BIND_AUTO_CREATE)
    }

    override fun onDestroy() {
        super.onDestroy()
        unbindService(serviceConnection)
    }

    inner class LogPagerAdapter(fragmentManager: FragmentManager): FragmentPagerAdapter(fragmentManager) {
        override fun getItem(position: Int): Fragment {
            return fragmentList.get(position)
        }

        override fun getCount(): Int {
            return fragmentList.size
        }
    }
}
