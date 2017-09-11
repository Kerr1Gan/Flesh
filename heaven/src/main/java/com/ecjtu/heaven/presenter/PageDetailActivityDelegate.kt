package com.ecjtu.heaven.presenter

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.ecjtu.heaven.R
import com.ecjtu.heaven.ui.activity.PageDetailActivity
import com.ecjtu.heaven.ui.adapter.PageDetailAdapter
import com.ecjtu.netcore.jsoup.PageDetailSoup
import com.ecjtu.netcore.jsoup.SoupFactory
import com.ecjtu.netcore.model.PageDetailModel
import com.ecjtu.sharebox.network.AsyncNetwork
import com.ecjtu.sharebox.network.IRequestCallback
import java.net.HttpURLConnection

/**
 * Created by Ethan_Xiang on 2017/9/11.
 */
class PageDetailActivityDelegate(owner: PageDetailActivity, url: String) : Delegate<PageDetailActivity>(owner) {

    private val mRecyclerView = owner.findViewById(R.id.recycler_view) as RecyclerView

    init {
        mRecyclerView.layoutManager = LinearLayoutManager(owner, LinearLayoutManager.VERTICAL, false)
        val request = AsyncNetwork()
        request.request(url)
        request.setRequestCallback(object : IRequestCallback {
            override fun onSuccess(httpURLConnection: HttpURLConnection?, response: String) {
                val ret = SoupFactory.parseHtml(PageDetailSoup::class.java, response, url)
                val model = ret.get(PageDetailSoup::class.java.simpleName)
                if(model!=null){
                    val local = model as PageDetailModel
                    owner.runOnUiThread{
                        mRecyclerView.adapter = PageDetailAdapter(local)
                    }
                }
            }
        })
    }


}