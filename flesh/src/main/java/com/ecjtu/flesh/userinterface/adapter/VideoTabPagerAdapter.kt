package com.ecjtu.flesh.userinterface.adapter

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.support.v4.view.ViewPager
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.ecjtu.flesh.R
import com.ecjtu.netcore.model.MenuModel

/**
 * Created by Ethan_Xiang on 2018/1/15.
 */
open class VideoTabPagerAdapter(menu: List<MenuModel>, private val viewPager: ViewPager) : TabPagerAdapter(menu), ViewPager.OnPageChangeListener {

    private val KEY_LAST_POSITION = "video_last_position_"
    private val KEY_LAST_POSITION_OFFSET = "video_last_position_offset_"
    private var mLastScrolledPosition = 0

    init {
        viewPager.addOnPageChangeListener(this)
    }

    override fun onPageScrollStateChanged(state: Int) {
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
    }

    override fun onPageSelected(position: Int) {
        if (position != mLastScrolledPosition) {
            var recyclerView = getViewStub(position) as RecyclerView?
            recyclerView?.let {
                if (recyclerView?.adapter is IChangeTab) {
                    (recyclerView?.adapter as IChangeTab).onSelectTab()
                }
            }
            recyclerView = getViewStub(mLastScrolledPosition) as RecyclerView?
            recyclerView?.let {
                if (recyclerView?.adapter is IChangeTab) {
                    (recyclerView?.adapter as IChangeTab).onUnSelectTab()
                }
            }
        }
        mLastScrolledPosition = position
    }

    override fun isViewFromObject(view: View?, `object`: Any?): Boolean = view == `object`

    override fun getCount(): Int {
        return menu.size
    }

//    override fun instantiateItem(container: ViewGroup?, position: Int): Any {
//        super.instantiateItem(container, position)
//        Log.i("ttttttt", "VideoTabPagerAdapter instantiateItem " + position + " container " + container?.childCount)
//        val item = LayoutInflater.from(container?.context).inflate(R.layout.layout_list_card_view, container, false)
//        container?.addView(item)
//        val title = getPageTitle(position) as String
//        val vh = VH(item, menu[position], title)
//        mMenuChildList?.get(title)?.let {
//            vh.load(mMenuChildList!!.get(title)!!)
//        }
//        mViewStub.put(getPageTitle(position).toString(), vh)
//        return item
//    }

//    override fun destroyItem(container: ViewGroup?, position: Int, `object`: Any?) {
//        Log.i("ttttttt", "VideoTabPagerAdapter remove view " + position)
//        container?.removeView(`object` as View)
//        val vh: VH? = mViewStub.remove(getPageTitle(position))
//        onDestroyItem(container?.context!!, getPageTitle(position).toString(), vh?.recyclerView, vh?.getPageModel())
//        (vh?.recyclerView?.adapter as VideoCardListAdapter?)?.onRelease()
//    }

    override fun getPageTitle(position: Int): CharSequence {
        return menu[position].title
    }

//    open fun onDestroyItem(context: Context, key: String, recyclerView: RecyclerView?, pageModel: List<VideoModel>?) {
//        val editor: SharedPreferences.Editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
//        if (recyclerView != null) {
//            editor.putInt(getLastPositionKey() + key, getScrollYPosition(recyclerView)).
//                    putInt(getLastPositionOffsetKey() + key, getScrollYOffset(recyclerView))
//        }
//        editor.apply()
//    }

    override fun onStop(context: Context, tabIndex: Int, isExpand: Boolean) {
        super.onStop(context, tabIndex, isExpand)
        val editor: SharedPreferences.Editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
        val stub = getViewStub()
        if (stub != null) {
            for (entry in stub) {
                val recyclerView = entry.value.recyclerView
                if (recyclerView != null) {
                    if (getScrollYPosition(recyclerView) >= 0) {
                        editor.putInt(getLastPositionKey() + entry.key,
                                getScrollYPosition(recyclerView)).
                                putInt(getLastPositionOffsetKey() + entry.key, getScrollYOffset(recyclerView))
                    }
                    if (recyclerView.adapter is VideoCardListAdapter) {
                        (recyclerView.adapter as VideoCardListAdapter).onStop()
                    }
                }
            }
        }
        if (tabIndex >= 0) {
            editor.putInt(KEY_LAST_TAB_ITEM + "_" + this::class.java.simpleName, tabIndex)
        }
        editor.putBoolean(KEY_APPBAR_LAYOUT_COLLAPSED + "_" + this::class.java.simpleName, isExpand)
        editor.apply()
    }

