package com.ecjtu.flesh.userinterface.activity

import android.content.Context
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.core.content.ContextCompat
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ecjtu.flesh.R
import com.ecjtu.flesh.db.DatabaseManager
import com.ecjtu.flesh.mvp.presenter.PageDetailContract
import com.ecjtu.flesh.mvp.presenter.PageDetailPresenter
import com.ecjtu.flesh.userinterface.adapter.PageDetailAdapter
import com.ecjtu.netcore.model.PageDetailModel

/**
 * Created by Ethan_Xiang on 2017/9/11.
 */
class PageDetailActivity : AppCompatActivity(), PageDetailContract.View {

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

    private var mDatabase: SQLiteDatabase? = null

    private var mUrl: String? = null

    private var mHref: String? = null

    private var mDescription: String? = null

    private var mImgUrl: String? = null

    private var mRecyclerView: RecyclerView? = null

    private val mPresenter: PageDetailContract.Presenter = PageDetailPresenter()

    private lateinit var actionBarDrawable: ColorDrawable

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
                mDatabase = DatabaseManager.getInstance(this)?.getDatabase()
                mUrl = url
                init()
                return
            }
        }

        finish()
    }

    private fun init() {
        mPresenter.takeView(this)

        mRecyclerView = findViewById<View>(R.id.recycler_view) as RecyclerView
        mRecyclerView?.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        val pageModel = mPresenter.readCache()
        if (pageModel != null) {
            actionBarDrawable = ColorDrawable(ContextCompat.getColor(this, R.color.colorPrimary))
            mRecyclerView?.adapter = PageDetailAdapter(pageModel)
//            mRecyclerView?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
//                override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
//                    super.onScrolled(recyclerView, dx, dy)
//                    Log.i("PageDetailActivity", "dx = $dx , dy = $dy")
//                    if (dx == 0 && dy == 0) return
//                    var alpha = actionBarDrawable.alpha + dy
//                    if (alpha < 0) {
//                        alpha = 0
//                    } else if (alpha > 255) {
//                        alpha = 255
//                    }
//                    actionBarDrawable.alpha = alpha
//                    supportActionBar?.setBackgroundDrawable(actionBarDrawable)
//                }
//            })
        }
    }

    override fun onStop() {
        super.onStop()
        mPresenter.saveCache()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_page_detail_activity, menu)
        mDatabase?.let {
            val isLike = mPresenter.isLike
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
            mDatabase?.let {
                val isLike = mPresenter.isLike
                if (isLike) {
                    item.title = getString(R.string.collection)
                    mPresenter.deleteLike()
                    Toast.makeText(this, R.string.delete_collection, Toast.LENGTH_SHORT).show()
                } else {
                    item.title = getString(R.string.delete_collection)
                    mPresenter.addLike()
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

    override fun loadDataFromInternet(model: PageDetailModel?) {
        if (model == null) {
            return
        }
        runOnUiThread {
            if (mRecyclerView?.adapter == null) {
                mRecyclerView?.adapter = PageDetailAdapter(model)
            } else {
                (mRecyclerView?.adapter as PageDetailAdapter?)?.pageModel = model
                mRecyclerView?.adapter?.notifyDataSetChanged()
            }
        }

    }

    override fun getSQLiteDatabase(): SQLiteDatabase? {
        return mDatabase
    }

    override fun getUrl(): String? {
        return mUrl
    }
}