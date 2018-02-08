package com.ecjtu.flesh.presenter

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.os.Build
import android.preference.PreferenceManager
import android.support.annotation.RequiresApi
import android.support.design.widget.AppBarLayout
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.format.Formatter
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.TextView
import com.ashokvarma.bottomnavigation.BottomNavigationBar
import com.ashokvarma.bottomnavigation.BottomNavigationItem
import com.bumptech.glide.Glide
import com.ecjtu.componentes.activity.AppThemeActivity
import com.ecjtu.flesh.R
import com.ecjtu.flesh.cache.impl.MenuListCacheHelper
import com.ecjtu.flesh.cache.impl.V33CacheHelper
import com.ecjtu.flesh.model.models.V33Model
import com.ecjtu.flesh.ui.activity.MainActivity
import com.ecjtu.flesh.ui.adapter.TabPagerAdapter
import com.ecjtu.flesh.ui.adapter.VideoTabPagerAdapter
import com.ecjtu.flesh.ui.fragment.PageHistoryFragment
import com.ecjtu.flesh.ui.fragment.PageLikeFragment
import com.ecjtu.flesh.util.file.FileUtil
import com.ecjtu.netcore.Constants
import com.ecjtu.netcore.jsoup.SoupFactory
import com.ecjtu.netcore.jsoup.impl.MenuSoup
import com.ecjtu.netcore.model.MenuModel
import com.ecjtu.netcore.network.AsyncNetwork
import com.ecjtu.netcore.network.IRequestCallback
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.net.HttpURLConnection
import kotlin.concurrent.thread
import kotlin.reflect.KClass


/**
 * Created by KerriGan on 2017/6/2.
 */
class MainActivityDelegate(owner: MainActivity) : Delegate<MainActivity>(owner) {

    private val mFloatButton = owner.findViewById(R.id.float_button) as FloatingActionButton
    private var mViewPager = owner.findViewById(R.id.view_pager) as ViewPager
    private val mTabLayout = owner.findViewById(R.id.tab_layout) as TabLayout
    private val mAppbarLayout = owner.findViewById(R.id.app_bar) as AppBarLayout
    private var mAppbarExpand = true
    private val mViewPagerArray = Array<ViewPager?>(2, { index -> null })
    private var mCurrentPagerIndex = 0
    private var mLoadingDialog: AlertDialog? = null
    private var mV33Menu: List<MenuModel>? = null
    private var mV33Cache: Map<String, List<V33Model>>? = null

    init {
        val helper = MenuListCacheHelper(owner.filesDir.absolutePath)
        val lastTabItem = getLastTabItem(TabPagerAdapter::class)
        Log.i("tttttttttt", "init lastTabItem " + lastTabItem)
        var menuList: MutableList<MenuModel>? = null
        if (helper.get<Any>(TabPagerAdapter.CACHE_MENU_LIST + "_" + TabPagerAdapter::class.java) != null) {
            menuList = helper.get(TabPagerAdapter.CACHE_MENU_LIST + "_" + TabPagerAdapter::class.java)
        }
        if (menuList != null) {
            mViewPager.adapter = TabPagerAdapter(menuList)
            mTabLayout.setupWithViewPager(mViewPager)
            mViewPagerArray[0] = mViewPager
        }
        val request = AsyncNetwork()
        request.request(Constants.HOST_MOBILE_URL, null)
        request.setRequestCallback(object : IRequestCallback {
            override fun onSuccess(httpURLConnection: HttpURLConnection?, response: String) {
                val values = SoupFactory.parseHtml(MenuSoup::class.java, response)
                if (values != null) {
                    owner.runOnUiThread {
                        if (mCurrentPagerIndex != 0) {
                            return@runOnUiThread
                        }
                        var localList: List<MenuModel>? = null
                        if (values[MenuSoup::class.java.simpleName] != null) {
                            localList = values[MenuSoup::class.java.simpleName] as List<MenuModel>
                            if (menuList == null && localList != null) {
                                mViewPager.adapter = null
                                mViewPager.adapter = TabPagerAdapter(localList)
                                mTabLayout.setupWithViewPager(mViewPager)
                                mViewPager.setCurrentItem(lastTabItem)
                                mViewPagerArray[0] = mViewPager
                                Log.i("tttttttttt", "init adapter" + mTabLayout.selectedTabPosition)
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
                                Log.i("tttttttttt", "re adapter" + mTabLayout.selectedTabPosition)
                            }
                        }
                    }
                }
            }
        })

        initView()
        recoverTab(lastTabItem, isAppbarLayoutExpand())
    }

