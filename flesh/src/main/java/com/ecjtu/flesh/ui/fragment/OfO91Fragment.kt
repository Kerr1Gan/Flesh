package com.ecjtu.flesh.ui.fragment

import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.View
import com.ecjtu.flesh.cache.impl.MenuListCacheHelper
import com.ecjtu.flesh.cache.impl.VideoCacheHelper
import com.ecjtu.flesh.model.models.VideoModel
import com.ecjtu.flesh.ui.adapter.OfOTabPagerAdapter
import com.ecjtu.netcore.model.MenuModel
import com.ecjtu.netcore.network.AsyncNetwork
import com.ecjtu.netcore.network.IRequestCallback
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import kotlin.concurrent.thread

/**
 * Created by KerriGan on 2018/2/24.
 */
class OfO91Fragment : VideoListFragment() {
    companion object {
        private const val TAG = "OfO91Fragment"
    }

    private var mLoadingDialog: AlertDialog? = null
    private var mV33Menu: List<MenuModel>? = null
    private var mV33Cache: Map<String, List<VideoModel>>? = null

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.i(TAG, "onViewCreated")
        getToolbar().setTitle("OfO")
    }

    override fun onUserVisibleHintChanged(isVisibleToUser: Boolean) {
        super.onUserVisibleHintChanged(isVisibleToUser)
        if (isVisibleToUser) {
            attachTabLayout()
            if (mV33Menu == null || mV33Menu?.size == 0) {
                val req = AsyncNetwork().apply {
                    request(com.ecjtu.flesh.Constants.OFO_URL, null)
                    setRequestCallback(object : IRequestCallback {
                        override fun onSuccess(httpURLConnection: HttpURLConnection?, response: String) {
                            val menuModel = arrayListOf<MenuModel>()
                            val map = linkedMapOf<String, List<VideoModel>>()
                            try {
                                val jObj = JSONArray(response)
                                for (i in 0 until jObj.length()) {
                                    val jTitle = jObj[i] as JSONObject
                                    val title = jTitle.optString("title")
                                    val list = jTitle.optJSONArray("array")
                                    val modelList = arrayListOf<VideoModel>()
                                    for (j in 0 until list.length()) {
                                        val v33Model = VideoModel()
                                        val jItem = list[j] as JSONObject
                                        v33Model.baseUrl = jItem.optString("videoUrl")
                                        v33Model.imageUrl = jItem.optString("imageUrl")
                                        v33Model.title = jItem.optString("title")
                                        v33Model.videoUrl = jItem.optString("innerVideoUrl")
                                        modelList.add(v33Model)
                                    }
                                    map.put(title, modelList)
                                    val model = MenuModel(title, "")
                                    menuModel.add(model)
                                }
                            } catch (ex: Exception) {
                                ex.printStackTrace()
                            }

                            mLoadingDialog?.cancel()
                            mLoadingDialog = null
                            if (menuModel != null && map != null) {
                                Log.i(TAG, "load from server")
                            }
                            activity?.runOnUiThread {
                                if (getViewPager() != null && getViewPager()?.adapter == null) {
                                    getViewPager()?.adapter = OfOTabPagerAdapter(menuModel, getViewPager()!!)
                                    (getViewPager()?.adapter as OfOTabPagerAdapter).setMenuChildList(map)
                                    (getViewPager()?.adapter as OfOTabPagerAdapter?)?.notifyDataSetChanged(true)
                                } else {
                                    (getViewPager()?.adapter as OfOTabPagerAdapter).menu = menuModel
                                    (getViewPager()?.adapter as OfOTabPagerAdapter).setMenuChildList(map)
                                    (getViewPager()?.adapter as OfOTabPagerAdapter?)?.notifyDataSetChanged(false)
                                }
                                mV33Menu = menuModel
                                mV33Cache = map

                                if (userVisibleHint) {
                                    attachTabLayout()
                                }
                            }
                        }
                    })
                }

                if (mLoadingDialog == null && context != null) {
                    mLoadingDialog = AlertDialog.Builder(context).setTitle("加载中").setMessage("需要一小会时间")
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
                thread {
                    if (context != null) {
                        val helper = VideoCacheHelper(context.filesDir.absolutePath)
                        val helper2 = MenuListCacheHelper(context.filesDir.absolutePath)
                        mV33Menu = helper2.get("ofoMenu")
                        mV33Cache = helper.get("ofoCache")
                        val localMenu = mV33Menu
                        val localCache = mV33Cache
                        if (localMenu != null && localCache != null) {
                            mLoadingDialog?.cancel()
                            mLoadingDialog = null
                        }
                        if (localMenu != null && localCache != null) {
                            Log.i(TAG, "load from cache")
                        }
                        activity?.runOnUiThread {
                            if (localMenu != null && localCache != null) {
                                if (getViewPager() != null && getViewPager()?.adapter == null) {
                                    getViewPager()?.adapter = OfOTabPagerAdapter(localMenu, getViewPager()!!)
                                    (getViewPager()?.adapter as OfOTabPagerAdapter).setMenuChildList(localCache as MutableMap<String, List<VideoModel>>)
                                    (getViewPager()?.adapter as OfOTabPagerAdapter?)?.notifyDataSetChanged(true)
                                } else {
                                    (getViewPager()?.adapter as OfOTabPagerAdapter).menu = localMenu
                                    (getViewPager()?.adapter as OfOTabPagerAdapter).setMenuChildList(localCache as MutableMap<String, List<VideoModel>>)
                                    (getViewPager()?.adapter as OfOTabPagerAdapter?)?.notifyDataSetChanged(false)
                                }
                                if (userVisibleHint) {
                                    attachTabLayout()
                                }
                                getViewPager()?.setCurrentItem(getLastTabPosition())
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
                val helper = VideoCacheHelper(context.filesDir.absolutePath)
                val helper2 = MenuListCacheHelper(context.filesDir.absolutePath)
                if (mV33Menu != null && mV33Cache != null) {
                    helper2.put("ofoMenu", mV33Menu)
                    helper.put("ofoCache", mV33Cache)
                }
            }
        }
        Log.i(TAG, "onStop tabIndex " + getTabLayout()?.selectedTabPosition)
    }

    override fun getLastTabPositionKey(): String {
        return TAG + "_" + "last_tab_position"
    }

}