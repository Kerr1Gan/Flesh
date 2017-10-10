package com.ecjtu.componentes

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle

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
}