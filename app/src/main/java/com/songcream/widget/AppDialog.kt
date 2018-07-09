package com.songcream.widget

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.OrientationHelper
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.songcream.aidl.R
import com.songcream.util.AppInfo

/**
 * Created by gengsong on 2018/7/6.
 */
class AppDialog(context: Context)  {
    var alertDialog:AlertDialog;
    var contentView:View;
    var builder:AlertDialog.Builder;
    var recyclerView:RecyclerView;
    var context:Context;
    var appInfos:ArrayList<AppInfo>?=null
    var saveAppName:String?=null;
    var saveAppPackage:String?=null;

    init {
        this.context=context
        builder= AlertDialog.Builder(context)
        contentView=LayoutInflater.from(context).inflate(R.layout.app_dialog,null)
        builder.setView(contentView)
        alertDialog=builder.create()

        getAllApps()
        recyclerView= contentView.findViewById(R.id.recyclerView_apps)!!;

        recyclerView.layoutManager=LinearLayoutManager(context,OrientationHelper.VERTICAL,false)
        recyclerView.adapter=AppsAdapter()

        contentView.findViewById<View>(R.id.imageView_close_dialog).setOnClickListener({
            alertDialog.dismiss()
        })
    }

    public fun show(){
        alertDialog?.show()
        showSaveAppInfo()
    }

    companion object {
        var sp:SharedPreferences?=null;
        fun getSaveAppinfo(context: Context):AppInfo{
            val appInfo=AppInfo()
            if(sp==null){
                sp=context.getSharedPreferences("AppInfo",Context.MODE_PRIVATE)
            }
            appInfo.appName=sp?.getString("name","");
            appInfo.packageName=sp?.getString("package","");
            return appInfo
        }
    }

    private fun saveAppInfo(appInfo: AppInfo){
        if(sp==null){
            sp=context.getSharedPreferences("AppInfo",Context.MODE_PRIVATE)
        }
        val editor=sp?.edit()
        editor?.putString("name",appInfo.appName)
        editor?.putString("package",appInfo.packageName)
        editor?.commit()
    }

    private fun showSaveAppInfo(){
        if(sp==null){
            sp=context.getSharedPreferences("AppInfo",Context.MODE_PRIVATE)
        }
        saveAppName=sp?.getString("name","");
        saveAppPackage=sp?.getString("package","");

        val textViewAppName=contentView.findViewById<TextView>(R.id.textView_select_appName)
        val imageViewAppIcon=contentView.findViewById<ImageView>(R.id.imageView_select_appIcon)

        textViewAppName?.setText(saveAppName)

        val appInfo=appInfos?.find {
            it.packageName.equals(saveAppPackage)
        }
        appInfo?.setDrawable(imageViewAppIcon)
    }

    private fun getAllApps():ArrayList<AppInfo>{
        val pm = context.applicationContext.getPackageManager();
        val packgeInfos = pm.getInstalledPackages(PackageManager.MATCH_UNINSTALLED_PACKAGES);
        appInfos = ArrayList<AppInfo>();
        packgeInfos.forEach {
            val appName = it.applicationInfo.loadLabel(pm).toString();
            val packageName = it.applicationInfo.packageName;
            val applicationInfo=it.applicationInfo
            val appInfo = AppInfo(appName, packageName);
            appInfo.applicationInfo=applicationInfo
            appInfos!!.add(appInfo);
        }
        return appInfos!!
    }

    inner class AppsAdapter : RecyclerView.Adapter<AppViewHolder>(){
        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): AppViewHolder {
            val view=LayoutInflater.from(parent?.context).inflate(R.layout.itemview_app,null)
            return AppViewHolder(view)
        }

        override fun getItemCount(): Int {
            return appInfos!!.size
        }

        override fun onBindViewHolder(holder: AppViewHolder?, position: Int) {
            appInfos?.get(position)?.setDrawable(holder!!.appIcon!!)
            holder?.appName?.setText(appInfos!!.get(position).appName)
            holder?.itemView?.setOnClickListener({
                saveAppInfo(appInfos!![position])
                alertDialog.dismiss()
            })
        }
    }

    inner class AppViewHolder(view: View): RecyclerView.ViewHolder(view){
        var appIcon: ImageView?=null;
        var appName:TextView?=null;
        init {
            appIcon=view.findViewById(R.id.imageView_appIcon)
            appName=view.findViewById(R.id.textView_appName)
        }
    }
}