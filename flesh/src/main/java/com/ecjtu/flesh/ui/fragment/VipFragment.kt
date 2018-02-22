package com.ecjtu.flesh.ui.fragment

import com.amazonaws.ClientConfiguration
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.regions.Region
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.Bucket
import com.amazonaws.services.s3.model.ObjectListing
import com.ecjtu.flesh.ui.adapter.VipTabPagerAdapter
import com.ecjtu.flesh.util.encrypt.SecretKeyUtils
import com.ecjtu.netcore.model.MenuModel
import kotlin.concurrent.thread


/**
 * Created by KerriGan on 2018/2/21.
 */
class VipFragment : VideoListFragment() {

    private var mS3: AmazonS3Client? = null
    private var mBuckets: List<Bucket>? = null
    override fun onUserVisibleHintChanged(isVisibleToUser: Boolean) {
        super.onUserVisibleHintChanged(isVisibleToUser)
        if (isVisibleToUser) {
            if (mS3 == null) {
                thread {
                    val secretKey = SecretKeyUtils.getKeyFromServer()
                    val content = SecretKeyUtils.getS3InfoFromServer(secretKey!!.key)
                    val params = content.split(",")
                    val provider = BasicAWSCredentials(params[0], params[1])
                    val config = ClientConfiguration()
                    mS3 = AmazonS3Client(provider, config)
                    val region = Region.getRegion(Regions.CN_NORTH_1)
                    mS3?.setRegion(region)
                    mS3?.setEndpoint("s3.ap-northeast-2.amazonaws.com")
                    val buckets = mS3?.listBuckets()
                    mBuckets = buckets
//                    val endDate = Calendar.getInstance()
//                    endDate.add(Calendar.HOUR, 1)
//                    mS3?.generatePresignedUrl(buckets?.get(0)?.name, objList!!.objectSummaries[0].key, endDate.time)
                    val menuModel = mutableListOf<MenuModel>()
                    if (mBuckets != null) {
                        for (bucket in mBuckets!!) {
                            val menu = MenuModel(bucket.name, "")
                            menuModel.add(menu)
                        }
                        activity.runOnUiThread {
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
            }
        }
    }
}