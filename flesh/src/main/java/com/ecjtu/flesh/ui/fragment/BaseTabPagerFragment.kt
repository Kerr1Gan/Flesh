package com.ecjtu.flesh.ui.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.preference.PreferenceManager
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ecjtu.flesh.R
import com.ecjtu.flesh.presenter.MainActivityDelegate
import com.ecjtu.flesh.ui.adapter.IChangeTab
import com.ecjtu.flesh.ui.adapter.TabPagerAdapter

/**
 * Created by xiang on 2018/2/9.
 */
abstract class BaseTabPagerFragment : Fragment, ViewPager.OnPageChangeListener, IChangeTab {

    companion object {
        private const val TAG = "BaseTabPagerFragment"
    }

    private var delegate: MainActivityDelegate? = null
    private var mViewPager: ViewPager? = null
    private var mTabLayout: TabLayout? = null
    private var mLastTabItem = 0
    private val mHandler = Handler()
    private var mLastTabPosition = -1

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
        return inflater?.inflate(R.layout.fragment_base_tab_pager, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        Log.i(TAG, "onViewCreated")
        initView()
        if (getLastTabPosition() < 0) {
            setLastTabPosition(getCompatLastTabPosition())
        }
    }

    open protected fun initView() {
        mViewPager = view!!.findViewById(R.id.view_pager) as ViewPager?
        mTabLayout = delegate?.getTabLayout()
        if (userVisibleHint) {
            attachTabLayout()
        }
        mViewPager?.addOnPageChangeListener(this)
    }

    fun setTabLayout(tabLayout: TabLayout) {
        mTabLayout = tabLayout
    }

    protected fun attachTabLayout() {
        mTabLayout?.removeAllTabs()
        mTabLayout?.setupWithViewPager(mViewPager)
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        Log.i(TAG, "setUserVisibleHint " + isVisibleToUser)
        mHandler.post {
            onUserVisibleHintChanged(isVisibleToUser)
        }
    }

    open fun onUserVisibleHintChanged(isVisibleToUser: Boolean) {
        Log.i(TAG, "onUserVisibleHintChanged " + isVisibleToUser)
        if (isVisibleToUser) {
            attachTabLayout()
            mViewPager?.setCurrentItem(mLastTabItem)
        } else {
            saveLastTabPosition()
        }
    }

    override fun onStop() {
        super.onStop()
        Log.i(TAG, "onStop tabIndex " + mTabLayout?.selectedTabPosition)
        mViewPager?.let {
            (mViewPager?.adapter as TabPagerAdapter?)?.onStop(context, mTabLayout?.selectedTabPosition ?: 0,
                    delegate?.isAppbarLayoutExpand() ?: false)
        }
        if (getLastTabPosition() < 0) {
            setLastTabPosition(getCompatLastTabPosition())
        } else {
            setLastTabPosition(getViewPager()?.currentItem ?: 0)
        }
        saveLastTabPosition()
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

    open fun getHandler(): Handler {
        return mHandler
    }

    open fun getCompatLastTabPosition(): Int {
        return if (mLastTabPosition >= 0) mLastTabPosition else PreferenceManager.getDefaultSharedPreferences(context).
                getInt(getLastTabPositionKey(), 0)
    }

    open fun getLastTabPosition(): Int = mLastTabPosition

    open fun setLastTabPosition(position: Int) {
        mLastTabPosition = position
    }

    open fun saveLastTabPosition() {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putInt(getLastTabPositionKey(), getCompatLastTabPosition()).apply()
    }

    open fun getViewPager(): ViewPager? {
        return mViewPager
    }

    open fun getDelegate(): MainActivityDelegate? {
        return delegate
    }

    open fun getTabLayout(): TabLayout? {
        return mTabLayout
    }

    open fun getLastTabPositionKey(): String {
        return ""
    }

    /**
     * This method will be invoked when the current page is scrolled, either as part
     * of a programmatically initiated smooth scroll or a user initiated touch scroll.
     *
     * @param position Position index of the first page currently being displayed.
     *                 Page position+1 will be visible if positionOffset is nonzero.
     * @param positionOffset Value from [0, 1) indicating the offset from the page at position.
     * @param positionOffsetPixels Value in pixels indicating the offset from position.
     */
    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

    }

    /**
     * This method will be invoked when a new page becomes selected. Animation is not
     * necessarily complete.
     *
     * @param position Position index of the new selected page.
     */
    override fun onPageSelected(position: Int) {

    }

    /**
     * Called when the scroll state changes. Useful for discovering when the user
     * begins dragging, when the pager is automatically settling to the current page,
     * or when it is fully stopped/idle.
     *
     * @param state The new scroll state.
     * @see ViewPager.SCROLL_STATE_IDLE
     *
     * @see ViewPager.SCROLL_STATE_DRAGGING
     *
     * @see ViewPager.SCROLL_STATE_SETTLING
     */
    override fun onPageScrollStateChanged(state: Int) {

    }

    override fun onSelectTab() {

    }

    override fun onUnSelectTab() {
        if (getLastTabPosition() < 0) {
            setLastTabPosition(getCompatLastTabPosition())
        } else {
            setLastTabPosition(getViewPager()?.currentItem ?: 0)
        }
        saveLastTabPosition()
    }
}