    override fun destroyItem(container: ViewGroup?, position: Int, `object`: Any?) {
        container?.removeView(`object` as View)
        val vh: VH? = getViewStub()?.remove(getPageTitle(position))
        onDestroyItem(container?.context!!, getPageTitle(position).toString(), vh?.recyclerView)
        (vh?.recyclerView?.adapter as VideoCardListAdapter?)?.onRelease()
    }

    open fun onDestroyItem(context: Context, key: String, recyclerView: RecyclerView?) {
        val editor: SharedPreferences.Editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
        if (recyclerView != null && getScrollYPosition(recyclerView) >= 0) {
            editor.putInt(getLastPositionKey() + key, getScrollYPosition(recyclerView)).
                    putInt(getLastPositionOffsetKey() + key, getScrollYOffset(recyclerView))
        }
        editor.apply()
    }

    override fun onResume() {
        if (getViewStub() != null) {
            for (entry in getViewStub()!!) {
                if (entry.value.recyclerView?.adapter is VideoCardListAdapter) {
                    (entry.value.recyclerView?.adapter as VideoCardListAdapter).onResume()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (getViewStub() != null) {
            for (entry in getViewStub()!!) {
                if (entry.value.recyclerView?.adapter is VideoCardListAdapter) {
                    (entry.value.recyclerView?.adapter as VideoCardListAdapter).onDestroy()
                }
            }
        }
        viewPager.removeOnPageChangeListener(this)
    }

    open inner class VH(val itemView: View, private val menu: MenuModel, val key: String) {
        val recyclerView = itemView.findViewById<View>(R.id.recycler_view) as RecyclerView?
        private val mRefreshLayout = if (itemView is SwipeRefreshLayout) itemView else null

        init {
            recyclerView?.layoutManager = LinearLayoutManager(recyclerView?.context, LinearLayoutManager.VERTICAL, false)
            initRefreshLayout()
        }

//        fun load(v33ModelList: List<VideoModel>) {
//            mPageModel = v33ModelList
//            loadCache(itemView.context, key)
//        }
//
//        fun getPageModel(): List<VideoModel>? {
//            return mPageModel
//        }
//
//        private fun loadCache(context: Context, key: String) {
//            if (mPageModel != null) {
//                recyclerView?.adapter = VideoCardListAdapter(mPageModel!!, recyclerView!!)
//                val lastPosition = PreferenceManager.getDefaultSharedPreferences(context).getInt(getLastPositionKey() + key, -1)
//                if (lastPosition >= 0) {
//                    val yOffset = PreferenceManager.getDefaultSharedPreferences(context).getInt(getLastPositionOffsetKey() + key, 0)
//                    (recyclerView?.layoutManager as LinearLayoutManager).scrollToPositionWithOffset(lastPosition, yOffset)
//                }
//            }
//        }

        private fun initRefreshLayout() {
            if (mRefreshLayout != null) {
                mRefreshLayout.isEnabled = false
            }
        }

        open fun getSize(): Int = 0

        open fun getMenu():MenuModel = menu
    }

    override fun getViewStub(position: Int): View? {
        return getViewStub()?.get(menu[position].title)?.recyclerView
    }

    override fun getListSize(position: Int): Int {
        return getViewStub()?.get(menu[position].title)?.getSize() ?: 0
    }

    open fun getLastPositionKey(): String {
        return KEY_LAST_POSITION
    }

    open fun getLastPositionOffsetKey(): String {
        return KEY_LAST_POSITION_OFFSET
    }

    open fun getViewStub(): HashMap<String, out VH>? {
        return null
    }
}