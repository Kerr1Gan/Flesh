package com.ecjtu.flesh.ui.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ecjtu.flesh.R
import com.ecjtu.flesh.cache.impl.MenuListCacheHelper
import com.ecjtu.flesh.presenter.MainActivityDelegate
import com.ecjtu.flesh.ui.adapter.TabPagerAdapter
import com.ecjtu.netcore.Constants
import com.ecjtu.netcore.jsoup.SoupFactory
import com.ecjtu.netcore.jsoup.impl.MenuSoup
import com.ecjtu.netcore.model.MenuModel
import com.ecjtu.netcore.network.AsyncNetwork
import com.ecjtu.netcore.network.IRequestCallback
import java.net.HttpURLConnection

/**
 * Created by Ethan_Xiang on 2018/2/1.
 */
class MzituFragment : Fragment {
    companion object {
        private const val TAG = "MzituFragment"
    }

    private var delegate: MainActivityDelegate? = null
    private var mViewPager: ViewPager? = null
    private var mTabLayout: TabLayout? = null
    private var mLastTabItem = 0

    constructor() : super()

    @SuppressLint("ValidFragment")
    constructor(delegate: MainActivityDelegate) : super() {
        this.delegate = delegate
    }

    fun setDelegate(delegate: MainActivityDelegate) {
        this.delegate = delegate
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.i(TAG, "onCreateView")
        return inflater?.inflate(R.layout.fragment_mzitu, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        Log.i(TAG, "onViewCreated")
        initView()
        val helper = MenuListCacheHelper(context.filesDir.absolutePath)
        mLastTabItem = delegate?.getLastTabItem(TabPagerAdapter::class) ?: 0
        var menuList: MutableList<MenuModel>? = null
        if (helper.get<Any>(TabPagerAdapter.CACHE_MENU_LIST + "_" + TabPagerAdapter::class.java) != null) {
            menuList = helper.get(TabPagerAdapter.CACHE_MENU_LIST + "_" + TabPagerAdapter::class.java)
        }
        if (menuList != null) {
            mViewPager?.adapter = TabPagerAdapter(menuList)
            if (userVisibleHint) {
                mTabLayout?.setupWithViewPager(mViewPager)
            }
        }
        val request = AsyncNetwork()
        request.request(Constants.HOST_MOBILE_URL, null)
        request.setRequestCallback(object : IRequestCallback {
            override fun onSuccess(httpURLConnection: HttpURLConnection?, response: String) {
                val values = SoupFactory.parseHtml(MenuSoup::class.java, response)
                if (values != null) {
                    activity.runOnUiThread {
                        var localList: List<MenuModel>? = null
                        if (values[MenuSoup::class.java.simpleName] != null) {
                            localList = values[MenuSoup::class.java.simpleName] as List<MenuModel>
                            if (menuList == null && localList != null) {
                                mViewPager?.adapter = TabPagerAdapter(localList)
                                if (userVisibleHint) {
                                    mTabLayout?.setupWithViewPager(mViewPager)
                                    mViewPager?.setCurrentItem(mLastTabItem)
                                }
                            } else {
                                var needUpdate = false
                                for (obj in localList) {
                                    if (menuList?.indexOf(obj) ?: 0 < 0) {
                                        menuList?.add(0, obj)
                                        needUpdate = true
                                    }
                                }
                                if (needUpdate) {
                                    mViewPager?.adapter?.notifyDataSetChanged()
                                }
                            }
                        }
                    }
                }
            }
        })
    }

    protected fun initView() {
        mViewPager = view!!.findViewById(R.id.view_pager) as ViewPager?
        mTabLayout = delegate?.getTabLayout()
        if (userVisibleHint) {
            attachTabLayout()
        }
    }

    private fun attachTabLayout() {
        mTabLayout?.removeAllTabs()
        mTabLayout?.setupWithViewPager(mViewPager)
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        Log.i(TAG, "setUserVisibleHint " + isVisibleToUser)
        if (isVisibleToUser) {
            attachTabLayout()
            mViewPager?.setCurrentItem(mLastTabItem)
        }
    }

    override fun onStop() {
        super.onStop()
        Log.i(TAG, "onStop tabIndex " + mTabLayout?.selectedTabPosition)
        mViewPager?.let {
            (mViewPager?.adapter as TabPagerAdapter?)?.onStop(context, mTabLayout?.selectedTabPosition ?: 0,
                    delegate?.isAppbarLayoutExpand() ?: false)
        }
    }

    override fun onResume() {
        super.onResume()
        Log.i(TAG, "onResume " + mTabLayout?.selectedTabPosition)
        mViewPager?.adapter?.let {
            (mViewPager?.adapter as TabPagerAdapter).onResume()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(TAG, "onDestroy " + mTabLayout?.selectedTabPosition)
        mViewPager?.let {
            (mViewPager?.adapter as TabPagerAdapter?)?.onDestroy()
        }
    }
}