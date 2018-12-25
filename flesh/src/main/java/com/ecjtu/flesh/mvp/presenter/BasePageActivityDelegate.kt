package com.ecjtu.flesh.mvp.presenter

import android.app.Activity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import com.ecjtu.flesh.R
import com.ecjtu.flesh.userinterface.adapter.CardListAdapter
import com.ecjtu.netcore.model.PageModel

/**
 * Created by Ethan_Xiang on 2017/9/18.
 */
abstract class BasePageActivityDelegate(owner: Activity, protected val itemList: List<PageModel.ItemModel>) : Delegate<Activity>(owner) {

    private val mRecyclerView = owner.findViewById<View>(R.id.recycler_view) as RecyclerView
    private var mPageModel: PageModel? = null

    init {
        mPageModel = PageModel()
        mPageModel?.nextPage = null
        mPageModel?.itemList = itemList.reversed()

        mRecyclerView.layoutManager = LinearLayoutManager(owner, LinearLayoutManager.VERTICAL, false)
        if (mPageModel != null) {
            mRecyclerView.adapter = getCardListAdapter(mPageModel!!)
        }
    }

    protected open fun getCardListAdapter(pageModel: PageModel): CardListAdapter {
        return CardListAdapter(pageModel)
    }

    open fun onRelease() {
        (mRecyclerView.adapter as CardListAdapter?)?.onRelease()
    }

    open fun onResume() {
        (mRecyclerView.adapter as CardListAdapter?)?.onResume()
    }

    protected fun getRecyclerView(): RecyclerView {
        return mRecyclerView
    }
}