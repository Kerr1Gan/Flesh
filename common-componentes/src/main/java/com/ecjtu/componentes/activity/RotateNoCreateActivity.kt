package com.ecjtu.componentes.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle

/**
 * Created by KerriGan on 2017/8/20.
 */
class RotateNoCreateActivity: BaseFragmentActivity(){
    companion object {
        @JvmOverloads
        @JvmStatic
        fun newInstance(context: Context, fragment: Class<*>, bundle: Bundle? = null,
                                      clazz: Class<out Activity> = getActivityClazz()): Intent {
            return BaseFragmentActivity.newInstance(context, fragment, bundle, clazz)
        }

        protected open fun getActivityClazz(): Class<out Activity> {
            return RotateNoCreateActivity::class.java
        }
    }
}