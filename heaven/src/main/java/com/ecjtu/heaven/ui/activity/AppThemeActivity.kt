package com.ecjtu.heaven.ui.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.ecjtu.heaven.ui.activity.base.BaseFragmentActivity

/**
 * Created by Ethan_Xiang on 2017/9/18.
 */
class AppThemeActivity : BaseFragmentActivity() {
    companion object {
        @JvmOverloads
        @JvmStatic
        fun newInstance(context: Context, fragment: Class<*>, bundle: Bundle? = null,
                        clazz: Class<out Activity> = getActivityClazz()): Intent {
            return BaseFragmentActivity.newInstance(context, fragment, bundle, getActivityClazz())
        }

        protected fun getActivityClazz(): Class<out Activity> {
            return AppThemeActivity::class.java
        }
    }
}