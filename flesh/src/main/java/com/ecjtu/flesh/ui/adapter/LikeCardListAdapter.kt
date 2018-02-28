package com.ecjtu.flesh.ui.adapter

import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import com.amazonaws.ClientConfiguration
import com.amazonaws.Protocol
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.regions.Region
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3Client
import com.ecjtu.componentes.activity.RotateNoCreateActivity
import com.ecjtu.flesh.R
import com.ecjtu.flesh.db.DatabaseManager
import com.ecjtu.flesh.db.table.impl.HistoryTableImpl
import com.ecjtu.flesh.ui.activity.FullScreenImageActivity
import com.ecjtu.flesh.ui.activity.PageDetailActivity
import com.ecjtu.flesh.ui.fragment.IjkVideoFragment
import com.ecjtu.flesh.util.encrypt.SecretKeyUtils
import com.ecjtu.netcore.model.PageModel
import java.util.*
import kotlin.concurrent.thread

/**
 * Created by Ethan_Xiang on 2017/9/19.
 */
class LikeCardListAdapter(pageModel: PageModel) : CardListAdapter(pageModel) {

    private var mHistory: List<PageModel.ItemModel>? = null
    private var mS3: AmazonS3Client? = null

    override fun onBindViewHolder(holder: VH?, position: Int) {
        super.onBindViewHolder(holder, position)
        if (mHistory == null) {
            val db = DatabaseManager.getInstance(holder?.itemView?.context)?.getDatabase()
            val impl = HistoryTableImpl()
            if (db != null) {
                mHistory = impl.getAllHistory(db)
            }
            db?.close()
        }

        if (mHistory != null) {
            val href = pageModel.itemList[position].href
            var index = 0
            for (url in mHistory!!) {
                if (href == url.href) {
                    index++
                }
            }
            holder?.description?.text = holder?.itemView?.resources?.getString(R.string.open_times, index)
        }
    }

    override fun onClick(v: View?) {
        val position = v?.getTag(R.id.extra_tag)
        position?.let {
            val item = pageModel.itemList[position as Int]
            val url = item.href
            if (item.type == 0) {
                if (!TextUtils.isEmpty(url) && url.startsWith("http://")) {
                    val intent = PageDetailActivity.newInstance(v.context, url, item.href, item.description, item.imgUrl)
                    v.context.startActivity(intent)
                    val db = DatabaseManager.getInstance(v.context)?.getDatabase() as SQLiteDatabase
                    val impl = HistoryTableImpl()
                    impl.addHistory(db, item.href)
                    db.close()
                } else {
                    FullScreenImageActivity.newInstance(v.context, item.imgUrl).apply {
                        v.context.startActivity(this)
                        val db = DatabaseManager.getInstance(v.context)?.getDatabase() as SQLiteDatabase
                        val impl = HistoryTableImpl()
                        impl.addHistory(db, item.href)
                        db.close()
                    }
                }
            } else {
                if (url.contains("http://")) {
                    val intent = RotateNoCreateActivity.newInstance(v.context, IjkVideoFragment::class.java
                            , Bundle().apply { putString(IjkVideoFragment.EXTRA_URI_PATH, url.toString()) })
                    v.context.startActivity(intent)
                } else {
                    thread {
                        try {
                            val keys = url.split("@,@")
                            val bucketName = keys[0]
                            val key = keys[1]
                            if (mS3 == null) {
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
                            }

                            if (mS3 != null) {
                                if (v.context != null) {
                                    v.post {
                                        val endDate = Calendar.getInstance()
                                        endDate.add(Calendar.HOUR, 1)
                                        val url = mS3?.generatePresignedUrl(bucketName, key, endDate.time)
                                        val intent = RotateNoCreateActivity.newInstance(v.context, IjkVideoFragment::class.java
                                                , Bundle().apply { putString(IjkVideoFragment.EXTRA_URI_PATH, url.toString()) })
                                        v.context.startActivity(intent)
                                    }
                                }
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            }
            setLastClickPosition(position)
        }
    }

    override fun onResume() {
        super.onResume()
        resetHistory()
    }

    fun resetHistory() {
        mHistory = null
    }
}