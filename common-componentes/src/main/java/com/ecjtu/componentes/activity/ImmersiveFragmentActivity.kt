package com.ecjtu.componentes.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager

/**
 * Created by KerriGan on 2017/7/12.
 */
open class ImmersiveFragmentActivity : BaseFragmentActivity() {
    companion object {
        @JvmOverloads
        @JvmStatic
        fun newInstance(context: Context, fragment: Class<*>, bundle: Bundle? = null,
                        clazz: Class<out Activity> = getActivityClazz()): Intent {
            return BaseFragmentActivity.newInstance(context, fragment, bundle, clazz)
        }

        fun getActivityClazz(): Class<out Activity> = ImmersiveFragmentActivity::class.java
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)   //去除半透明状态栏
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN) //一般配合fitsSystemWindows()使用, 或者在根部局加上属性android:fitsSystemWindows="true", 使根部局全屏显示
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.TRANSPARENT)
        }

        if (Build.VERSION.SDK_INT >= 24/*Build.VERSION_CODES.N*/) {
            try {
                val decorViewClazz = Class.forName("com.android.internal.policy.DecorView")
                val field = decorViewClazz.getDeclaredField("mSemiTransparentStatusBarColor")
                field.isAccessible = true
                field.setInt(window.decorView, Color.TRANSPARENT)  //改为透明
            } catch (e: Exception) {
            }
        }
    }
}