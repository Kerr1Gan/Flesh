package com.ecjtu.heaven.presenter

import android.app.Activity
import com.ecjtu.heaven.ui.adapter.CardListAdapter
import com.ecjtu.heaven.ui.adapter.HistoryCardListAdapter
import com.ecjtu.netcore.model.PageModel

/**
 * Created by Ethan_Xiang on 2017/9/18.
 */
class PageHistoryFragmentDelegate(owner: Activity, itemList: List<PageModel.ItemModel>) : BasePageActivityDelegate(owner, itemList) {
    override fun getCardListAdapter(pageModel: PageModel): CardListAdapter {
        return HistoryCardListAdapter(pageModel)
    }
}