package com.ecjtu.heaven.presenter

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.ecjtu.heaven.R
import com.ecjtu.heaven.ui.activity.MainActivity
import com.ecjtu.heaven.ui.adapter.CardListAdapter
import com.ecjtu.netcore.Constants
import com.ecjtu.netcore.jsoup.PageSoup
import com.ecjtu.netcore.jsoup.SoupFactory
import com.ecjtu.netcore.model.PageModel
import com.ecjtu.sharebox.network.AsyncNetwork
import com.ecjtu.sharebox.network.IRequestCallback
import java.net.HttpURLConnection


/**
 * Created by KerriGan on 2017/6/2.
 */
class MainActivityDelegate(owner: MainActivity) : Delegate<MainActivity>(owner) {

    private val mRecyclerView = owner.findViewById(R.id.recycler_view) as RecyclerView

    init {
        mRecyclerView.layoutManager = LinearLayoutManager(owner, LinearLayoutManager.VERTICAL, false)

        val request = AsyncNetwork()
        request.request(Constants.HOST_URL)
        request.setRequestCallback(object : IRequestCallback {
            override fun onSuccess(httpURLConnection: HttpURLConnection?, response: String) {
                val values = SoupFactory.parseHtml(PageSoup::class.java, response)
                if (values != null) {
                    val soups = values[PageSoup::class.java.simpleName] as PageModel
                    owner.runOnUiThread {
                        mRecyclerView.adapter = CardListAdapter(soups)
                    }
                }
            }
        })
    }
}