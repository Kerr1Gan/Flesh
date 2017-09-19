package com.ecjtu.heaven.ui.activity.base

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.TextUtils
import com.ecjtu.heaven.R


/**
 * Created by KerriGan on 2017/5/21.
 */

open class BaseFragmentActivity : BaseActionActivity() {

    companion object {
        private const val TAG = "BaseFragmentActivity"
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
        setContentView(R.layout.activity_base_fragment)
        val fragmentName = intent.getStringExtra(EXTRA_FRAGMENT_NAME)
        var fragment: Fragment? = null
        if (TextUtils.isEmpty(fragmentName)) {
            //set default fragment
//            fragment = makeFragment(MainFragment::class.java!!.getName())
        } else {
            val args = intent.getBundleExtra(EXTRA_FRAGMENT_ARG)
            try {
                fragment = makeFragment(fragmentName)
                if (args != null)
                    fragment!!.arguments = args
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }

        if (fragment == null) return

        if (supportFragmentManager.findFragmentByTag(TAG) == null) {
            supportFragmentManager
                    .beginTransaction()
                    .add(R.id.fragment_container, fragment, TAG)
                    .commit()
        }
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