    private fun initView() {
        val cacheSize = PreferenceManager.getDefaultSharedPreferences(owner).getLong(com.ecjtu.flesh.Constants.PREF_CACHE_SIZE, com.ecjtu.flesh.Constants.DEFAULT_GLIDE_CACHE_SIZE)
        val cacheStr = Formatter.formatFileSize(owner, cacheSize)
        val glideSize = FileUtil.getGlideCacheSize(owner)
        val glideStr = Formatter.formatFileSize(owner, glideSize)
        val textView = findViewById(R.id.size) as TextView?
        val bottomNav = findViewById(R.id.bottom_navigation_bar) as BottomNavigationBar

        textView?.let {
            textView.setText(String.format("%s/%s", glideStr, cacheStr))
        }
        mFloatButton.setOnClickListener {
            doFloatButton(bottomNav)
        }

        findViewById(R.id.like)?.setOnClickListener {
            val intent = AppThemeActivity.newInstance(owner, PageLikeFragment::class.java)
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
            AlertDialog.Builder(owner).setTitle(R.string.cache_size).setMessage(owner.getString(R.string.cached_data_cleaned_or_not, size))
                    .setPositiveButton(R.string.ok, { dialog, which -> thread { Glide.get(owner).clearDiskCache() } })
                    .setNegativeButton(R.string.cancel, null)
                    .create().show()
        }

        findViewById(R.id.disclaimer)?.setOnClickListener {
            AlertDialog.Builder(owner).setTitle(R.string.statement).setMessage(R.string.statement_content)
                    .setPositiveButton(R.string.ok, null)
                    .create().show()
        }

        findViewById(R.id.history)?.setOnClickListener {
            val intent = AppThemeActivity.newInstance(owner, PageHistoryFragment::class.java)
            owner.startActivity(intent)
            val drawerLayout = findViewById(R.id.drawer_layout) as DrawerLayout
            drawerLayout.closeDrawer(Gravity.START)
        }

        mAppbarExpand = PreferenceManager.getDefaultSharedPreferences(owner).getBoolean(TabPagerAdapter.KEY_APPBAR_LAYOUT_COLLAPSED, false)
        mAppbarLayout.addOnOffsetChangedListener { appBarLayout, verticalOffset ->
            if (verticalOffset == 0) {
                mAppbarExpand = true
            } else if (verticalOffset == -(appBarLayout.height - mTabLayout.height)) {
                mAppbarExpand = false
            }
        }

        bottomNav
                .addItem(BottomNavigationItem(R.drawable.ic_image, "Image"))
                .addItem(BottomNavigationItem(R.drawable.ic_video, "Video"))
//                .addItem(BottomNavigationItem(R.drawable.ic_video, "More"))
                .initialise()
        bottomNav.setTabSelectedListener(object : BottomNavigationBar.OnTabSelectedListener {
            override fun onTabUnselected(position: Int) {
            }

            @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
            override fun onTabSelected(position: Int) {
                mCurrentPagerIndex = position
                mViewPager.adapter?.apply {
                    (this as TabPagerAdapter).onStop(owner, mTabLayout.selectedTabPosition, isAppbarLayoutExpand())
                }
                when (position) {
                    0 -> {
                        mViewPager = mViewPagerArray[0]!!
                        recoverTab(getLastTabItem(TabPagerAdapter::class), isAppbarLayoutExpand())
                        changeViewPager(0)
                    }

                    1 -> {
                        if (mViewPagerArray[1] != null) {
                            mViewPager = mViewPagerArray[1]!!
                            if (mV33Menu == null || mV33Menu?.size == 0) {
                                thread {
                                    val helper = V33CacheHelper(owner.filesDir.absolutePath)
                                    val helper2 = MenuListCacheHelper(owner.filesDir.absolutePath)
                                    mV33Menu = helper2.get("v33menu")
                                    mV33Cache = helper.get("v33cache")
                                    val localMenu = mV33Menu
                                    val localCache = mV33Cache
                                    if (localMenu != null && localCache != null) {
                                        mLoadingDialog?.cancel()
                                        mLoadingDialog = null
                                    }
                                    owner.runOnUiThread {
                                        if (mCurrentPagerIndex == 1 && localMenu != null && localCache != null) {
                                            if (mViewPager.adapter == null) {
                                                mViewPager.adapter = VideoTabPagerAdapter(localMenu, mViewPager)
                                                (mViewPager.adapter as VideoTabPagerAdapter).setMenuChildList(localCache as MutableMap<String, List<V33Model>>)
                                                (mViewPager.adapter as VideoTabPagerAdapter?)?.notifyDataSetChanged(true)
                                            } else {
                                                (mViewPager.adapter as VideoTabPagerAdapter).menu = localMenu
                                                (mViewPager.adapter as VideoTabPagerAdapter).setMenuChildList(localCache as MutableMap<String, List<V33Model>>)
                                                (mViewPager.adapter as VideoTabPagerAdapter?)?.notifyDataSetChanged(false)
                                            }
                                            recoverTab(getLastTabItem(VideoTabPagerAdapter::class), isAppbarLayoutExpand())
                                        }
                                    }
                                }
                            }
                            recoverTab(getLastTabItem(VideoTabPagerAdapter::class), isAppbarLayoutExpand())
                        } else {
                            mViewPagerArray[1] = ViewPager(owner)
                            mViewPager = mViewPagerArray[1]!!
                            val layoutParams = mViewPagerArray[1]?.layoutParams
                            layoutParams?.apply {
                                width = ViewGroup.LayoutParams.MATCH_PARENT
                                height = ViewGroup.LayoutParams.MATCH_PARENT
                            }
                            val req = AsyncNetwork().apply {
                                request(com.ecjtu.flesh.Constants.V33_URL, null)
                                setRequestCallback(object : IRequestCallback {
                                    override fun onSuccess(httpURLConnection: HttpURLConnection?, response: String) {
                                        val menuModel = arrayListOf<MenuModel>()
                                        val map = linkedMapOf<String, List<V33Model>>()
                                        try {
                                            val jObj = JSONArray(response)
                                            for (i in 0 until jObj.length()) {
                                                val jTitle = jObj[i] as JSONObject
                                                val title = jTitle.optString("title")
                                                val list = jTitle.optJSONArray("list")
                                                val modelList = arrayListOf<V33Model>()
                                                for (j in 0 until list.length()) {
                                                    val v33Model = V33Model()
                                                    val jItem = list[j] as JSONObject
                                                    v33Model.baseUrl = jItem.optString("baseUrl")
                                                    v33Model.imageUrl = jItem.optString("imageUrl")
                                                    v33Model.title = jItem.optString("title")
                                                    v33Model.videoUrl = jItem.optString("videoUrl")
                                                    modelList.add(v33Model)
                                                }
                                                map.put(title, modelList)
                                                val model = MenuModel(title, "")
                                                menuModel.add(model)
                                            }
                                        } catch (ex: Exception) {
                                            ex.printStackTrace()
                                        }

                                        mLoadingDialog?.cancel()
                                        mLoadingDialog = null
                                        owner.runOnUiThread {
                                            if (mCurrentPagerIndex == 1) {
                                                if (mViewPager.adapter == null) {
                                                    mViewPager.adapter = VideoTabPagerAdapter(menuModel, mViewPager)
                                                    (mViewPager.adapter as VideoTabPagerAdapter).setMenuChildList(map)
                                                    (mViewPager.adapter as VideoTabPagerAdapter?)?.notifyDataSetChanged(true)
                                                } else {
                                                    (mViewPager.adapter as VideoTabPagerAdapter).menu = menuModel
                                                    (mViewPager.adapter as VideoTabPagerAdapter).setMenuChildList(map)
                                                    (mViewPager.adapter as VideoTabPagerAdapter?)?.notifyDataSetChanged(false)
                                                }
                                                mV33Menu = menuModel
                                                mV33Cache = map
                                                recoverTab(getLastTabItem(VideoTabPagerAdapter::class), isAppbarLayoutExpand())
                                            }
                                        }
                                    }
                                })
                            }
                            if (mLoadingDialog == null) {
                                mLoadingDialog = AlertDialog.Builder(owner).setTitle("加载中").setMessage("需要一小会时间")
                                        .setNegativeButton("取消", { dialog, which ->
                                            thread {
                                                req.cancel()
                                            }
                                        })
                                        .setCancelable(false)
                                        .setOnCancelListener {
                                            mLoadingDialog = null
                                        }.create()
                                mLoadingDialog?.show()
                            }
                            thread {
                                val helper = V33CacheHelper(owner.filesDir.absolutePath)
                                val helper2 = MenuListCacheHelper(owner.filesDir.absolutePath)
                                mV33Menu = helper2.get("v33menu")
                                mV33Cache = helper.get("v33cache")
                                val localMenu = mV33Menu
                                val localCache = mV33Cache
                                if (localMenu != null && localCache != null) {
                                    mLoadingDialog?.cancel()
                                    mLoadingDialog = null
                                }
                                owner.runOnUiThread {
                                    if (mCurrentPagerIndex == 1 && localMenu != null && localCache != null) {
                                        if (mViewPager.adapter == null) {
                                            mViewPager.adapter = VideoTabPagerAdapter(localMenu, mViewPager)
                                            (mViewPager.adapter as VideoTabPagerAdapter).setMenuChildList(localCache as MutableMap<String, List<V33Model>>)
                                            (mViewPager.adapter as VideoTabPagerAdapter?)?.notifyDataSetChanged(true)
                                        } else {
                                            (mViewPager.adapter as VideoTabPagerAdapter).menu = localMenu
                                            (mViewPager.adapter as VideoTabPagerAdapter).setMenuChildList(localCache as MutableMap<String, List<V33Model>>)
                                            (mViewPager.adapter as VideoTabPagerAdapter?)?.notifyDataSetChanged(false)
                                        }
                                        recoverTab(getLastTabItem(VideoTabPagerAdapter::class), isAppbarLayoutExpand())
                                    }
                                }
                            }
                        }
                        changeViewPager(1)
                    }
                }
                //store view states
                //mViewPager.adapter?.notifyDataSetChanged()
            }

            override fun onTabReselected(position: Int) {
            }

        })
    }

