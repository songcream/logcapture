package com.songcream.main

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.OrientationHelper
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.ViewHolder
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.songcream.aidl.R
import com.songcream.logcapture.LocalLogUtil
import com.songcream.logcapture.LogBean

/**
 * Created by gengsong on 2018/7/1.
 */
class LocalLogFragment : Fragment() {
    var recyclerView:RecyclerView?=null
    val adapter=LocalLogAdapter()
    val data=ArrayList<LogBean>()
    var filterData=ArrayList<LogBean>()
    var logLevel=0;
    var findString:String?=null;

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view=LayoutInflater.from(context).inflate(R.layout.network_fragment,null)
        recyclerView=view.findViewById(R.id.recyclerView_network)
        recyclerView?.layoutManager=LinearLayoutManager(context,OrientationHelper.VERTICAL,false)
        recyclerView?.adapter=adapter
        return view
    }

    fun setFilter(logLevel:Int,findString:String){
        this.logLevel=logLevel
        this.findString=findString
        filterData=LocalLogUtil.filterLocalLog(data,findString,logLevel)
        adapter.notifyDataSetChanged();
    }

    fun addData(logBean: LogBean){
        data.add(logBean)
        if(LocalLogUtil.isInLevel(logLevel,logBean.localLog)){
            if(!TextUtils.isEmpty(findString)){
                if(logBean.localLog.contains(findString!!))
                    filterData.add(logBean)
            }else{
                filterData.add(logBean)
            }
        }
        adapter.notifyDataSetChanged()
    }

    fun clear(){
        data.clear()
        filterData.clear()
        adapter.notifyDataSetChanged();
    }

    inner class LocalLogAdapter:RecyclerView.Adapter<UrlViewHolder>(){

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): UrlViewHolder {
            val view=LayoutInflater.from(parent?.context).inflate(R.layout.itemview_network,null)
            return UrlViewHolder(view);
        }

        override fun getItemCount(): Int {
            return filterData.size
        }

        override fun onBindViewHolder(holder: UrlViewHolder?, position: Int) {
            holder?.urlText?.setText(filterData.get(position).localLog)
        }
    }

    class UrlViewHolder(view:View): ViewHolder(view){
        var urlText:TextView?=null;
        init {
            urlText=view.findViewById(R.id.textView_url)
        }
    }
}