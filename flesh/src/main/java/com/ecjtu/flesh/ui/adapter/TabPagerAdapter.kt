package com.ecjtu.flesh.ui.adapter

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.support.v4.view.PagerAdapter
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ecjtu.flesh.R
import com.ecjtu.flesh.cache.impl.MenuListCacheHelper
import com.ecjtu.flesh.cache.impl.PageListCacheHelper
import com.ecjtu.flesh.db.DatabaseManager
import com.ecjtu.flesh.db.table.impl.ClassPageTableImpl
import com.ecjtu.netcore.jsoup.SoupFactory
import com.ecjtu.netcore.jsoup.impl.PageSoup
import com.ecjtu.netcore.model.MenuModel
import com.ecjtu.netcore.model.PageModel
import com.ecjtu.netcore.network.AsyncNetwork
import com.ecjtu.netcore.network.IRequestCallbackV2
import java.lang.Exception
import java.net.HttpURLConnection
import kotlin.concurrent.thread

/**
 * Created by Ethan_Xiang on 2017/9/12.
 */
open class TabPagerAdapter(var menu: List<MenuModel>) : PagerAdapter() {

    companion object {
        const val KEY_LAST_TAB_ITEM = "key_last_tab_item"
        const val KEY_APPBAR_LAYOUT_COLLAPSED = "key_appbar_layout_collapse"
        const val CACHE_MENU_LIST = "menu_list_cache"
    }

    private val KEY_CARD_CACHE = "card_cache_"
    private val KEY_LAST_POSITION = "last_position_"
    private val KEY_LAST_POSITION_OFFSET = "last_position_offset_"

    private val mViewStub = HashMap<String, VH>()

    override fun isViewFromObject(view: View?, `object`: Any?): Boolean = view == `object`

    override fun getCount(): Int {
        return menu.size
    }

    override fun instantiateItem(container: ViewGroup?, position: Int): Any {
        Log.i("ttttttt", "TabPagerAdapter instantiateItem " + position + " container " + container?.childCount)
        val item = LayoutInflater.from(container?.context).inflate(R.layout.layout_list_card_view, container, false)
        container?.addView(item)
        val title = getPageTitle(position) as String
        val vh = VH(item, menu[position], title)
        thread {
            val helper = PageListCacheHelper(container?.context?.filesDir?.absolutePath)
            if (!title.contains("推荐")) {
                val pageModel: PageModel? = helper.get(KEY_CARD_CACHE + getPageTitle(position))
                vh.itemView.post {
                    vh.load(pageModel)
                }
            } else {
                vh.itemView.post {
                    vh.load(null)
                }
            }
        }
        mViewStub.put(getPageTitle(position).toString(), vh)
        return item
    }

    override fun destroyItem(container: ViewGroup?, position: Int, `object`: Any?) {
        Log.i("ttttttt", "TabPagerAdapter remove view " + position)
        container?.removeView(`object` as View)
        val vh: VH? = mViewStub.remove(getPageTitle(position))
        onDestroyItem(container?.context!!, getPageTitle(position).toString(), vh?.recyclerView, vh?.getPageModel())
        (vh?.recyclerView?.adapter as CardListAdapter?)?.onRelease()
        vh?.getRefreshLayout()?.setRefreshing(false)
        vh?.getRefreshLayout()?.visibility = View.INVISIBLE
    }

    override fun getPageTitle(position: Int): CharSequence {
        return menu[position].title
    }

    open fun onDestroyItem(context: Context, key: String, recyclerView: RecyclerView?, pageModel: PageModel?) {
        thread {
            val helper = PageListCacheHelper(context.filesDir.absolutePath)
            if (pageModel != null) {
                helper.put(KEY_CARD_CACHE + key, pageModel)
            }
        }
        val editor: SharedPreferences.Editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
        if (recyclerView != null) {
            editor.putInt(KEY_LAST_POSITION + key,
                    getScrollYPosition(recyclerView)).
                    putInt(KEY_LAST_POSITION_OFFSET + key, getScrollYOffset(recyclerView))
        }
        editor.apply()
    }

