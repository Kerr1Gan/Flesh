package com.ecjtu.flesh.ui.fragment

import com.amazonaws.ClientConfiguration
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.regions.Region
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3Client
import com.ecjtu.flesh.util.encrypt.SecretKeyUtils
import kotlin.concurrent.thread


/**
 * Created by KerriGan on 2018/2/21.
 */
class VipFragment : VideoListFragment() {

    private var mS3: AmazonS3Client? = null

    override fun onUserVisibleHintChanged(isVisibleToUser: Boolean) {
        super.onUserVisibleHintChanged(isVisibleToUser)
        if (isVisibleToUser) {
            if (mS3 == null) {
                thread {
                    var secretKey = SecretKeyUtils.getKeyFromServer()
                    val content = SecretKeyUtils.getS3InfoFromServer(secretKey!!.key)
                    val params = content.split(",")
                    val provider = BasicAWSCredentials(params[0], params[1])
                    val config = ClientConfiguration()
                    mS3 = AmazonS3Client(provider, config)
                    val region = Region.getRegion(Regions.CN_NORTH_1)
                    mS3?.setRegion(region)
                    mS3?.setEndpoint("s3.ap-northeast-2.amazonaws.com")
                    val buckets = mS3?.listBuckets()
                    val objList = mS3?.listObjects(buckets?.get(0)?.name)
                }
            }
        }


    }
}