package com.ecjtu.flesh.mvp.presenter

import android.app.Activity
import android.content.Context
import android.support.design.widget.TabLayout
import com.ashokvarma.bottomnavigation.BottomNavigationBar
import com.ecjtu.flesh.mvp.IPresenter
import com.ecjtu.flesh.mvp.IView

class MainContract {
    interface Presenter : IPresenter<View> {
        fun onStop()
        fun onResume()
        fun onDestroy()
        fun checkZero(activity: Activity, view: MainContract.View)
        fun loadServerUrl()
        fun readPaymentId(deviceId: String)
        fun query(query: String)
    }

    interface View : IView<Presenter> {
        fun initialize()
        fun initView()
        fun recoverTab(tabItem: Int, isExpand: Boolean)
        fun isAppbarLayoutExpand(): Boolean
        fun doFloatButton(bottomNavigationBar: BottomNavigationBar)
        fun getTabLayout(): TabLayout
        fun loadAd()
        fun getContext(): Context
    }
}