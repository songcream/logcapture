package com.songcream.main

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.OrientationHelper
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.ViewHolder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.songcream.aidl.R
import com.songcream.logcapture.LogBean

/**
 * Created by gengsong on 2018/7/1.
 */
class NetworkFragment : Fragment() {
    var recyclerView:RecyclerView?=null
    val adapter=NetworkAdapter()
    val data=ArrayList<LogBean>()

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view=LayoutInflater.from(context).inflate(R.layout.network_fragment,null)
        recyclerView=view.findViewById(R.id.recyclerView_network)
        recyclerView?.layoutManager=LinearLayoutManager(context,OrientationHelper.VERTICAL,false)
        recyclerView?.adapter=adapter
        return view
    }

    inner class NetworkAdapter:RecyclerView.Adapter<UrlViewHolder>(){

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): UrlViewHolder {
            val view=LayoutInflater.from(parent?.context).inflate(R.layout.itemview_network,null)
            return UrlViewHolder(view);
        }

        override fun getItemCount(): Int {
            return data.size
        }

        override fun onBindViewHolder(holder: UrlViewHolder?, position: Int) {
            holder?.urlText?.setText(data.get(position).url)
            holder?.urlText?.setOnClickListener({
                val intent=Intent(context,NetLogDetailActivity::class.java)
                intent.putExtra("logBean",data.get(position))
                startActivity(intent)
            })
        }
    }

    class UrlViewHolder(view:View): ViewHolder(view){
        var urlText:TextView?=null;
        init {
            urlText=view.findViewById(R.id.textView_url)
        }
    }
}