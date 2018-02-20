package com.ecjtu.flesh.ui.fragment

import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.TextView
import com.ecjtu.componentes.activity.BaseActionActivity
import com.ecjtu.flesh.R
import com.ecjtu.flesh.ui.adapter.TabPagerAdapter

/**
 * Created by Ethan_Xiang on 2018/2/8.
 */
open class VideoListFragment : BaseTabPagerFragment(), BaseTabPagerFragment.IDelegate {
    companion object {
        private const val TAG = "VideoListFragment"
    }

    private var mTabLayout: TabLayout? = null
    private var mToolbar: Toolbar? = null
    private var mFloatButton: FloatingActionButton? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.i(TAG, "onCreateView")
        setHasOptionsMenu(true)
        return inflater?.inflate(R.layout.fragment_video_list, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        setDelegate(this)
        mTabLayout = view?.findViewById(R.id.tab_layout) as TabLayout
        super.onViewCreated(view, savedInstanceState)
        Log.i(TAG, "onViewCreated")
        userVisibleHint = true
        mToolbar = view.findViewById(R.id.tool_bar) as Toolbar?
        mToolbar?.setTitle("爱恋")
        if (activity is AppCompatActivity) {
            val content = view.findViewById(R.id.content)
            (activity as AppCompatActivity).setSupportActionBar(mToolbar)
            (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
            content?.setPadding(content.paddingLeft, content.paddingTop + getStatusBarHeight(), content.paddingRight, content.paddingBottom)
        }
        mFloatButton = view.findViewById(R.id.float_button) as FloatingActionButton?
        mFloatButton?.setOnClickListener {
            doFloatButton(mTabLayout!!, getViewPager()!!, getViewPager()!!)
        }
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        Log.i(TAG, "setUserVisibleHint " + isVisibleToUser)
    }

    override fun onUserVisibleHintChanged(isVisibleToUser: Boolean) {
        super.onUserVisibleHintChanged(isVisibleToUser)
    }

    override fun onStop() {
        super.onStop()
        Log.i(TAG, "onStop tabIndex " + getTabLayout().selectedTabPosition)
    }

    override fun getLastTabPositionKey(): String {
        return TAG + "_" + "last_tab_position"
    }

    override fun getTabLayout(): TabLayout {
        return mTabLayout!!
    }

    fun getToolbar(): Toolbar {
        return mToolbar!!
    }

    override fun isAppbarLayoutExpand(): Boolean {
        return false
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == android.R.id.home) {
            activity.finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    fun getStatusBarHeight(): Int {
        val resources = getResources()
        val resourceId = resources.getIdentifier(BaseActionActivity.STATUS_BAR_HEIGHT, "dimen", "android")
        return resources.getDimensionPixelSize(resourceId)
    }

    protected fun doFloatButton(tabLayout: TabLayout, content: View, viewPager: ViewPager) {
        val position = tabLayout.selectedTabPosition
        var recyclerView: RecyclerView? = null
        var size = 0
        viewPager.adapter?.let {
            val tabPager = viewPager.adapter
            recyclerView = (tabPager as TabPagerAdapter).getViewStub(position) as RecyclerView?
            size = tabPager.getListSize(position)
        }
        val snake = Snackbar.make(content, "", Snackbar.LENGTH_SHORT)
        if (snake.view is LinearLayout) {
            val vg = snake.view as LinearLayout
            val layout = LayoutInflater.from(context).inflate(R.layout.layout_quick_jump, vg, false) as ViewGroup

            val local = layout.findViewById(R.id.seek_bar) as SeekBar
            val pos = layout.findViewById(R.id.position) as TextView

            val listener = { v: View ->
                if (position != tabLayout.selectedTabPosition) {
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
}
