package com.ecjtu.heaven.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
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

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

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

    override fun onStop() {
        super.onStop()
        mDelegate?.onStop()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_page_detail_activity,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if(item?.itemId == R.menu.menu_page_detail_activity){

            return true
        }else if(item?.itemId == android.R.id.home){
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}