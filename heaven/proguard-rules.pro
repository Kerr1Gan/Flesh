# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in D:\AndroidSDK/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

#glide begin
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.AppGlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}
# for DexGuard only
#-keepresourcexmlelements manifest/application/meta-data@value=GlideModule
#glide end\

#指定代码的压缩级别
-optimizationpasses 5

#包明不混合大小写
#dontusemixedcaseclassnames

#不去忽略非公共的库类
#-dontskipnonpubliclibraryclasses

#优化 不优化输入的类文件
#-dontoptimize

#预校验
#-dontpreverify

#混淆时是否记录日志
#-verbose

#混淆时所采用的算法
#-optimizations !code/simplification/arithmetic,!field/,!class/merging/

#保护注解
#-keepattributes Annotation

#-libraryjars libs/jcifs-1.3.14.1.jar

-keep class com.google.zxing.**{*;}

-keep public class * extends com.ecjtu.heaven.cache.base.FileCacheHelper
-keep public class * extends com.ecjtu.heaven.presenter.Delegate
-keep class org.jsoup.**{*;}
-keep class com.ecjtu.netcore.**{*;}
