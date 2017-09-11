package com.ecjtu.heaven.presenter

import android.preference.PreferenceManager
import android.support.design.widget.FloatingActionButton
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import com.ecjtu.heaven.R
import com.ecjtu.heaven.cache.PageListCacheHelper
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
    private var mPageModel: PageModel? = null
    private val mFloatButton = owner.findViewById(R.id.float_button) as FloatingActionButton

    init {
        mRecyclerView.layoutManager = LinearLayoutManager(owner, LinearLayoutManager.VERTICAL, false)
        val helper = PageListCacheHelper(owner.filesDir.absolutePath)
        mPageModel = helper.get("list_cache")

        val lastPosition = PreferenceManager.getDefaultSharedPreferences(owner).getInt("last_position", -1)

        if (mPageModel != null) {
            mRecyclerView.adapter = CardListAdapter(mPageModel!!)
            if (lastPosition >= 0) {
                val yOffset = PreferenceManager.getDefaultSharedPreferences(owner).getInt("last_position_offset", 0)
                (mRecyclerView.layoutManager as LinearLayoutManager).scrollToPositionWithOffset(lastPosition, yOffset)
            }
        }

        val request = AsyncNetwork()
        request.request(Constants.HOST_MOBILE_URL)
        request.setRequestCallback(object : IRequestCallback {
            override fun onSuccess(httpURLConnection: HttpURLConnection?, response: String) {
                val values = SoupFactory.parseHtml(PageSoup::class.java, response)
                if (values != null) {
                    val soups = values[PageSoup::class.java.simpleName] as PageModel
                    owner.runOnUiThread {
                        if (mPageModel == null) {
                            mRecyclerView.adapter = CardListAdapter(soups)
                            mPageModel = soups
                        } else {
                            val list = mPageModel!!.itemList
                            for (item in soups.itemList) {
                                if (list.indexOf(item) < 0) {
                                    list.add(0, item)
                                }
                            }
                            (mRecyclerView.adapter as CardListAdapter).pageModel = mPageModel!!
                            mRecyclerView.adapter.notifyDataSetChanged()
                        }
                    }
                }
            }
        })
        mFloatButton.setOnClickListener{
            (mRecyclerView.layoutManager as LinearLayoutManager).scrollToPosition(0)
        }
    }

    fun onStop() {
        mPageModel?.let {
            val helper = PageListCacheHelper(owner.filesDir.absolutePath)
            helper.put("list_cache", mPageModel)
        }
        PreferenceManager.getDefaultSharedPreferences(owner).edit().putInt("last_position", getScrollYPosition())
                .putInt("last_position_offset", getScrollYOffset()).apply()
    }

    fun getScollYDistance(): Int {
        val layoutManager = mRecyclerView.layoutManager as LinearLayoutManager
        val position = layoutManager.findFirstVisibleItemPosition()
        val firstVisibleChildView = layoutManager.findViewByPosition(position)
        val itemHeight = firstVisibleChildView.height
        return position * itemHeight - firstVisibleChildView.top
    }

    fun getScrollYPosition(): Int {
        val layoutManager = mRecyclerView.layoutManager as LinearLayoutManager
        return layoutManager.findFirstVisibleItemPosition()
    }

    fun getScrollYOffset(): Int {
        val layoutManager = mRecyclerView.layoutManager as LinearLayoutManager
        val position = layoutManager.findFirstVisibleItemPosition()
        val firstVisibleChildView = layoutManager.findViewByPosition(position)
        return firstVisibleChildView.top
    }
}