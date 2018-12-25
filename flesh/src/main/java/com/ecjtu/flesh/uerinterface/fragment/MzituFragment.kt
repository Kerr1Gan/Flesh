package com.ecjtu.flesh.uerinterface.fragment

import android.os.Bundle
import android.support.v4.view.ViewPager
import android.util.Log
import android.view.View
import com.ecjtu.flesh.cache.impl.MenuListCacheHelper
import com.ecjtu.flesh.uerinterface.adapter.TabPagerAdapter
import com.ecjtu.netcore.Constants
import com.ecjtu.netcore.jsoup.SoupFactory
import com.ecjtu.netcore.jsoup.impl.MenuSoup
import com.ecjtu.netcore.model.MenuModel
import okhttp3.*
import java.io.IOException

/**
 * Created by Ethan_Xiang on 2018/2/1.
 */
class MzituFragment : BaseTabPagerFragment {
    companion object {
        private const val TAG = "MzituFragment"
    }

    constructor() : super()

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.i(TAG, "onViewCreated")
        getViewPager()?.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
                (getViewPager()?.adapter as TabPagerAdapter?)?.unSelect(position)
            }
        })
    }

    override fun onUserVisibleHintChanged(isVisibleToUser: Boolean) {
        super.onUserVisibleHintChanged(isVisibleToUser)
        if (isVisibleToUser) {
            val helper = MenuListCacheHelper(context.filesDir.absolutePath)
            val lastTabPosition = getLastTabPosition()
            var menuList: MutableList<MenuModel>? = null
            if (helper.get<Any>(TabPagerAdapter.CACHE_MENU_LIST + "_" + TabPagerAdapter::class.java) != null) {
                menuList = helper.get(TabPagerAdapter.CACHE_MENU_LIST + "_" + TabPagerAdapter::class.java)
            }
            if (menuList != null) {
                getViewPager()?.adapter = TabPagerAdapter(menuList)
                if (userVisibleHint) {
                    getViewPager()?.let {
                        getTabLayout()?.setupWithViewPager(getViewPager())
                        getViewPager()?.setCurrentItem(lastTabPosition)
                    }
                }
            }

            val client = OkHttpClient()
            val request = Request.Builder()
                    .url(Constants.HOST_MOBILE_URL)
                    .get()
                    .build()
            client.newCall(request)
                    .enqueue(object : Callback {
                        override fun onFailure(call: Call, e: IOException) {
                        }

                        override fun onResponse(call: Call, response: Response) {
                            val values = SoupFactory.parseHtml(MenuSoup::class.java, response.body()?.string())
                            if (values != null) {
                                activity.runOnUiThread {
                                    if (getViewPager()?.currentItem != 0) {
                                        return@runOnUiThread
                                    }
                                    var localList: List<MenuModel>? = null
                                    if (values[MenuSoup::class.java.simpleName] != null) {
                                        localList = values[MenuSoup::class.java.simpleName] as List<MenuModel>
                                        if (menuList == null && localList != null) {
                                            getViewPager()?.adapter = TabPagerAdapter(localList)
                                            if (userVisibleHint && getViewPager() != null) {
                                                getTabLayout()?.setupWithViewPager(getViewPager())
                                                getViewPager()?.setCurrentItem(lastTabPosition)
                                            }
                                        } else {
                                            var needUpdate = false
                                            for (obj in localList) {
                                                if (menuList?.indexOf(obj) ?: 0 < 0) {
                                                    menuList?.add(0, obj)
                                                    needUpdate = true
                                                }
                                            }
                                            if (needUpdate && userVisibleHint) {
                                                getViewPager()?.adapter?.notifyDataSetChanged()
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    })
        }
    }

    override fun onUnSelectTab() {
        (getViewPager()?.adapter as TabPagerAdapter?)?.unSelect()
        super.onUnSelectTab()
    }

    override fun getLastTabPositionKey(): String {
        return TAG + "_" + "last_tab_position"
    }

    override fun onStop() {
        super.onStop()
    }

    override fun saveLastTabPosition() {
        super.saveLastTabPosition()
    }
}