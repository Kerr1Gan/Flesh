package com.ecjtu.heaven.presenter

import android.content.DialogInterface
import android.content.Intent
import android.preference.PreferenceManager
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.format.Formatter
import android.view.Gravity
import com.ecjtu.heaven.R
import com.ecjtu.heaven.cache.MenuListCacheHelper
import com.ecjtu.heaven.ui.activity.MainActivity
import com.ecjtu.heaven.ui.activity.PageLikeActivity
import com.ecjtu.heaven.ui.adapter.TabPagerAdapter
import com.ecjtu.heaven.util.file.FileUtil
import com.ecjtu.netcore.Constants
import com.ecjtu.netcore.jsoup.MenuSoup
import com.ecjtu.netcore.jsoup.SoupFactory
import com.ecjtu.netcore.model.MenuModel
import com.ecjtu.netcore.network.AsyncNetwork
import com.ecjtu.netcore.network.IRequestCallback
import java.io.File
import java.net.HttpURLConnection


/**
 * Created by KerriGan on 2017/6/2.
 */
class MainActivityDelegate(owner: MainActivity) : Delegate<MainActivity>(owner) {

    companion object {
        private const val KEY_LAST_TAB_ITEM = "key_last_tab_item"
    }

    private val mFloatButton = owner.findViewById(R.id.float_button) as FloatingActionButton
    private val mViewPager = owner.findViewById(R.id.view_pager) as ViewPager
    private val mTabLayout = owner.findViewById(R.id.tab_layout) as TabLayout

    init {
        val helper = MenuListCacheHelper(owner.filesDir.absolutePath)
        val lastTabItem = PreferenceManager.getDefaultSharedPreferences(owner).getInt(KEY_LAST_TAB_ITEM, 0)
        var menuList: MutableList<MenuModel>? = null
        if (helper.get<Any>("menu_list_cache") != null) {
            menuList = helper.get("menu_list_cache")
        }
        if (menuList != null) {
            mViewPager.adapter = TabPagerAdapter(menuList)
            mTabLayout.setupWithViewPager(mViewPager)
            mViewPager.setCurrentItem(lastTabItem)
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
                                mViewPager.setCurrentItem(lastTabItem)
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
            val position = mTabLayout.selectedTabPosition
            val recyclerView = (mViewPager.adapter as TabPagerAdapter).getViewStub(position) as RecyclerView?
            recyclerView?.let {
                (recyclerView.layoutManager as LinearLayoutManager).scrollToPosition(0)
            }
        }

        findViewById(R.id.like)?.setOnClickListener {
            val intent = Intent(owner, PageLikeActivity::class.java)
            owner.startActivity(intent)
            val drawerLayout = findViewById(R.id.drawer_layout) as DrawerLayout
            drawerLayout.closeDrawer(Gravity.START)
        }

        findViewById(R.id.cache)?.setOnClickListener {
            val cacheFile = File(owner.cacheDir.absolutePath + "/image_manager_disk_cache")
            val list = FileUtil.getFilesByFolder(cacheFile)
            var ret = 0L
            for (child in list) {
                ret += child.length()
            }
            val size = Formatter.formatFileSize(owner, ret)
            AlertDialog.Builder(owner).setTitle("缓存大小").setMessage("已缓存${size}数据,是否清理？")
                    .setPositiveButton("确定", { dialog, which -> for (child in list) child.delete() })
                    .setNegativeButton("取消", null)
                    .create().show()
        }

        findViewById(R.id.disclaimer)?.setOnClickListener {
            AlertDialog.Builder(owner).setTitle("声明").setMessage("所有资源均来自www.mzitu.com，如有侵权请联系mnsync@outlook.com，将会尽快删除。")
                    .setPositiveButton("确定",null)
                    .create().show()
        }
    }

    fun onStop() {
        mViewPager.adapter?.let {
            (mViewPager.adapter as TabPagerAdapter).onStop(owner)
        }
        val helper = MenuListCacheHelper(owner.filesDir.absolutePath)
        helper.put("menu_list_cache", (mViewPager.adapter as TabPagerAdapter).menu)

        PreferenceManager.getDefaultSharedPreferences(owner).edit().
                putInt(KEY_LAST_TAB_ITEM, mTabLayout.selectedTabPosition).
                apply()
    }

    fun onResume() {
        mViewPager.adapter?.let {
            (mViewPager.adapter as TabPagerAdapter).onResume()
        }
    }


}