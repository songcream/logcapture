package com.songcream.util

import android.content.Context
import android.content.pm.ApplicationInfo
import android.graphics.drawable.Drawable
import android.util.Log
import android.widget.ImageView
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


/**
 * Created by gengsong on 2018/7/7.
 */
class AppInfo {
    internal var applicationInfo:ApplicationInfo?=null;
    internal var appName: String? = null
    internal var packageName: String? = null
    var drawable: Drawable?=null;

    constructor() {}

    constructor(appName: String) {
        this.appName = appName
    }

    constructor(appName: String, packageName: String) {
        this.appName = appName
        this.packageName = packageName
    }

    constructor(appName: String, packageName: String, drawable: Drawable) {
        this.appName = appName
        this.packageName = packageName
        this.drawable = drawable
    }

    fun setDrawable(imageView: ImageView){
        if(drawable==null){
            Observable.just(applicationInfo)
                    .map {
                        this.drawable=it?.loadIcon(imageView.context.packageManager)
                        this.drawable
                    }
                    .observeOn(Schedulers.io())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        imageView.setImageDrawable(it)
                    })
        }else{
            imageView.setImageDrawable(drawable)
        }
    }


    fun getAppName(): String {
        return if (null == appName)
            ""
        else
            appName!!
    }

    fun setAppName(appName: String) {
        this.appName = appName
    }

    fun getPackageName(): String {
        return if (null == packageName)
            ""
        else
            packageName!!
    }

    fun setPackageName(packageName: String) {
        this.packageName = packageName
    }

}