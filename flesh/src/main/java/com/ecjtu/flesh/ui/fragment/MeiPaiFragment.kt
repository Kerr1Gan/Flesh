package com.ecjtu.flesh.ui.fragment

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ecjtu.flesh.R
import com.ecjtu.flesh.presenter.MainActivityDelegate
import com.ecjtu.netcore.network.AsyncNetwork
import com.ecjtu.netcore.network.IRequestCallback
import java.net.HttpURLConnection

/**
 * Created by xiang on 2018/2/8.
 */
class MeiPaiFragment : Fragment() {
    companion object {
        private const val TAG = "MeiPaiFragment"
    }

    private var delegate: MainActivityDelegate? = null
    private var mViewPager: ViewPager? = null
    private var mTabLayout: TabLayout? = null

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
        if (isVisibleToUser) {
            val req = AsyncNetwork().apply {
                request(com.ecjtu.flesh.Constants.WEIPAI_URL, null)
                setRequestCallback(object : IRequestCallback {
                    override fun onSuccess(httpURLConnection: HttpURLConnection?, response: String) {

                    }
                })
            }
        }
    }
}