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
	        implementation 'com.github.songcream:logcapture:v1.0.0'
	}
  
 Step 3. 完成上面两个步骤之后，就可以使用logcapture的类了，下一步需要添加OKHttp的拦截器，代码如下：（目的是在debug模式下才加入改拦	截，在正式包中加入有数据泄露的风险）
 
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
	
Step 4. Manifest.xml中加入service的声明

	<service android:name="com.songcream.logcapture.CommucateService"
            android:exported="true">
            <intent-filter>
                <action android:name="com.songcream.logcapture.CommucateService" />
            </intent-filter>
        </service>
	
Step 5. 自己编译工程里的app包或者下载apk，打开apk然后点击螺丝刀图标提示服务连接成功就可以愉快的网络抓包了
（如果遇到连不上的情况，可以尝试在手机系统里允许你自己的程序后台运行）

  
