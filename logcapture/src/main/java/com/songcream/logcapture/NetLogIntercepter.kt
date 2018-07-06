package com.songcream.logcapture

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
            logBean.body=formatJsonString(paramsStr)
            logBean.url=oldRequest.url().encodedPath()
            logBean.response=formatJsonString(responseString)
            CommucateService.notifyLog(logBean)

            val responseBody = ResponseBody.create(mediaType, responseString)
            return response.newBuilder().body(responseBody).build()
        }else{
            return response!!
        }
    }

    fun formatJsonString(jsonSring:String):String{
        val jsonObject=JSONObject(jsonSring)
        var resultString="{\n"
        jsonObject.keys().forEach {
            resultString=resultString+it+"："+jsonObject[it]+"\n";
        }
        resultString=resultString+"}"
        return resultString
    }

}