package com.ecjtu.flesh.ui.adapter

import android.content.Context
import android.preference.PreferenceManager
import android.support.v4.view.ViewPager
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ecjtu.flesh.R
import com.ecjtu.flesh.model.models.V33Model
import com.ecjtu.netcore.model.MenuModel

/**
 * Created by Ethan_Xiang on 2018/2/22.
 */
class V33TabPagerAdapter(menu: List<MenuModel>, viewPager: ViewPager) : VideoTabPagerAdapter(menu, viewPager) {
    private val KEY_LAST_POSITION = "v33_last_position_"
    private val KEY_LAST_POSITION_OFFSET = "v33_last_position_offset_"

    private val mViewStub = HashMap<String, VH>()

    private var mMenuChildList: Map<String, List<V33Model>>? = null

    override fun instantiateItem(container: ViewGroup?, position: Int): Any {
        val item = LayoutInflater.from(container?.context).inflate(R.layout.layout_list_card_view, container, false)
        container?.addView(item)
        val title = getPageTitle(position) as String
        val vh = VH(item, menu[position], title)
        mMenuChildList?.get(title)?.let {
            vh.load(mMenuChildList!!.get(title)!!)
        }
        mViewStub.put(getPageTitle(position).toString(), vh)
        return item
    }

    private inner class VH(itemView: View, menu: MenuModel, key: String) : VideoTabPagerAdapter.VH(itemView, menu, key) {
        private var mPageModel: List<V33Model>? = null

        fun load(v33ModelList: List<V33Model>) {
            mPageModel = v33ModelList
            loadCache(itemView.context, key)
        }

        fun getPageModel(): List<V33Model>? {
            return mPageModel
        }

        private fun loadCache(context: Context, key: String) {
            if (mPageModel != null) {
                recyclerView?.adapter = VideoCardListAdapter(mPageModel!!, recyclerView!!)
                val lastPosition = PreferenceManager.getDefaultSharedPreferences(context).getInt(getLastPositionKey() + key, -1)
                if (lastPosition >= 0) {
                    val yOffset = PreferenceManager.getDefaultSharedPreferences(context).getInt(getLastPositionOffsetKey() + key, 0)
                    (recyclerView?.layoutManager as LinearLayoutManager).scrollToPositionWithOffset(lastPosition, yOffset)
                }
            }
        }

        override fun getSize(): Int {
            return mPageModel?.size ?: 0
        }
    }

    override fun getViewStub(position: Int): View? {
        return mViewStub.get(menu[position].title)?.recyclerView
    }

    override fun getListSize(position: Int): Int {
        return mViewStub.get(menu[position].title)?.getSize() ?: 0
    }

    fun setMenuChildList(mutableMap: MutableMap<String, List<V33Model>>) {
        mMenuChildList = mutableMap
    }

    override fun getLastPositionKey(): String {
        return KEY_LAST_POSITION
    }

    override fun getLastPositionOffsetKey(): String {
        return KEY_LAST_POSITION_OFFSET
    }

    override fun getViewStub(): HashMap<String, out VideoTabPagerAdapter.VH>? {
        return mViewStub
    }
}