package com.ecjtu.flesh.presenter

import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Build
import android.preference.PreferenceManager
import android.support.annotation.RequiresApi
import android.support.design.widget.AppBarLayout
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.format.Formatter
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
import com.ecjtu.flesh.model.models.V33Model
import com.ecjtu.flesh.ui.activity.MainActivity
import com.ecjtu.flesh.ui.adapter.TabPagerAdapter
import com.ecjtu.flesh.ui.fragment.*
import com.ecjtu.flesh.util.file.FileUtil
import com.ecjtu.netcore.model.MenuModel
import java.io.File
import kotlin.concurrent.thread
import kotlin.reflect.KClass


/**
 * Created by KerriGan on 2017/6/2.
 */
class MainActivityDelegate(owner: MainActivity) : Delegate<MainActivity>(owner), BaseTabPagerFragment.IDelegate {

    private val mFloatButton = owner.findViewById(R.id.float_button) as FloatingActionButton
    private var mViewPager = owner.findViewById(R.id.view_pager) as ViewPager
    private val mTabLayout = owner.findViewById(R.id.tab_layout) as TabLayout
    private val mAppbarLayout = owner.findViewById(R.id.app_bar) as AppBarLayout
    private var mAppbarExpand = true
    private var mCurrentPagerIndex = 0

    init {
        mViewPager.adapter = FragmentAdapter(owner.supportFragmentManager)
        initView()
        recoverTab(0, isAppbarLayoutExpand())
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
//                .addItem(BottomNavigationItem(R.drawable.ic_girl, "More"))
                .initialise()
        bottomNav.setTabSelectedListener(object : BottomNavigationBar.OnTabSelectedListener {
            override fun onTabUnselected(position: Int) {
                if (mViewPager.adapter is FragmentPagerAdapter) {
                    val fragment = (mViewPager.adapter as FragmentPagerAdapter).getItem(position)
                    if (fragment is BaseTabPagerFragment) {
                        fragment.onUnSelectTab()
                    }
                }
            }

            @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
            override fun onTabSelected(position: Int) {
                mCurrentPagerIndex = position
//                mViewPager.adapter?.apply {
//                    (this as TabPagerAdapter).onStop(owner, mTabLayout.selectedTabPosition, isAppbarLayoutExpand())
//                }
                when (position) {



                    0 -> {
                        mTabLayout.visibility = View.VISIBLE
                        mViewPager.setCurrentItem(0)
                    }

                    1 -> {
                        mTabLayout.visibility = View.GONE
                        mViewPager.setCurrentItem(1)
                    }
                }
                if (mViewPager.adapter is FragmentPagerAdapter) {
                    val fragment = (mViewPager.adapter as FragmentPagerAdapter).getItem(position)
                    if (fragment is BaseTabPagerFragment) {
                        fragment.onSelectTab()
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
//        for ((index, viewPager) in mViewPagerArray.withIndex()) {
//            viewPager?.let {
//                if (index == mCurrentPagerIndex) {
//                    Log.i("tttttttttt", "onStop curPage" + mTabLayout.selectedTabPosition)
//                    (viewPager.adapter as TabPagerAdapter?)?.onStop(owner, mTabLayout.selectedTabPosition, isAppbarLayoutExpand())
//                } else {
//                    Log.i("tttttttttt", "onStop " + mTabLayout.selectedTabPosition)
//                    (viewPager.adapter as TabPagerAdapter?)?.onStop(owner, -1, isAppbarLayoutExpand())
//                }
//            }
//        }
    }

    fun onResume() {
//        mViewPager.adapter?.let {
//            (mViewPager.adapter as TabPagerAdapter).onResume()
//        }
    }

    fun onDestroy() {
//        for ((index, viewPager) in mViewPagerArray.withIndex()) {
//            viewPager?.let {
//                (viewPager.adapter as TabPagerAdapter?)?.onDestroy()
//            }
//        }
    }

    override fun isAppbarLayoutExpand(): Boolean = mAppbarExpand

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
            val fragment = (mViewPager.adapter as FragmentAdapter).getItem(mViewPager.currentItem)
            val viewPager = (fragment as BaseTabPagerFragment).getViewPager()
            val tabPager = viewPager?.adapter
            recyclerView = (tabPager as TabPagerAdapter).getViewStub(position) as RecyclerView?
            size = tabPager.getListSize(position)
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

    fun recoverTab(tabItem: Int, isExpand: Boolean) {
        mViewPager.setCurrentItem(tabItem)
        mAppbarLayout.setExpanded(isExpand)
    }

    fun getLastTabItem(clazz: KClass<out TabPagerAdapter>): Int = PreferenceManager.getDefaultSharedPreferences(owner).
            getInt(TabPagerAdapter.KEY_LAST_TAB_ITEM + "_" + clazz.java.simpleName, 0)

    fun changeViewPager(index: Int) {
//        mTabLayout.removeAllTabs()
//        mTabLayout.setupWithViewPager((mViewPager.adapter as FragmentPagerAdapter).getItem(index))
    }

    override fun getTabLayout(): TabLayout {
        return mTabLayout
    }

    inner class FragmentAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
        val fragments = Array<Fragment?>(2, { int ->
            when (int) {
                0 -> {
                    MzituFragment().apply { setDelegate(this@MainActivityDelegate) }
                }
                1 -> {
                    VideoFragment().apply { setDelegate(this@MainActivityDelegate) }
                }
                else -> {
                    null
                }
            }
        })

        init {
        }

        override fun getItem(position: Int): Fragment {
            return fragments[position]!!
        }

        override fun getCount(): Int {
            return fragments.size
        }
    }
}