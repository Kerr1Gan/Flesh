# Flesh（果肉）

果肉一款福利满满的app，数据源[mzitu][3]，MD风格的界面。

如果你是一位想学习一下Kotlin的同学，那么绝对不要错过Flesh。如Kotlin所说它与Java完美兼容，所以这里有Kotlin调用Java，同时也有Java调用Kotlin。果肉将会不定期更新，增加更多福利。

国际惯例，先上福利。[Release1.0](https://github.com/Kerr1Gan/Flesh/releases/download/170929/flesh-release.apk)

![fuli](art/fuli.gif)

特点
--------
1. 列表显示图片，点击查看更多。
2. 快速跳转至顶部，底部，指定位置。
3. 收藏，查看历史记录。
4. 设置壁纸。
5. 离线缓存。

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

Serializable的效率远低于Parcelable，所以采用Parcelable实现的缓存机制，速度快了大概7，8倍。
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
**5)** 组件
+ Activity fragment替代activity来显示新界面

    因为activity需要在Manifiest中注册，所以当有多个activity的时候，就需要编写很长的Manifiest文件，严重影响了Manifiest的可读性，界面的风格也比较笨重。所以一个新页面就注册一个activity不太合适，我们通过用activity做为容器添加不同的Fragment来达到注册一个activity启动多个不同页面的效果。生命周期由activity管理，更方便简洁。
    ```kotlin
    open class BaseFragmentActivity : BaseActionActivity() {

        companion object {

            private const val EXTRA_FRAGMENT_NAME = "extra_fragment_name"
            private const val EXTRA_FRAGMENT_ARG = "extra_fragment_arguments"

            @JvmOverloads
            @JvmStatic
            fun newInstance(context: Context, fragment: Class<*>, bundle: Bundle? = null,
                                          clazz: Class<out Activity> = getActivityClazz()): Intent {
                val intent = Intent(context, clazz)
                intent.putExtra(EXTRA_FRAGMENT_NAME, fragment.name)
                intent.putExtra(EXTRA_FRAGMENT_ARG, bundle)
                return intent
            }

            protected open fun getActivityClazz(): Class<out Activity> {
                return BaseFragmentActivity::class.java
            }
        }

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.cc_activity_base_fragment)
            val fragmentName = intent.getStringExtra(EXTRA_FRAGMENT_NAME)
            var fragment: Fragment? = null
            if (TextUtils.isEmpty(fragmentName)) {
                //set default fragment
                //fragment = makeFragment(MainFragment::class.java!!.getName())
            } else {
                val args = intent.getBundleExtra(EXTRA_FRAGMENT_ARG)
                try {
                    fragment = makeFragment(fragmentName)
                    if (args != null)
                        fragment?.arguments = args
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }

            if (fragment == null) return

            supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit()
        }

        fun makeFragment(name: String): Fragment? {
            try {
                val fragmentClazz = Class.forName(name)
                return fragmentClazz.newInstance() as Fragment
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return null
        }

    }
    ```


**6)** 序列化性能

性能测试，Serializable VS Externalizable，为了避免干扰，我们使用AndroidTest进行测试。

