# logcapture
Step 1. 在项目的gradle中加入

	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
Step 2. 在需要使用抓包的module中加入

	dependencies {
	        debugImplementation 'com.github.songcream:logcapture:v1.0.3'
	}
  
Step 3. 完成上面两个步骤之后，就可以使用logcapture的类了，以下分为两个小步，分别集成网络抓包和本地logcat抓包，根据自己的需要集成

1、集成网络抓包

 需要添加OKHttp的拦截器，代码如下：（目的是在debug模式下才加入改拦	截，在正式包中加入有数据泄露的风险）
 
 	public static OkHttpClient.Builder addLogCaptureInterceptor(OkHttpClient.Builder builder){
		try{
		    Class<?> stethoInterceptor=Class.forName("com.songcream.logcapture.NetLogIntercepter");
		    Object instance=stethoInterceptor.newInstance();
		    builder.addInterceptor((Interceptor) instance);
		}catch (Exception e){
		    e.printStackTrace();
		}
		return builder;
   	}
	
	OkHttpClient.Builder builder = new OkHttpClient.Builder();
	if(BuildConfig.DEBUG) {
                addLogCaptureInterceptor(builder);
        }
	
  Manifest.xml中加入service的声明

	<service android:name="com.songcream.logcapture.CommucateService"
            android:exported="true">
            <intent-filter>
                <action android:name="com.songcream.logcapture.CommucateService" />
            </intent-filter>
        </service>
	
2、集成本地logcat抓包

  在MainActivity中添加如下代码即可：
  
	  onCreate():
		if(BuildConfig.DEBUG){
		    try {
			Class<?> localLogClass = Class.forName("com.songcream.logcapture.LocalLogUtil");
			Method initialize = localLogClass.getMethod("startLog");
			initialize.invoke(null);
		    } catch (Exception e) {
			e.printStackTrace();
		    }
		}

	  onDestroy():
		if(BuildConfig.DEBUG){
		    try {
			Class<?> localLogClass = Class.forName("com.songcream.logcapture.LocalLogUtil");
			Method initialize = localLogClass.getMethod("stopLog");
			initialize.invoke(null);
		    } catch (Exception e) {
			e.printStackTrace();
		    }
		}

Step 4. 自己编译工程里的app包或者下载工程根目录下的apk安装，打开apk然后长按螺丝刀图标可以配置要连接的应用，然后点击螺丝刀图标提示服务连接成功就可以愉快的抓包了
（如果遇到连不上的情况，可以尝试在手机系统里允许你自己的程序后台运行）

 ![image](https://github.com/songcream/logcapture/blob/master/pic1.jpg)
 
 ![image](https://github.com/songcream/logcapture/blob/master/pic.jpg)