    open fun onStop(context: Context, tabIndex: Int, isExpand: Boolean) {
        val editor: SharedPreferences.Editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
        for (entry in mViewStub) {
            val recyclerView = entry.value.recyclerView

            val helper = PageListCacheHelper(context.filesDir.absolutePath)
            if (entry.value.getPageModel() != null) {
                val pageModel = entry.value.getPageModel()
                //todo 无法正确获取到当前位置对应的nextPageUrl
//                if (recyclerView != null) {
//                    val lastPosition = findLastVisiblePosition(recyclerView)
//                    val href = pageModel?.itemList?.get(lastPosition)?.href ?: ""
//                    val db = DatabaseManager.getInstance(context)?.getDatabase()
//                    if (db != null) {
//                        val impl = ClassPageListTableImpl()
//                        val ret = impl.findNextPageAndLastHref(db, href)
//                        if (ret != null && !TextUtils.isEmpty(ret[0])) {
//                            pageModel?.nextPage = ret[0]
//                            val list = ArrayList<PageModel.ItemModel>()
//                            for (item in pageModel?.itemList!!) {
//                                list.add(item)
//                                if (item.href == ret[1]) {
//                                    break
//                                }
//                            }
//                            pageModel.itemList = list
//                        }
//                        db.close()
//                    }
//                }
                helper.put(KEY_CARD_CACHE + entry.key, pageModel)
                if (recyclerView != null) {
                    editor.putInt(KEY_LAST_POSITION + entry.key,
                            getScrollYPosition(recyclerView)).
                            putInt(KEY_LAST_POSITION_OFFSET + entry.key, getScrollYOffset(recyclerView))
                }
                if (recyclerView?.adapter is CardListAdapter) {
                    (recyclerView.adapter as CardListAdapter).onStop()
                }
            }
        }
        val helper = MenuListCacheHelper(context.filesDir.absolutePath)
        helper.put(CACHE_MENU_LIST + "_" + TabPagerAdapter::class.java.toString(), menu)

        editor.putBoolean(KEY_APPBAR_LAYOUT_COLLAPSED, isExpand)
        if (tabIndex >= 0) {
            Log.i("tttttttttt", "tabPager " + tabIndex)
            editor.putInt(KEY_LAST_TAB_ITEM + "_" + TabPagerAdapter::class.java.simpleName, tabIndex)
        }
        editor.apply()
    }

    open fun onResume() {
        for (entry in mViewStub) {
            if (entry.value.recyclerView?.adapter is CardListAdapter) {
                (entry.value.recyclerView?.adapter as CardListAdapter).onResume()
            }
        }
    }

    open fun onDestroy() {
    }

