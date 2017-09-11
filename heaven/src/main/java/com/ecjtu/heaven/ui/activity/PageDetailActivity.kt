package com.ecjtu.heaven.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.ecjtu.heaven.R
import com.ecjtu.heaven.presenter.PageDetailActivityDelegate

/**
 * Created by Ethan_Xiang on 2017/9/11.
 */
class PageDetailActivity : AppCompatActivity() {
    companion object {
        private const val EXTRA_DETAIL_URL = "extra_detail_url"
        fun newInstance(context: Context, url: String): Intent {
            return Intent(context, PageDetailActivity::class.java).apply {
                putExtra(EXTRA_DETAIL_URL, url)
            }
        }
    }

    private var mDelegate: PageDetailActivityDelegate? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_page_detail)

        val intent = intent
        val bundle = intent.extras
        if (bundle != null) {
            val url = bundle.getString(EXTRA_DETAIL_URL, "")
            if (url != "") {
                mDelegate = PageDetailActivityDelegate(this, url)
                return
            }
        }

        finish()
    }


}