    fun onStop() {
        for ((index, viewPager) in mViewPagerArray.withIndex()) {
            viewPager?.let {
                if (index == mCurrentPagerIndex) {
                    Log.i("tttttttttt", "onStop curPage" + mTabLayout.selectedTabPosition)
                    (viewPager.adapter as TabPagerAdapter?)?.onStop(owner, mTabLayout.selectedTabPosition, isAppbarLayoutExpand())
                } else {
                    Log.i("tttttttttt", "onStop " + mTabLayout.selectedTabPosition)
                    (viewPager.adapter as TabPagerAdapter?)?.onStop(owner, -1, isAppbarLayoutExpand())
                }
            }
        }
    }

    fun onResume() {
        mViewPager.adapter?.let {
            (mViewPager.adapter as TabPagerAdapter).onResume()
        }
    }

    fun onDestroy() {
        for ((index, viewPager) in mViewPagerArray.withIndex()) {
            viewPager?.let {
                (viewPager.adapter as TabPagerAdapter?)?.onDestroy()
            }
        }
    }

    fun isAppbarLayoutExpand(): Boolean = mAppbarExpand

    fun convertView2Bitmap(view: View, width: Int, height: Int): Bitmap {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        view.draw(Canvas(bitmap))
        return bitmap
    }

    private fun doFloatButton(bottomNavigationBar: BottomNavigationBar) {
        bottomNavigationBar.hide()
        val position = mTabLayout.selectedTabPosition
        var recyclerView: RecyclerView? = null
        var size = 0
        mViewPager.adapter?.let {
            recyclerView = (mViewPager.adapter as TabPagerAdapter).getViewStub(position) as RecyclerView?
            size = (mViewPager.adapter as TabPagerAdapter).getListSize(position)
        }
        val snake = Snackbar.make(findViewById(R.id.content)!!, "", Snackbar.LENGTH_SHORT)
        if (snake.view is LinearLayout) {
            val vg = snake.view as LinearLayout
            val layout = LayoutInflater.from(owner).inflate(R.layout.layout_quick_jump, vg, false) as ViewGroup

            val local = layout.findViewById(R.id.seek_bar) as SeekBar
            val pos = layout.findViewById(R.id.position) as TextView

            val listener = { v: View ->
                if (position != mTabLayout.selectedTabPosition) {
                    snake.dismiss()
                } else {
                    when (v.id) {
                        R.id.top -> {
                            recyclerView?.let {
                                (recyclerView?.layoutManager as LinearLayoutManager).scrollToPosition(0)
                            }
                        }

                        R.id.mid -> {
                            recyclerView?.let {
                                var jumpPos = Integer.valueOf(pos.text.toString()) - 2
                                if (jumpPos < 0) jumpPos = 0
                                (recyclerView?.layoutManager as LinearLayoutManager).scrollToPosition(jumpPos)
                            }
                        }

                        R.id.bottom -> {
                            recyclerView?.let {
                                (recyclerView?.layoutManager as LinearLayoutManager).scrollToPosition(size - 2)
                            }
                        }
                    }
                    snake.dismiss()
                }
                Unit
            }
            layout.findViewById(R.id.top).setOnClickListener(listener)
            layout.findViewById(R.id.mid).setOnClickListener(listener)
            layout.findViewById(R.id.bottom).setOnClickListener(listener)

            local.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    pos.setText(progress.toString())
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {
                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                }
            })
            local.max = size
            if (recyclerView != null) {
                val curPos = (recyclerView?.layoutManager as LinearLayoutManager).findLastVisibleItemPosition()
                local.progress = curPos
            }
            layout.findViewById(R.id.mid).setOnClickListener(listener)
            vg.addView(layout)
        }
        snake.show()
    }

    private fun recoverTab(tabItem: Int, isExpand: Boolean) {
        mViewPager.setCurrentItem(tabItem)
        mAppbarLayout.setExpanded(isExpand)
    }

    private fun getLastTabItem(clazz: KClass<out TabPagerAdapter>): Int = PreferenceManager.getDefaultSharedPreferences(owner).
            getInt(TabPagerAdapter.KEY_LAST_TAB_ITEM + "_" + clazz.java.simpleName, 0)

    private fun changeViewPager(index: Int) {
        val container = owner.findViewById(R.id.view_pager_container) as ViewGroup
        container.removeAllViews()
        container.addView(mViewPagerArray[index])
        mTabLayout.removeAllTabs()
        mTabLayout.setupWithViewPager(mViewPagerArray[index])
    }
}