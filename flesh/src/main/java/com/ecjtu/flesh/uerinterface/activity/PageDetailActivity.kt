package com.ecjtu.flesh.uerinterface.activity

import android.content.Context
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.ecjtu.flesh.R
import com.ecjtu.flesh.db.DatabaseManager
import com.ecjtu.flesh.db.table.impl.LikeTableImpl
import com.ecjtu.flesh.mvp.presenter.PageDetailActivityDelegate

/**
 * Created by Ethan_Xiang on 2017/9/11.
 */
class PageDetailActivity : AppCompatActivity() {
    companion object {
        private const val EXTRA_DETAIL_URL = "extra_detail_url"
        private const val EXTRA_HREF = "extra_href"
        private const val EXTRA_DESCRIPTION = "extra_description"
        private const val EXTRA_IMG_URL = "extra_img_url"
        fun newInstance(context: Context, url: String, href: String, description: String, imgUrl: String): Intent {
            return Intent(context, PageDetailActivity::class.java).apply {
                putExtra(EXTRA_DETAIL_URL, url)
                putExtra(EXTRA_HREF, href)
                putExtra(EXTRA_DESCRIPTION, description)
                putExtra(EXTRA_IMG_URL, imgUrl)
            }
        }
    }

    private var mDelegate: PageDetailActivityDelegate? = null

    private var mDatabase: SQLiteDatabase? = null

    private var mUrl: String? = null

    private var mHref: String? = null

    private var mDescription: String? = null

    private var mImgUrl: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_page_detail)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val intent = intent
        val bundle = intent.extras
        if (bundle != null) {
            val url = bundle.getString(EXTRA_DETAIL_URL, "")
            mHref = bundle.getString(EXTRA_HREF, "")
            mDescription = bundle.getString(EXTRA_DESCRIPTION, "")
            mImgUrl = bundle.getString(EXTRA_IMG_URL, "")
            if (url != "") {
                mDelegate = PageDetailActivityDelegate(this, url)
                mDatabase = DatabaseManager.getInstance(this)?.getDatabase()
                mUrl = url
                init()
                return
            }
        }

        finish()
    }

    private fun init() {

    }

    override fun onStop() {
        super.onStop()
        mDelegate?.onStop()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_page_detail_activity, menu)
        val impl = LikeTableImpl()
        mDatabase?.let {
            val isLike = impl.isLike(mDatabase!!, mUrl!!)
            val item = menu?.findItem(R.id.item)
            if (isLike) {
                item?.title = getString(R.string.delete_collection)
            } else {
                item?.title = getString(R.string.collection)
            }
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == R.id.item) {
            val impl = LikeTableImpl()
            mDatabase?.let {
                val isLike = impl.isLike(mDatabase!!, mUrl!!)
                if (isLike) {
                    item.title = getString(R.string.collection)
                    impl.deleteLike(mDatabase!!, mUrl!!)
                    Toast.makeText(this, R.string.delete_collection, Toast.LENGTH_SHORT).show()
                } else {
                    item.title = getString(R.string.delete_collection)
                    impl.addLike(mDatabase!!, mUrl!!)
                    Toast.makeText(this, R.string.collect_success, Toast.LENGTH_SHORT).show()
                }
            }
            return true
        } else if (item?.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()
        mDatabase?.close()
    }
}