package com.ecjtu.flesh.ui.adapter

import android.support.v4.view.ViewPager
import android.support.v7.widget.RecyclerView
import com.ecjtu.flesh.model.models.VideoModel
import com.ecjtu.netcore.model.MenuModel

/**
 * Created by xiang on 2018/2/25.
 */
class OfOTabPagerAdapter(menu: List<MenuModel>, viewPager: ViewPager) : V33TabPagerAdapter(menu, viewPager) {
    private val KEY_LAST_POSITION = "ofo91_last_position_"
    private val KEY_LAST_POSITION_OFFSET = "ofo91_last_position_offset_"


    override fun getLastPositionKey(): String {
        return KEY_LAST_POSITION
    }

    override fun getLastPositionOffsetKey(): String {
        return KEY_LAST_POSITION_OFFSET
    }

    override fun getVideoCardListAdapter(pageModel: List<VideoModel>, recyclerView: RecyclerView, url: String?): VideoCardListAdapter {
        return OfOVideoCardListAdapter(pageModel,recyclerView,url)
    }
}