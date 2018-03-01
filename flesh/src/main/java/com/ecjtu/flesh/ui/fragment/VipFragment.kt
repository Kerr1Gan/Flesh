package com.ecjtu.flesh.ui.fragment

import android.content.DialogInterface
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.View
import com.amazonaws.ClientConfiguration
import com.amazonaws.Protocol
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.regions.Region
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.Bucket
import com.ecjtu.flesh.Constants
import com.ecjtu.flesh.ui.adapter.VipTabPagerAdapter
import com.ecjtu.flesh.util.encrypt.SecretKeyUtils
import com.ecjtu.netcore.model.MenuModel
import com.ecjtu.netcore.network.AsyncNetwork
import com.ecjtu.netcore.network.IRequestCallback
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.util.*
import kotlin.concurrent.thread


/**
 * Created by KerriGan on 2018/2/21.
 */
class VipFragment : VideoListFragment() {

    private var mS3: AmazonS3Client? = null
    private var mBuckets: List<Bucket>? = null
    private var mRequest: Thread? = null
    private var mAccessible = false

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getToolbar().setTitle("Vip")
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
                                        AlertDialog.Builder(context).setTitle("回答问题才能进入").
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
        if (mS3 == null) {
            mRequest = thread {
                try {
                    val secretKey = SecretKeyUtils.getKeyFromServer()
                    val content = SecretKeyUtils.getS3InfoFromServer(secretKey!!.key)
                    val params = content.split(",")
                    val provider = BasicAWSCredentials(params[0], params[1])
                    val config = ClientConfiguration()
                    config.protocol = Protocol.HTTP
                    mS3 = AmazonS3Client(provider, config)
                    val region = Region.getRegion(Regions.CN_NORTH_1)
                    mS3?.setRegion(region)
                    mS3?.setEndpoint("s3.ap-northeast-2.amazonaws.com")
                    val buckets = mS3?.listBuckets()
                    mBuckets = buckets
                    val menuModel = mutableListOf<MenuModel>()
                    if (mBuckets != null) {
                        for (bucket in mBuckets!!) {
                            val menu = MenuModel(bucket.name, "")
                            menuModel.add(menu)
                        }
                        if (activity != null && mRequest?.isInterrupted == false) {
                            activity.runOnUiThread {
                                if (mRequest?.isInterrupted == true) {
                                    return@runOnUiThread
                                }
                                if (getViewPager() != null && getViewPager()?.adapter == null) {
                                    getViewPager()?.adapter = VipTabPagerAdapter(menuModel, getViewPager()!!)
                                    (getViewPager()?.adapter as VipTabPagerAdapter?)?.setBucketsList(mBuckets!!, mS3!!)
                                    (getViewPager()?.adapter as VipTabPagerAdapter?)?.notifyDataSetChanged(true)
                                } else {
                                    (getViewPager()?.adapter as VipTabPagerAdapter).menu = menuModel
                                    (getViewPager()?.adapter as VipTabPagerAdapter?)?.setBucketsList(mBuckets!!, mS3!!)
                                    (getViewPager()?.adapter as VipTabPagerAdapter?)?.notifyDataSetChanged(false)
                                }
                                if (userVisibleHint) {
                                    attachTabLayout()
                                }
                            }
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mRequest?.interrupt()
    }
}