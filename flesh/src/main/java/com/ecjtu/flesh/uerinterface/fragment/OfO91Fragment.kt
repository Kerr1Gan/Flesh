package com.ecjtu.flesh.uerinterface.fragment

import android.content.DialogInterface
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.text.TextUtils
import android.util.Log
import android.view.View
import com.ecjtu.flesh.Constants
import com.ecjtu.flesh.cache.impl.MenuListCacheHelper
import com.ecjtu.flesh.cache.impl.VideoCacheHelper
import com.ecjtu.flesh.db.DatabaseManager
import com.ecjtu.flesh.db.table.impl.ClassPageTableImpl
import com.ecjtu.flesh.model.models.VideoModel
import com.ecjtu.flesh.uerinterface.adapter.OfOTabPagerAdapter
import com.ecjtu.netcore.model.MenuModel
import com.ecjtu.netcore.model.PageModel
import com.ecjtu.netcore.network.AsyncNetwork
import com.ecjtu.netcore.network.IRequestCallback
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.util.*
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
    private var mAccessible = false
    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.i(TAG, "onViewCreated")
        val title = arguments?.getString("title") ?: ""
        if (!TextUtils.isEmpty(title)) {
            getToolbar().setTitle(title)
        } else {
            getToolbar().setTitle("OfO")
        }
    }

    override fun onUserVisibleHintChanged(isVisibleToUser: Boolean) {
        super.onUserVisibleHintChanged(isVisibleToUser)
        if (isVisibleToUser) {
            AsyncNetwork().apply {
                request(Constants.QUESTION).setRequestCallback(object : IRequestCallback {
                    override fun onSuccess(httpURLConnection: HttpURLConnection?, response: String) {
                        val jArr = JSONArray(response)
                        try {
                            val jObj = jArr.get(Random(System.currentTimeMillis()).nextInt(jArr.length())) as JSONObject
                            val question = jObj.optString("question")
                            val answer1 = jObj.optString("answer1")
                            val answer2 = jObj.optString("answer2")
                            val answer3 = jObj.optString("answer3")
                            val answer = Integer.valueOf(jObj.getString("answer"))
                            if (!mAccessible) {
                                val listener = { dialog: DialogInterface, which: Int ->
                                    if (which == DialogInterface.BUTTON_POSITIVE) {
                                    } else if (which == DialogInterface.BUTTON_NEGATIVE) {
                                    } else if (which == DialogInterface.BUTTON_NEUTRAL) {
                                    }
                                    if (which == answer) {
                                        mAccessible = true
                                        doLoading()
                                    } else {
                                        if (activity != null) {
                                            activity.finish()
                                        }
                                    }
                                }
                                if (activity != null) {
                                    activity.runOnUiThread {
                                        if (activity == null)
                                            return@runOnUiThread
                                        AlertDialog.Builder(activity).setTitle("成年问答,未成年请赶紧离开！").
                                                setMessage(question).
                                                setPositiveButton(answer1, listener).
                                                setNegativeButton(answer2, listener).
                                                setNeutralButton(answer3, listener).
                                                setOnCancelListener { if (!mAccessible && activity != null) activity.finish() }.
                                                create().
                                                show()
                                    }
                                }
                            }
                        } catch (ex: Exception) {
                            ex.printStackTrace()
                        }
                    }
                })
            }
        }
    }

    private fun doLoading() {
        attachTabLayout()
        val arg = arguments
        val url = arg?.getString("url") ?: ""
        if ((mV33Menu == null || mV33Menu?.size == 0) && !TextUtils.isEmpty(url)) {
            val req = AsyncNetwork().apply {
                request(url, null)
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

                                if (context != null) {
                                    val impl = ClassPageTableImpl()
                                    val db = DatabaseManager.getInstance(context)?.getDatabase()
                                    val itemListModel = arrayListOf<PageModel.ItemModel>()
                                    for (videoModel in modelList) {
                                        val model = PageModel.ItemModel(videoModel.videoUrl, videoModel.title, videoModel.imageUrl, 1)
                                        itemListModel.add(model)
                                    }
                                    val pageModel = PageModel(itemListModel)
                                    pageModel.nextPage = ""
                                    db?.let {
                                        db.beginTransaction()
                                        impl.addPage(db, pageModel)
                                        db.setTransactionSuccessful()
                                        db.endTransaction()
                                    }
                                    db?.close()
                                }
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
                val local = context
                if (local != null) {
                    val helper = VideoCacheHelper(local.filesDir.absolutePath)
                    val helper2 = MenuListCacheHelper(local.filesDir.absolutePath)
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

    override fun onStop() {
        super.onStop()
        thread {
            val local = context
            if (local != null) {
                val helper = VideoCacheHelper(local.filesDir.absolutePath)
                val helper2 = MenuListCacheHelper(local.filesDir.absolutePath)
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