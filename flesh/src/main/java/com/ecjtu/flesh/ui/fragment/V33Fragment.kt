package com.ecjtu.flesh.ui.fragment

import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.View
import com.ecjtu.flesh.cache.impl.MenuListCacheHelper
import com.ecjtu.flesh.cache.impl.V33CacheHelper
import com.ecjtu.flesh.model.models.VideoModel
import com.ecjtu.flesh.ui.adapter.V33TabPagerAdapter
import com.ecjtu.netcore.model.MenuModel
import com.ecjtu.netcore.network.AsyncNetwork
import com.ecjtu.netcore.network.IRequestCallback
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import kotlin.concurrent.thread

/**
 * Created by KerriGan on 2018/2/20.
 */
class V33Fragment : VideoListFragment() {
    companion object {
        private const val TAG = "V33Fragment"
    }

    private var mLoadingDialog: AlertDialog? = null
    private var mV33Menu: List<MenuModel>? = null
    private var mV33Cache: Map<String, List<VideoModel>>? = null

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.i(TAG, "onViewCreated")
        getToolbar().setTitle("爱恋")
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        Log.i(TAG, "setUserVisibleHint " + isVisibleToUser)
    }

    override fun onUserVisibleHintChanged(isVisibleToUser: Boolean) {
        super.onUserVisibleHintChanged(isVisibleToUser)
        if (isVisibleToUser) {
            attachTabLayout()
            if (mV33Menu == null || mV33Menu?.size == 0) {
                val req = AsyncNetwork().apply {
                    request(com.ecjtu.flesh.Constants.V33_URL, null)
                    setRequestCallback(object : IRequestCallback {
                        override fun onSuccess(httpURLConnection: HttpURLConnection?, response: String) {
                            val menuModel = arrayListOf<MenuModel>()
                            val map = linkedMapOf<String, List<VideoModel>>()
                            try {
                                val jObj = JSONArray(response)
                                for (i in 0 until jObj.length()) {
                                    val jTitle = jObj[i] as JSONObject
                                    val title = jTitle.optString("title")
                                    val list = jTitle.optJSONArray("list")
                                    val modelList = arrayListOf<VideoModel>()
                                    for (j in 0 until list.length()) {
                                        val v33Model = VideoModel()
                                        val jItem = list[j] as JSONObject
                                        v33Model.baseUrl = jItem.optString("baseUrl")
                                        v33Model.imageUrl = jItem.optString("imageUrl")
                                        v33Model.title = jItem.optString("title")
                                        v33Model.videoUrl = jItem.optString("videoUrl")
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
                            activity.runOnUiThread {
                                if (getViewPager() != null && getViewPager()?.adapter == null) {
                                    getViewPager()?.adapter = V33TabPagerAdapter(menuModel, getViewPager()!!)
                                    (getViewPager()?.adapter as V33TabPagerAdapter).setMenuChildList(map)
                                    (getViewPager()?.adapter as V33TabPagerAdapter?)?.notifyDataSetChanged(true)
                                } else {
                                    (getViewPager()?.adapter as V33TabPagerAdapter).menu = menuModel
                                    (getViewPager()?.adapter as V33TabPagerAdapter).setMenuChildList(map)
                                    (getViewPager()?.adapter as V33TabPagerAdapter?)?.notifyDataSetChanged(false)
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

                if (mLoadingDialog == null) {
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
                    val helper = V33CacheHelper(context.filesDir.absolutePath)
                    val helper2 = MenuListCacheHelper(context.filesDir.absolutePath)
                    mV33Menu = helper2.get("v33menu")
                    mV33Cache = helper.get("v33cache")
                    val localMenu = mV33Menu
                    val localCache = mV33Cache
                    if (localMenu != null && localCache != null) {
                        mLoadingDialog?.cancel()
                        mLoadingDialog = null
                    }
                    if (localMenu != null && localCache != null) {
                        Log.i(TAG, "load from cache")
                    }
                    activity.runOnUiThread {
                        if (localMenu != null && localCache != null) {
                            if (getViewPager() != null && getViewPager()?.adapter == null) {
                                getViewPager()?.adapter = V33TabPagerAdapter(localMenu, getViewPager()!!)
                                (getViewPager()?.adapter as V33TabPagerAdapter).setMenuChildList(localCache as MutableMap<String, List<VideoModel>>)
                                (getViewPager()?.adapter as V33TabPagerAdapter?)?.notifyDataSetChanged(true)
                            } else {
                                (getViewPager()?.adapter as V33TabPagerAdapter).menu = localMenu
                                (getViewPager()?.adapter as V33TabPagerAdapter).setMenuChildList(localCache as MutableMap<String, List<VideoModel>>)
                                (getViewPager()?.adapter as V33TabPagerAdapter?)?.notifyDataSetChanged(false)
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

    override fun onStop() {
        super.onStop()
        thread {
            if (context != null) {
                val helper = V33CacheHelper(context.filesDir.absolutePath)
                val helper2 = MenuListCacheHelper(context.filesDir.absolutePath)
                if (mV33Menu != null && mV33Cache != null) {
                    helper2.put("v33menu", mV33Menu)
                    helper.put("v33cache", mV33Cache)
                }
            }
        }
        Log.i(TAG, "onStop tabIndex " + getTabLayout()?.selectedTabPosition)
    }

    override fun getLastTabPositionKey(): String {
        return TAG + "_" + "last_tab_position"
    }

}