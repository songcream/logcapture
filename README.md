# logcapture
Step 1. 在项目的gradle中加入

	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}Copy
Step 2. 在需要使用抓包的module中加入

	dependencies {
	        implementation 'com.github.songcream:logcapture:v1.0.0'
	}
  
  完成上面两个步骤之后，就可以使用logcapture的类了，下一步需要添加OKHttp的拦截器，代码如下：
  
