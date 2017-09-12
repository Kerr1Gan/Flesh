package com.ecjtu.heaven.presenter

import android.support.design.widget.FloatingActionButton
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import com.ecjtu.heaven.R
import com.ecjtu.heaven.cache.MenuListCacheHelper
import com.ecjtu.heaven.ui.activity.MainActivity
import com.ecjtu.heaven.ui.adapter.TabPagerAdapter
import com.ecjtu.netcore.Constants
import com.ecjtu.netcore.jsoup.MenuSoup
import com.ecjtu.netcore.jsoup.SoupFactory
import com.ecjtu.netcore.model.MenuModel
import com.ecjtu.netcore.network.AsyncNetwork
import com.ecjtu.netcore.network.IRequestCallback
import java.net.HttpURLConnection


/**
 * Created by KerriGan on 2017/6/2.
 */
class MainActivityDelegate(owner: MainActivity) : Delegate<MainActivity>(owner) {

    private val mFloatButton = owner.findViewById(R.id.float_button) as FloatingActionButton
    private val mViewPager = owner.findViewById(R.id.view_pager) as ViewPager
    private val mTabLayout = owner.findViewById(R.id.tab_layout) as TabLayout

    init {
        val helper = MenuListCacheHelper(owner.filesDir.absolutePath)

        var menuList: MutableList<MenuModel>? = null
        if (helper.get<Any>("menu_list_cache") != null) {
            menuList = helper.get("menu_list_cache")
        }
        if (menuList != null) {
            mViewPager.adapter = TabPagerAdapter(menuList)
            mTabLayout.setupWithViewPager(mViewPager)
        }
        val request = AsyncNetwork()
        request.request(Constants.HOST_MOBILE_URL, null)
        request.setRequestCallback(object : IRequestCallback {
            override fun onSuccess(httpURLConnection: HttpURLConnection?, response: String) {
                val values = SoupFactory.parseHtml(MenuSoup::class.java, response)
                if (values != null) {
                    owner.runOnUiThread {
                        var localList: List<MenuModel>? = null
                        if (values[MenuSoup::class.java.simpleName] != null) {
                            localList = values[MenuSoup::class.java.simpleName] as List<MenuModel>
                            if (menuList == null && localList != null) {
                                mViewPager.adapter = TabPagerAdapter(localList)
                                mTabLayout.setupWithViewPager(mViewPager)
                            } else {
                                var needUpdate = false
                                for (obj in localList) {
                                    if (menuList?.indexOf(obj) ?: 0 < 0) {
                                        menuList?.add(0, obj)
                                        needUpdate = true
                                    }
                                }
                                if (needUpdate) {
                                    mViewPager.adapter.notifyDataSetChanged()
                                }
                            }
                        }
                    }
                }
            }
        })
        mFloatButton.setOnClickListener {
            //            (mRecyclerView.layoutManager as LinearLayoutManager).scrollToPosition(0)
        }
    }

    fun onStop() {
        mViewPager.adapter?.let {
            (mViewPager.adapter as TabPagerAdapter).onStop(owner)
        }
        val helper = MenuListCacheHelper(owner.filesDir.absolutePath)
        helper.put("menu_list_cache",(mViewPager.adapter as TabPagerAdapter).menu)
    }

}