package com.ecjtu.heaven.presenter

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.ecjtu.heaven.R
import com.ecjtu.heaven.ui.activity.PageLikeActivity
import com.ecjtu.heaven.ui.adapter.CardListAdapter
import com.ecjtu.netcore.model.PageModel

/**
 * Created by Ethan_Xiang on 2017/9/13.
 */
class PageLikeActivityDelegate(owner: PageLikeActivity, private val itemList: List<PageModel.ItemModel>) : Delegate<PageLikeActivity>(owner) {

    private val mRecyclerView = owner.findViewById(R.id.recycler_view) as RecyclerView
    private var mPageModel: PageModel? = null

    init {
        mPageModel = PageModel()
        mPageModel?.nextPage = null
        mPageModel?.itemList = itemList.reversed()

        mRecyclerView.layoutManager = LinearLayoutManager(owner, LinearLayoutManager.VERTICAL, false)
        if (mPageModel != null) {
            mRecyclerView.adapter = CardListAdapter(mPageModel!!)
        }
    }

    fun onRelease() {
        (mRecyclerView.adapter as CardListAdapter?)?.onRelease()
    }

    fun onResume() {
        (mRecyclerView.adapter as CardListAdapter?)?.onResume()
    }
}