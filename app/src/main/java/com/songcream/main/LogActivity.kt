package com.songcream.main

import android.os.Bundle
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import com.songcream.aidl.R
import com.songcream.logcapture.ICommucateService
import com.songcream.logcapture.ILogListener
import com.songcream.logcapture.LogBean
import com.songcream.util.SystemUtil

import kotlinx.android.synthetic.main.activity_log.*
import com.songcream.widget.AppDialog
import com.songcream.widget.FilterDialog


class LogActivity : FragmentActivity() ,View.OnClickListener{
    val fragmentList=ArrayList<Fragment>()
    var networkFragment:NetworkFragment?=null;
    var localLogFragment:LocalLogFragment?=null;
    var iCommucateService:ICommucateService?=null;
    var iLogListener:ILogListener.Stub?=null;
    var appDialog:AppDialog?=null;
    var filterDialog:FilterDialog?=null;
    var connectPackage:String?=null;
    var serviceConnection=object :ServiceConnection{
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
        localLogFragment= LocalLogFragment()
        fragmentList.add(networkFragment!!)
        fragmentList.add(localLogFragment!!)
        viewPager.offscreenPageLimit=2

        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
                if(position==0){
                    floatingButton_logFilter.visibility= View.GONE
                }else{
                    floatingButton_logFilter.visibility= View.VISIBLE
                }
            }
        })
        floatingButton_logFilter.setOnClickListener(this)
        floatingButton_clear.setOnClickListener(this)
        floatingButton_connect.setOnLongClickListener({
            if(appDialog==null) {
                appDialog = AppDialog(this@LogActivity)
            }
            appDialog!!.show()
            true
        })
        floatingButton_connect.setOnClickListener(this)
        iLogListener=object :ILogListener.Stub(){
            override fun message(logBean: LogBean?) {
                if(logBean==null) return;
                Log.e("log",logBean?.code.toString());
                if(TextUtils.isEmpty(logBean?.localLog)) {
                    networkFragment?.data?.add(logBean!!)
                    runOnUiThread({
                        networkFragment?.adapter?.notifyDataSetChanged()
                    })
                }else{
                    runOnUiThread({
                        localLogFragment!!.addData(logBean!!)
                    })
                }
            }
        }
        viewPager.adapter=LogPagerAdapter(supportFragmentManager);
        tabLayout.setupWithViewPager(viewPager)
        tabLayout.getTabAt(0)?.setText("network")
        tabLayout.getTabAt(1)?.setText("local")

        val intent=Intent()
        connectPackage=AppDialog.getSaveAppinfo(this).packageName
        intent.component = ComponentName(connectPackage, "com.songcream.logcapture.CommucateService")
        bindService(intent,serviceConnection, Context.BIND_AUTO_CREATE)
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.floatingButton_clear -> {
                if(viewPager.currentItem==0) {
                    networkFragment?.data?.clear()
                    networkFragment?.adapter?.notifyDataSetChanged()
                }else{
                    localLogFragment?.clear()
                }
            }
            R.id.floatingButton_connect ->{
                if(iCommucateService==null){
                    val intent=Intent()
                    connectPackage=AppDialog.getSaveAppinfo(this).packageName
                    if(TextUtils.isEmpty(connectPackage)){
                        Toast.makeText(this,"请先设置您要连接的应用",Toast.LENGTH_SHORT).show()
                        return
                    }
                    intent.component = ComponentName(connectPackage, "com.songcream.logcapture.CommucateService")
                    bindService(intent,serviceConnection, Context.BIND_AUTO_CREATE)
                }else{
                    if(!connectPackage.equals(AppDialog.getSaveAppinfo(this).packageName)){
                        try {
                            unBindService(true)
                        }catch (e:Exception){
                            Toast.makeText(this, "无法连接该应用", Toast.LENGTH_SHORT).show()
                        }
                    }else {
                        Toast.makeText(this, "服务已经连接上", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            R.id.floatingButton_logFilter -> {
                if(filterDialog==null){
                    filterDialog= object:FilterDialog(this@LogActivity){
                        override fun onConfirm(logLevel: Int, findString: String) {
                            localLogFragment?.setFilter(logLevel,findString)
                        }
                    }
                }
                filterDialog?.show()
            }
        }
    }

    private fun unBindService(reBindService:Boolean){
        iCommucateService?.unRegisterReceiver(iLogListener)
        iCommucateService=null
        Toast.makeText(this@LogActivity,"原服务已断开",Toast.LENGTH_SHORT).show()

        if(reBindService) {
            serviceConnection=object :ServiceConnection{
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
            val intent = Intent()
            connectPackage = AppDialog.getSaveAppinfo(this@LogActivity).packageName
            intent.component = ComponentName(connectPackage, "com.songcream.logcapture.CommucateService")
            bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unBindService(false)
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
