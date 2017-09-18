package com.ecjtu.heaven.presenter

import android.database.sqlite.SQLiteDatabase
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.ecjtu.heaven.R
import com.ecjtu.heaven.cache.PageDetailCacheHelper
import com.ecjtu.heaven.db.DatabaseManager
import com.ecjtu.heaven.db.table.impl.DetailPageTableImpl
import com.ecjtu.heaven.ui.activity.PageDetailActivity
import com.ecjtu.heaven.ui.adapter.PageDetailAdapter
import com.ecjtu.netcore.jsoup.impl.PageDetailSoup
import com.ecjtu.netcore.jsoup.SoupFactory
import com.ecjtu.netcore.model.PageDetailModel
import com.ecjtu.netcore.network.AsyncNetwork
import com.ecjtu.netcore.network.IRequestCallback
import java.net.HttpURLConnection

/**
 * Created by Ethan_Xiang on 2017/9/11.
 */
class PageDetailActivityDelegate(owner: PageDetailActivity, val url: String) : Delegate<PageDetailActivity>(owner) {

    private val mRecyclerView = owner.findViewById(R.id.recycler_view) as RecyclerView
    private var mPageModel: PageDetailModel? = null

    init {
        mRecyclerView.layoutManager = LinearLayoutManager(owner, LinearLayoutManager.VERTICAL, false)
        val helper = PageDetailCacheHelper(owner.filesDir.absolutePath)
        val local = url.substring(url.lastIndexOf("/"))
        mPageModel = helper.get(local)

        val request = AsyncNetwork()
        request.request(url, null)
        request.setRequestCallback(object : IRequestCallback {
            override fun onSuccess(httpURLConnection: HttpURLConnection?, response: String) {
                val ret = SoupFactory.parseHtml(PageDetailSoup::class.java, response, url)
                val model = ret.get(PageDetailSoup::class.java.simpleName)
                if (model != null) {
                    val local = model as PageDetailModel
                    owner.runOnUiThread {
                        if (mPageModel == null) {
                            mPageModel = local
                            mRecyclerView.adapter = PageDetailAdapter(local)
                        } else {
                            mPageModel = model
                            (mRecyclerView.adapter as PageDetailAdapter).pageModel = model
                            mRecyclerView.adapter.notifyDataSetChanged()
                        }
                    }
                    val db = DatabaseManager.getInstance(owner)?.getDatabase() as SQLiteDatabase
                    val impl = DetailPageTableImpl()
                    //todo 获取到畸形缓存列表加入数据库
                    impl.addDetailPage(db,local)
                    db.close()
                }
            }
        })

        if (mPageModel != null) {
            mRecyclerView.adapter = PageDetailAdapter(mPageModel!!)
        }
    }

    fun onStop() {
        mPageModel?.let {
            val helper = PageDetailCacheHelper(owner.filesDir.absolutePath)
            val local = url.substring(url.lastIndexOf("/"))
            helper.put(local, mPageModel)
        }
    }
}