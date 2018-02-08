package com.ecjtu.flesh.ui.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ecjtu.flesh.R
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
class MzituFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_mzitu, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        val request = AsyncNetwork()
        var mViewPager = view?.findViewById(R.id.view_pager) as ViewPager
        request.request(Constants.HOST_MOBILE_URL, null)
        request.setRequestCallback(object : IRequestCallback {
            override fun onSuccess(httpURLConnection: HttpURLConnection?, response: String) {
                val values = SoupFactory.parseHtml(MenuSoup::class.java, response)
                if (values != null) {
                    activity.runOnUiThread {
                        var localList: List<MenuModel>? = null
                        if (values[MenuSoup::class.java.simpleName] != null) {
                            localList = values[MenuSoup::class.java.simpleName] as List<MenuModel>
                            mViewPager.adapter = TabPagerAdapter(localList)
                        }
                    }
                }
            }
        })
    }
}