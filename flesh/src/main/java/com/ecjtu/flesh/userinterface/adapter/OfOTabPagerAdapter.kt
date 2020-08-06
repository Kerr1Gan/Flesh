package com.ecjtu.flesh.userinterface.adapter

import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.ecjtu.flesh.model.models.VideoModel
import com.ecjtu.netcore.model.MenuModel

/**
 * Created by xiang on 2018/2/25.
 */
class OfOTabPagerAdapter(menu: List<MenuModel>, viewPager: androidx.viewpager.widget.ViewPager) : V33TabPagerAdapter(menu, viewPager) {
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