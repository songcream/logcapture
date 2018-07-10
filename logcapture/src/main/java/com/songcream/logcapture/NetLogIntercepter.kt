package com.songcream.logcapture

import android.util.Log
import com.songcream.logcapture.LogBean
import okhttp3.*
import okio.Buffer
import org.json.JSONObject
import java.nio.charset.Charset
import java.text.SimpleDateFormat

/**
 * Created by gengsong on 2018/7/2.
 */
class NetLogIntercepter:Interceptor{
    val simpleDateFormat=SimpleDateFormat("HH:mm:ss")

    override fun intercept(chain: Interceptor.Chain?): Response {
        val response=chain?.proceed(chain.request())
        if(response?.body()!=null){
            val mediaType = response.body()!!.contentType()
            val responseString = response.body()!!.string()

            val oldRequest = chain?.request()
            val oldBody = oldRequest?.body()
            val buffer = Buffer()
            oldBody!!.writeTo(buffer)
            val paramsStr = buffer.readString(Charset.forName("UTF-8"))
            val logBean=LogBean()
            logBean.time=simpleDateFormat.format(System.currentTimeMillis())
            logBean.body=stringToJSON(paramsStr)
            logBean.url=oldRequest.url().encodedPath()
            logBean.response=stringToJSON(responseString)
            logBean.code=response.code()

            val map=HashMap<String,String?>();
            val headers=oldRequest.headers()
            headers.names().forEach({
                map.put(it,headers[it])
            })
            logBean.headsMap=map

            CommucateService.notifyNetLog(logBean)

            val responseBody = ResponseBody.create(mediaType, responseString)
            return response.newBuilder().body(responseBody).build()
        }else{
            return response!!
        }
    }

    fun stringToJSON(strJson: String): String {
        // 计数tab的个数
        var tabNum = 0
        val jsonFormat = StringBuffer()
        val length = strJson.length

        var last: Char = 0.toChar()
        for (i in 0 until length) {
            val c = strJson[i]
            if (c == '{') {
                tabNum++
                jsonFormat.append(c + "\n")
                jsonFormat.append(getSpaceOrTab(tabNum))
            } else if (c == '}') {
                tabNum--
                jsonFormat.append("\n")
                jsonFormat.append(getSpaceOrTab(tabNum))
                jsonFormat.append(c)
            } else if (c == ',') {
                jsonFormat.append(c + "\n")
                jsonFormat.append(getSpaceOrTab(tabNum))
            } else if (c == ':') {
                jsonFormat.append("$c ")
            } else if (c == '[') {
                tabNum++
                val next = strJson[i + 1]
                if (next == ']') {
                    jsonFormat.append(c)
                } else {
                    jsonFormat.append(c + "\n")
                    jsonFormat.append(getSpaceOrTab(tabNum))
                }
            } else if (c == ']') {
                tabNum--
                if (last == '[') {
                    jsonFormat.append(c)
                } else {
                    jsonFormat.append("\n" + getSpaceOrTab(tabNum) + c)
                }
            } else {
                jsonFormat.append(c)
            }
            last = c
        }
        return jsonFormat.toString()
    }

    // 是空格还是tab
    private fun getSpaceOrTab(tabNum: Int): String {
        val sbTab = StringBuffer()
        for (i in 0 until tabNum) {
            sbTab.append('\t')
        }
        return sbTab.toString()
    }

}