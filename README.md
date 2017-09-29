# Flesh（果肉）

果肉一款福利满满的app，数据源[mzitu][3]，MD风格的界面。

组成
--------
1. 语言：Kotlin，Java
2. 网络请求：HttpUrlConnection
3. 数据库：Sqlite
4. 数据源：Jsoup
5. 第三方库：Glide

概述
--------
**1)** 网络请求

网络框架并没有使用RxRetrofit等，为了保证精简高效直接使用的HttpUrlConnection
+ get 
```kotlin
val request = AsyncNetwork()
request.request(Constants.HOST_MOBILE_URL, null)
request.setRequestCallback(object : IRequestCallback {
    override fun onSuccess(httpURLConnection: HttpURLConnection?, response: String) {
        //todo
    }
})
```
+ post
```kotlin
val request = AsyncNetwork()
request.request(Constants.HOST_MOBILE_URL, mutableMapOf())
request.setRequestCallback(object : IRequestCallback {
    override fun onSuccess(httpURLConnection: HttpURLConnection?, response: String) {
        //todo
    }
})
```
**2)** 数据库

数据库没有使用第三方框架，直接使用的sql语句。
```sql
CREATE TABLE tb_class_page_list ( 
                    _id           INTEGER PRIMARY KEY ASC AUTOINCREMENT,
                    href          STRING  UNIQUE,
                    description   STRING,
                    image_url     STRING,
                    id_class_page INTEGER REFERENCES tb_class_page (_id) ON DELETE CASCADE ON UPDATE CASCADE,
                    [index]       INTEGER);
```
**3)** 读写缓存

由于Serializable的效率远低于Parcelable，所以采用Parcelable实现的缓存机制，速度快了大概7，8倍。
+ 读取缓存
```kotlin
val helper = PageListCacheHelper(container?.context?.filesDir?.absolutePath)
val pageModel: Any? = helper.get(key)
```
+ 写入缓存
```kotlin
val helper = PageListCacheHelper(context.filesDir.absolutePath)
helper.put(key, object)
```
+ 删除缓存
```kotlin
val helper = PageListCacheHelper(context.filesDir.absolutePath)
helper.remove(key)
```
**4)** jsoup获取数据

由于数据是用从html页面中提取的，所以速度偏慢，为了不影响体验做了一套缓存机制，来做到流畅体验。
```java
Document doc = Jsoup.parse(html);
Elements elements = body.getElementsByTag("a");
String text = elements.get(0).text();
String imageUrl = elements.get(0).attr("src");
...
```

ProGuard
--------
```pro
-keep class org.jsoup.**{*;}
-keep public class com.ecjtu.netcore.jsoup.SoupFactory{*;}
-keep public class * extends com.ecjtu.netcore.jsoup.BaseSoup{*;}
-keep public class com.ecjtu.netcore.Constants{static <fields>;}
-keep public class com.ecjtu.netcore.model.**{*;}
-keep public class com.ecjtu.netcore.network.BaseNetwork{public <methods>;}
-keep public class * extends com.ecjtu.netcore.network.BaseNetwork{ public <methods>; }
-keep public interface com.ecjtu.netcore.network.IRequestCallback{*;}
-keep public class * extends android.support.design.widget.CoordinatorLayout$Behavior{*;}
```

Contributing
------------
contributors submmit pull requests.

Thanks
------
* The **Glide** [image loading framework][1] Flesh's image loader is based on.
* The **Bugly** [app monitoring tools][2] Flesh's log collector.
* Everyone who has contributed code and reported issues!

Author
------
KerriGan - mnsync@outlook.com or ethanxiang95@gmail.com

License
-------
[Apache2][4]

Disclaimer
---------
Only available for study and communication.If the flesh violate your rights,we can delete immediately violate to your rights and interests content.

[1]: https://github.com/bumptech/glide
[2]: https://bugly.qq.com/v2/
[3]: http://www.mzitu.com
[4]: https://github.com/Kerr1Gan/Flesh/blob/master/LICENSE