模型
```java
class Model1 implements Serializable {
    String text;
    int code;
    boolean bool;
    Model1 child;
}

class Model2 extends Model1 implements Externalizable {

    public Model2() {
    }

    @Override
    public void readExternal(ObjectInput input) throws IOException, ClassNotFoundException {
        text = input.readUTF();
        code = input.readInt();
        bool = input.readBoolean();

        child = new Model2();
        child.text = input.readUTF();
        child.code = input.readInt();
        child.bool = input.readBoolean();
    }

    @Override
    public void writeExternal(ObjectOutput output) throws IOException {
        output.writeUTF(text);
        output.writeInt(code);
        output.writeBoolean(bool);
        if (child != null) {
            output.writeUTF(child.text);
            output.writeInt(child.code);
            output.writeBoolean(child.bool);
        }
    }
}
```
测试
```java
@Test
public void serializableVSExternalizable() throws Exception {
    List<Model1> testModel1 = new ArrayList<>();
    for (int i = 0; i < 50000; i++) {
        Model1 model1 = new Model1();
        model1.text = "Hello World " + i;
        model1.code = i;
        model1.bool = false;

        Model1 child = new Model1();
        child.text = "Hello World Child" + i;
        child.code = i;
        child.bool = false;

        model1.child = child;
        testModel1.add(model1);
    }
    long startTime = System.currentTimeMillis();
    File file = new File("/sdcard/serializable");
    ObjectOutputStream oStream = new ObjectOutputStream(new FileOutputStream(file));
    oStream.writeObject(testModel1);
    oStream.close();
    Log.e("serializable", "write time " + (System.currentTimeMillis() - startTime));
    startTime = System.currentTimeMillis();
    ObjectInputStream iStream = new ObjectInputStream(new FileInputStream(file));
    testModel1 = (List<Model1>) iStream.readObject();
    iStream.close();
    Log.e("serializable", "read time " + (System.currentTimeMillis() - startTime));
    testModel1 = null;

    List<Model2> testModel2 = new ArrayList<>();
    for (int i = 0; i < 50000; i++) {
        Model2 model2 = new Model2();
        model2.text = "Hello World " + i;
        model2.code = i;
        model2.bool = false;

        Model2 child = new Model2();
        child.text = "Hello World Child" + i;
        child.code = i;
        child.bool = false;

        model2.child = child;
        testModel2.add(model2);
    }
    startTime = System.currentTimeMillis();
    file = new File("/sdcard/externalizable");
    oStream = new ObjectOutputStream(new FileOutputStream(file));
    oStream.writeObject(testModel2);
    oStream.close();
    Log.e("externalizable", "write time " + (System.currentTimeMillis() - startTime));
    startTime = System.currentTimeMillis();
    iStream = new ObjectInputStream(new FileInputStream(file));
    testModel2 = (List<Model2>) iStream.readObject();
    iStream.close();
    Log.e("externalizable", "read time " + (System.currentTimeMillis() - startTime));
}
```
结果
```
序列化5000个对象
Serializable：写入耗时4026 ms，读取耗时177 ms
Externalizable：写入耗时2680 ms，读取耗时79 ms

序列化50000个对象
Serializable：写入耗时46872 ms，读取耗时1807 ms
Externalizable：写入耗时41334 ms，读取耗时792 ms
```

从结果上可以看到Externalizalbe相比于Serializable是稍微快一些点不管是写入还是读取速度。对象存储还可以使用一些对象关系映射（ORM）型的数据库。如[GreenDao][5]等等。

**7)** Java中的深拷贝

由于System.arrayCopy()该方法拷贝数组的时候，如果是基本数据类型则是深拷贝，如果是对象类型则会是浅拷贝，无法做到深拷贝，所以想深拷贝一个数组就得循环创建对象并赋值，这显得很麻烦。所以项目中使用序列化的方法进行深拷贝。PS：Serializable序列化方式读取的时候并不会调用对象构造方法，而Externalizable序列化方式读取时会调用对象的无参构造方法。
```java
@SuppressWarnings("unchecked")
public static <T> T deepCopyOrThrow(T src) throws IOException, ClassNotFoundException {
    ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
    ObjectOutputStream out = new ObjectOutputStream(byteOut);
    out.writeObject(src);

    ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
    ObjectInputStream in = new ObjectInputStream(byteIn);
    return (T) in.readObject();
}

public static <T> T deepCopy(T src) {
    try {
        return deepCopyOrThrow(src);
    } catch (Exception ignore) {
        ignore.printStackTrace();
        return null;
    }
}
```
**8)** 释放进程资源

直接调用System.exit()方法可释放所在进程的资源，腾出内存给其他组件使用，减少被系统回收的概率。

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
[5]: http://greenrobot.org/greendao/
