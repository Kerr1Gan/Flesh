package com.ecjtu.flesh.userinterface.fragment

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.ecjtu.flesh.cache.impl.MeiPaiCacheHelper
import com.ecjtu.flesh.cache.impl.MenuListCacheHelper
import com.ecjtu.flesh.model.models.MeiPaiModel
import com.ecjtu.flesh.userinterface.adapter.MeiPaiPagerAdapter
import com.ecjtu.netcore.model.MenuModel
import com.ecjtu.netcore.network.AsyncNetwork
import com.ecjtu.netcore.network.IRequestCallback
import java.net.HttpURLConnection
import kotlin.concurrent.thread

/**
 * Created by xiang on 2018/2/8.
 */
class MeiPaiFragment : VideoListFragment(), BaseTabPagerFragment.IDelegate {
    companion object {
        private const val TAG = "MeiPaiFragment"

    }

    private var mLoadingDialog: AlertDialog? = null

    private var mMeiPaiMenu: List<MenuModel>? = null
    private var mMeiPaiCache: Map<String, List<MeiPaiModel>>? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.i(TAG, "onViewCreated")
        getToolbar().setTitle("美拍")
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
    }

    override fun onUserVisibleHintChanged(isVisibleToUser: Boolean) {
        super.onUserVisibleHintChanged(isVisibleToUser)
        val lastTabPosition = getLastTabPosition()
        if (isVisibleToUser) {
            attachTabLayout()
            if (mMeiPaiMenu == null && mMeiPaiMenu?.size ?: 0 == 0) {
                val req = AsyncNetwork().apply {
                    request(com.ecjtu.flesh.Constants.WEIPAI_URL, null)
                    setRequestCallback(object : IRequestCallback {
                        override fun onSuccess(httpURLConnection: HttpURLConnection?, response: String) {
                            val maps = MeiPaiModel.getMeiPaiModelByJsonString(response)
                            val menu = arrayListOf<MenuModel>()
                            for (entry in maps) {
                                val menuModel = MenuModel(entry.key, "")
                                menu.add(menuModel)
                            }

                            mLoadingDialog?.cancel()
                            mLoadingDialog = null
                            activity?.runOnUiThread {
                                if (getViewPager() != null && getViewPager()?.adapter == null) {
                                    getViewPager()?.adapter = MeiPaiPagerAdapter(menu, getViewPager()!!)
                                    (getViewPager()?.adapter as MeiPaiPagerAdapter).setMeiPaiList(maps)
                                    (getViewPager()?.adapter as MeiPaiPagerAdapter?)?.notifyDataSetChanged(true)
                                } else {
                                    (getViewPager()?.adapter as MeiPaiPagerAdapter).menu = menu
                                    (getViewPager()?.adapter as MeiPaiPagerAdapter).setMeiPaiList(maps)
                                    (getViewPager()?.adapter as MeiPaiPagerAdapter?)?.notifyDataSetChanged(false)
                                }
                                if (userVisibleHint) {
                                    attachTabLayout()
                                }
                            }
                        }
                    })
                }
                if (mLoadingDialog == null) {
                    Handler().post {
                        if (context == null) return@post
                        mLoadingDialog = AlertDialog.Builder(context!!).setTitle("加载中").setMessage("需要一小会时间")
                                .setNegativeButton("取消", { dialog, which ->
                                    thread {
                                        req.cancel()
                                    }
                                })
                                .setCancelable(false)
                                .setOnCancelListener {
                                    mLoadingDialog = null
                                }.create()
                        mLoadingDialog?.show()
                    }
                }
                thread {
                    if (context != null) {
                        val helper = MeiPaiCacheHelper(context?.filesDir?.absolutePath)
                        val helper2 = MenuListCacheHelper(context?.filesDir?.absolutePath)
                        mMeiPaiMenu = helper2.get("meipaimenu")
                        mMeiPaiCache = helper.get("meipaicache")
                        val localMenu = mMeiPaiMenu
                        val localCache = mMeiPaiCache
                        activity?.runOnUiThread {
                            if (localMenu != null && localCache != null) {
                                if (getViewPager() != null && getViewPager()?.adapter == null) {
                                    getViewPager()?.adapter = MeiPaiPagerAdapter(localMenu, getViewPager()!!)
                                    (getViewPager()?.adapter as MeiPaiPagerAdapter).setMeiPaiList(localCache as MutableMap<String, List<MeiPaiModel>>)
                                    (getViewPager()?.adapter as MeiPaiPagerAdapter?)?.notifyDataSetChanged(true)
                                } else {
                                    (getViewPager()?.adapter as MeiPaiPagerAdapter).menu = localMenu
                                    (getViewPager()?.adapter as MeiPaiPagerAdapter).setMeiPaiList(localCache as MutableMap<String, List<MeiPaiModel>>)
                                    (getViewPager()?.adapter as MeiPaiPagerAdapter?)?.notifyDataSetChanged(false)
                                }
                                if (userVisibleHint) {
                                    attachTabLayout()
                                }
                                getViewPager()?.setCurrentItem(lastTabPosition)
                                mLoadingDialog?.cancel()
                                mLoadingDialog = null

                            }
                        }
                    }
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        thread {
            if (context != null) {
                val helper = MeiPaiCacheHelper(context?.filesDir?.absolutePath)
                val helper2 = MenuListCacheHelper(context?.filesDir?.absolutePath)
                if (mMeiPaiMenu != null && mMeiPaiCache != null) {
                    helper2.put("meipaimenu", mMeiPaiMenu)
                    helper.put("meipaicache", mMeiPaiCache)
                }
            }
        }
    }

    override fun getLastTabPositionKey(): String {
        return TAG + "_" + "last_tab_position"
    }
}