    private inner class VH(val itemView: View, private val menu: MenuModel, val key: String) {
        val recyclerView = itemView.findViewById(R.id.recycler_view) as RecyclerView?
        private var mPageModel: PageModel? = null
        private val mRefreshLayout = if (itemView is SwipeRefreshLayout) itemView else null

        init {
            recyclerView?.layoutManager = LinearLayoutManager(recyclerView?.context, LinearLayoutManager.VERTICAL, false)
            initRefreshLayout()
        }

        fun load(pageModel: PageModel?) {
            mPageModel = pageModel
            loadCache(itemView.context, key)
            requestUrl()
        }

        fun getPageModel(): PageModel? {
            return mPageModel
        }

        private fun loadCache(context: Context, key: String) {
            if (mPageModel != null) {
                recyclerView?.adapter = CardListAdapter(mPageModel!!)
//                delegate.hideBg()
//                val fastScroller = itemView.findViewById(R.id.fast_scroll) as FastScroller
//                fastScroller.setRecyclerView(recyclerView)
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
            val request = AsyncNetwork()
            if (!TextUtils.isEmpty(menu.url)) {
                request.request(menu.url, null)
                mRefreshLayout?.setRefreshing(true)
            } else {
                mRefreshLayout?.setRefreshing(false)
            }
            request.setRequestCallback(object : IRequestCallbackV2 {
                override fun onSuccess(httpURLConnection: HttpURLConnection?, response: String) {
                    val values = SoupFactory.parseHtml(PageSoup::class.java, response)
                    if (values != null) {
                        val soups = values[PageSoup::class.java.simpleName] as PageModel

                        var needUpdate = false
                        if (mPageModel != null) {
                            val list = mPageModel!!.itemList
                            soups.itemList.reverse()
                            for (item in soups.itemList) {
                                val index = list.indexOf(item)
                                if (index < 0) {
                                    list.add(0, item)
                                    needUpdate = true
                                } else {
                                    list.set(index, item)
                                }
                            }
                        }
                        val finalNeedUpdate = needUpdate
                        recyclerView?.post {
                            if (mPageModel == null) {
                                recyclerView.adapter = CardListAdapter(soups)
//                                delegate.hideBg()
//                                val fastScroller = itemView.findViewById(R.id.fast_scroll) as FastScroller
//                                fastScroller.setRecyclerView(recyclerView)
                                mPageModel = soups
                            } else {
                                (recyclerView.adapter as CardListAdapter).pageModel = mPageModel!!
                                if (finalNeedUpdate) {
                                    recyclerView.adapter.notifyDataSetChanged()
                                }
                            }
                        }
                        val impl = ClassPageTableImpl()
                        val db = DatabaseManager.getInstance(mRefreshLayout?.context)?.getDatabase()
                        db?.let {
                            db.beginTransaction()
                            impl.addPage(db, if (mPageModel == null) soups else mPageModel!!)
                            db.setTransactionSuccessful()
                            db.endTransaction()
                        }
                        db?.close()
                    }
                    mRefreshLayout?.post {
                        mRefreshLayout.setRefreshing(false)
                    }
                }

                override fun onError(httpURLConnection: HttpURLConnection?, exception: Exception) {
                    mRefreshLayout?.post {
                        mRefreshLayout.setRefreshing(false)
                    }
                }
            })
        }

        fun getRefreshLayout(): SwipeRefreshLayout? {
            return mRefreshLayout
        }
    }

    open fun getScrollYDistance(recyclerView: RecyclerView): Int {
        val layoutManager = recyclerView.layoutManager as LinearLayoutManager
        val position = layoutManager.findFirstVisibleItemPosition()
        val firstVisibleChildView = layoutManager.findViewByPosition(position)
        val itemHeight = firstVisibleChildView.height
        return position * itemHeight - (firstVisibleChildView?.top ?: 0)
    }

    open fun getScrollYPosition(recyclerView: RecyclerView): Int {
        val layoutManager = recyclerView.layoutManager as LinearLayoutManager
        return layoutManager.findFirstVisibleItemPosition()
    }

    open fun getScrollYOffset(recyclerView: RecyclerView): Int {
        val layoutManager = recyclerView.layoutManager as LinearLayoutManager
        val position = layoutManager.findFirstVisibleItemPosition()
        val firstVisibleChildView = layoutManager.findViewByPosition(position)
        return firstVisibleChildView?.top ?: 0
    }

    open fun findLastVisiblePosition(recyclerView: RecyclerView): Int {
        val layoutManager = recyclerView.layoutManager as LinearLayoutManager
        return layoutManager.findLastVisibleItemPosition()
    }

    override fun getItemPosition(`object`: Any?): Int {
        return POSITION_NONE
    }

    open fun getViewStub(position: Int): View? {
        return mViewStub.get(menu[position].title)?.recyclerView
    }

    open fun getListSize(position: Int): Int {
        return mViewStub.get(menu[position].title)?.getPageModel()?.itemList?.size ?: 0
    }
}