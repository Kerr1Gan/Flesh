package com.ecjtu.flesh.ui.adapter

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ecjtu.flesh.R
import com.ecjtu.flesh.cache.impl.PageListCacheHelper
import com.ecjtu.flesh.model.models.V33Model
import com.ecjtu.netcore.model.MenuModel
import kotlin.concurrent.thread

/**
 * Created by Ethan_Xiang on 2018/1/15.
 */
class VideoTabPagerAdapter(menu: List<MenuModel>) : TabPagerAdapter(menu) {

    companion object {
        private const val KEY_CARD_CACHE = "video_card_cache_"
        private const val KEY_LAST_POSITION = "video_last_position_"
        private const val KEY_LAST_POSITION_OFFSET = "video_last_position_offset_"
    }

    private val mViewStub = HashMap<String, VH>()

    private var mMenuChildList: Map<String, List<V33Model>>? = null

    override fun isViewFromObject(view: View?, `object`: Any?): Boolean = view == `object`

    override fun getCount(): Int {
        return menu.size
    }

    override fun instantiateItem(container: ViewGroup?, position: Int): Any {
        val item = LayoutInflater.from(container?.context).inflate(R.layout.layout_list_card_view, container, false)
        container?.addView(item)
        val title = getPageTitle(position) as String
        val vh = VH(item, menu[position], title)
        thread {
            //            val helper = PageListCacheHelper(container?.context?.filesDir?.absolutePath)
//            val pageModel: PageModel? = helper.get(KEY_CARD_CACHE + getPageTitle(position))
            vh.itemView.post {
                mMenuChildList?.get(title)?.let {
                    vh.load(mMenuChildList!!.get(title)!!)
                }
            }
        }
        mViewStub.put(getPageTitle(position).toString(), vh)
        return item
    }

    override fun destroyItem(container: ViewGroup?, position: Int, `object`: Any?) {
        container?.removeView(`object` as View)
        val vh: VH? = mViewStub.remove(getPageTitle(position))
        onStop(container?.context!!, getPageTitle(position).toString(), vh?.recyclerView, vh?.getPageModel())
        (vh?.recyclerView?.adapter as VideoCardListAdapter?)?.onRelease()
    }

    override fun getPageTitle(position: Int): CharSequence {
        return menu[position].title
    }

    fun onStop(context: Context, key: String, recyclerView: RecyclerView?, pageModel: List<V33Model>?) {
        thread {
            val editor: SharedPreferences.Editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
            val helper = PageListCacheHelper(context.filesDir.absolutePath)
            if (pageModel != null) {
                helper.put(KEY_CARD_CACHE + key, pageModel)
            }
            if (recyclerView != null) {
                editor.putInt(KEY_LAST_POSITION + key,
                        getScrollYPosition(recyclerView)).
                        putInt(KEY_LAST_POSITION_OFFSET + key, getScrollYOffset(recyclerView))
            }
            editor.apply()
        }
    }

    override fun onStop(context: Context) {
        val editor: SharedPreferences.Editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
        for (entry in mViewStub) {
            val recyclerView = entry.value.recyclerView

            val helper = PageListCacheHelper(context.filesDir.absolutePath)
            if (entry.value.getPageModel() != null) {
                val pageModel = entry.value.getPageModel()
                //todo 无法正确获取到当前位置对应的nextPageUrl
                helper.put(KEY_CARD_CACHE + entry.key, pageModel)
            }
            if (recyclerView != null) {
                editor.putInt(KEY_LAST_POSITION + entry.key,
                        getScrollYPosition(recyclerView)).
                        putInt(KEY_LAST_POSITION_OFFSET + entry.key, getScrollYOffset(recyclerView))
            }
        }
        editor.apply()
    }

    override fun onResume() {
        for (entry in mViewStub) {
            if (entry.value.recyclerView?.adapter is CardListAdapter) {
                (entry.value.recyclerView?.adapter as CardListAdapter).onResume()
            }
        }
    }

    private class VH(val itemView: View, private val menu: MenuModel, val key: String) {
        val recyclerView = itemView.findViewById(R.id.recycler_view) as RecyclerView?
        private var mPageModel: List<V33Model>? = null
        private val mRefreshLayout = if (itemView is SwipeRefreshLayout) itemView else null

        init {
            recyclerView?.layoutManager = LinearLayoutManager(recyclerView?.context, LinearLayoutManager.VERTICAL, false)
            initRefreshLayout()
        }

        fun load(v33ModelList: List<V33Model>) {
            mPageModel = v33ModelList
            loadCache(itemView.context, key)
            requestUrl()
        }

        fun getPageModel(): List<V33Model>? {
            return mPageModel
        }

        private fun loadCache(context: Context, key: String) {
            if (mPageModel != null) {
                recyclerView?.adapter = VideoCardListAdapter(mPageModel!!)
                val lastPosition = PreferenceManager.getDefaultSharedPreferences(context).getInt(KEY_LAST_POSITION + key, -1)
                if (lastPosition >= 0) {
                    val yOffset = PreferenceManager.getDefaultSharedPreferences(context).getInt(KEY_LAST_POSITION_OFFSET + key, 0)
                    (recyclerView?.layoutManager as LinearLayoutManager).scrollToPositionWithOffset(lastPosition, yOffset)
                }
            }
        }

        private fun initRefreshLayout() {
            if (mRefreshLayout != null) {
                mRefreshLayout.setColorSchemeColors(mRefreshLayout.context.resources.getColor(R.color.colorPrimary))
                mRefreshLayout.setOnRefreshListener {
                    requestUrl()
                }
            }
        }

        private fun requestUrl() {

        }
    }

    override fun getScrollYDistance(recyclerView: RecyclerView): Int {
        val layoutManager = recyclerView.layoutManager as LinearLayoutManager
        val position = layoutManager.findFirstVisibleItemPosition()
        val firstVisibleChildView = layoutManager.findViewByPosition(position)
        val itemHeight = firstVisibleChildView.height
        return position * itemHeight - (firstVisibleChildView?.top ?: 0)
    }

    override fun getScrollYPosition(recyclerView: RecyclerView): Int {
        val layoutManager = recyclerView.layoutManager as LinearLayoutManager
        return layoutManager.findFirstVisibleItemPosition()
    }

    override fun getScrollYOffset(recyclerView: RecyclerView): Int {
        val layoutManager = recyclerView.layoutManager as LinearLayoutManager
        val position = layoutManager.findFirstVisibleItemPosition()
        val firstVisibleChildView = layoutManager.findViewByPosition(position)
        return firstVisibleChildView?.top ?: 0
    }

    override fun findLastVisiblePosition(recyclerView: RecyclerView): Int {
        val layoutManager = recyclerView.layoutManager as LinearLayoutManager
        return layoutManager.findLastVisibleItemPosition()
    }

    override fun getItemPosition(`object`: Any?): Int {
        return POSITION_NONE
    }

    override fun getViewStub(position: Int): View? {
        return mViewStub.get(menu[position].title)?.recyclerView
    }

    override fun getListSize(position: Int): Int {
        return mViewStub.get(menu[position].title)?.getPageModel()?.size ?: 0
    }

    fun setMenuChildList(mutableMap: MutableMap<String, List<V33Model>>) {
        mMenuChildList = mutableMap
    }
}