package com.ecjtu.flesh.ui.fragment

import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.services.s3.AmazonS3Client
import com.ecjtu.flesh.util.encrypt.SecretKeyUtils
import kotlin.concurrent.thread


/**
 * Created by KerriGan on 2018/2/21.
 */
class VipFragment : VideoListFragment() {

    override fun onUserVisibleHintChanged(isVisibleToUser: Boolean) {
        super.onUserVisibleHintChanged(isVisibleToUser)
        if (isVisibleToUser) {
            thread {
                var secretKey = SecretKeyUtils.getKeyFromServer()
                SecretKeyUtils.encode("")
            }
            val provider = BasicAWSCredentials("", "")
            val s3 = AmazonS3Client(provider)
        }


    }
}