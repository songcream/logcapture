package com.songcream.widget

import android.content.Context
import android.content.DialogInterface
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
import android.widget.*
import com.songcream.aidl.R
import com.songcream.logcapture.LocalLogUtil
import com.songcream.util.AppInfo
import com.songcream.util.FilterConfig
import org.w3c.dom.Text


/**
 * Created by gengsong on 2018/7/6.
 */
abstract class FilterDialog(context: Context)  {
    val logLevels= arrayOf(LocalLogUtil.Log_V,LocalLogUtil.Log_D,LocalLogUtil.Log_I,LocalLogUtil.Log_W,LocalLogUtil.Log_E)
    var alertDialog:AlertDialog;
    var contentView:View;
    var builder:AlertDialog.Builder;
    var context:Context;
    var logLevel:Int=0;
    var findString:String="";
    var textViewLogLevel:TextView?=null;
    var editTextFindString:EditText?=null;
    var spinner:Spinner?=null;

    init {
        this.context=context
        builder= AlertDialog.Builder(context)
        contentView=LayoutInflater.from(context).inflate(R.layout.filter_dialog,null)
        builder.setView(contentView)
        builder.setPositiveButton("confirm",object : DialogInterface.OnClickListener{
            override fun onClick(dialog: DialogInterface?, which: Int) {
                val filterConfig=FilterConfig()
                filterConfig.findString=editTextFindString?.text.toString()
                filterConfig.filterLevel=spinner!!.selectedItemPosition
                saveConfig(filterConfig)
                val newLogLevel=filterConfig.filterLevel
                val newFindString=filterConfig.findString!!
                if(logLevel != newLogLevel && !findString.equals(newFindString)){
                    logLevel=newLogLevel
                    findString=newFindString
                    onConfirm(logLevel, findString)
                }
            }
        })

        textViewLogLevel=contentView.findViewById(R.id.editText_findString)
        editTextFindString=contentView.findViewById(R.id.editText_findString);
        spinner=contentView.findViewById(R.id.spinner);

        alertDialog=builder.create()
        val adapter = ArrayAdapter<String>(context,R.layout.spinner_item, logLevels)
        spinner?.adapter=adapter;
    }

    abstract fun onConfirm(logLevel:Int,findString:String)

    public fun show(){
        alertDialog?.show()
        showSaveAppInfo()
    }

    companion object {
        var sp:SharedPreferences?=null;
        fun getFilterConfig(context: Context):FilterConfig{
            val config=FilterConfig()
            if(sp==null){
                sp=context.getSharedPreferences("filterConfig",Context.MODE_PRIVATE)
            }
            config.filterLevel=sp!!.getInt("filterLevel",0);
//            config.findString=sp?.getString("findString","");
            return config
        }
    }

    private fun saveConfig(config:FilterConfig){
        if(sp==null){
            sp=context.getSharedPreferences("filterConfig",Context.MODE_PRIVATE)
        }
        val editor=sp?.edit()
        editor?.putInt("filterLevel",config.filterLevel)
//        editor?.putString("findString",config.findString)
        editor?.commit()
    }

    private fun showSaveAppInfo(){
        if(sp==null){
            sp=context.getSharedPreferences("filterConfig",Context.MODE_PRIVATE)
        }
        logLevel=sp!!.getInt("filterLevel",0);
//        findString=sp?.getString("findString","");

        spinner?.setSelection(logLevel)
    }

}