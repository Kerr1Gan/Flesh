package com.ecjtu.componentes.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.MenuItem

class ActionBarFragmentActivity : BaseFragmentActivity() {
    companion object {
        const private val EXTRA_TITLE = "extra_title"
        @JvmOverloads
        @JvmStatic
        fun newInstance(context: Context, fragment: Class<*>, bundle: Bundle? = null,
                        title: String? = null, clazz: Class<out Activity> = getActivityClazz()): Intent {
            val ret = newInstance(context, fragment, bundle, clazz)
            if (!TextUtils.isEmpty(title)) {
                ret.putExtra(EXTRA_TITLE, title)
            }
            return ret
        }

        fun getActivityClazz(): Class<out Activity> = ActionBarFragmentActivity::class.java
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val title = intent.getStringExtra(EXTRA_TITLE)
        if (!TextUtils.isEmpty(title)) {
            supportActionBar?.setTitle(title)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): kotlin.Boolean {
        if (item?.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}