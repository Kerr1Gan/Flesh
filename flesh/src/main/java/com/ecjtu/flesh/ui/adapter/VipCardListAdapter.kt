package com.ecjtu.flesh.ui.adapter

import android.support.v7.widget.RecyclerView
import android.view.View
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.Bucket
import com.ecjtu.componentes.activity.RotateNoCreateActivity
import com.ecjtu.flesh.R
import com.ecjtu.flesh.model.models.V33Model
import com.ecjtu.flesh.ui.fragment.IjkVideoFragment
import com.ecjtu.flesh.ui.fragment.WebViewFragment
import java.util.*
import kotlin.concurrent.thread

/**
 * Created by Ethan_Xiang on 2018/2/22.
 */
class VipCardListAdapter(pageModel: List<V33Model>, recyclerView: RecyclerView, private val s3Client: AmazonS3Client, private val bucket: Bucket) : VideoCardListAdapter(pageModel, recyclerView) {
    override fun onClick(v: View?) {
        val position = v?.getTag(R.id.extra_tag) as Int?
        thread {
            val endDate = Calendar.getInstance()
            endDate.add(Calendar.HOUR, 1)
            val url = s3Client?.generatePresignedUrl(bucket.name, pageModel.get(position!!).title, endDate.time)
            v?.post {
                val intent = RotateNoCreateActivity.newInstance(v.context, WebViewFragment::class.java
                        , WebViewFragment.openUrl(url.toString()))
                v.context.startActivity(intent)
            }
        }
    }
}
