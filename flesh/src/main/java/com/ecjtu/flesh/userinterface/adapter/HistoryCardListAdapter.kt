package com.ecjtu.flesh.userinterface.adapter

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
import com.ecjtu.flesh.Constants
import com.ecjtu.flesh.R
import com.ecjtu.flesh.db.DatabaseManager
import com.ecjtu.flesh.db.table.impl.HistoryTableImpl
import com.ecjtu.flesh.userinterface.activity.FullScreenImageActivity
import com.ecjtu.flesh.userinterface.activity.PageDetailActivity
import com.ecjtu.flesh.userinterface.fragment.IjkVideoFragment
import com.ecjtu.flesh.util.encrypt.SecretKeyUtils
import com.ecjtu.netcore.model.PageModel
import java.text.SimpleDateFormat
import java.util.*
import kotlin.concurrent.thread

/**
 * Created by Ethan_Xiang on 2017/9/19.
 */
class HistoryCardListAdapter(pageModel: PageModel) : CardListAdapter(pageModel) {

    private val mDateFormat1 = SimpleDateFormat("yyyy-MM-dd")
    private var mDateFormat2: SimpleDateFormat? = null

    private var mS3: AmazonS3Client? = null

    override fun onBindViewHolder(holder: VH?, position: Int) {
        super.onBindViewHolder(holder, position)
        if (mDateFormat2 == null) {
            mDateFormat2 = SimpleDateFormat(holder?.itemView?.context?.getString(R.string.simple_date_format_ymd) ?:
                    "yyyy-MM-dd",
                    Locale.getDefault())
        }
        val db = DatabaseManager.getInstance(holder?.itemView?.context)?.getDatabase()
        val impl = HistoryTableImpl()
        var time = ""
        if (db != null) {
            time = impl.getHistoryTime(db, pageModel.itemList[position].href)
        }
        db?.close()

        if (!TextUtils.isEmpty(time)) {
            val date = mDateFormat1.parse(time)
            holder?.description?.text = mDateFormat2?.format(date)
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
                    val db = DatabaseManager.getInstance(v.context)?.getDatabase() as SQLiteDatabase
                    val impl = HistoryTableImpl()
                    impl.addHistory(db, url)
                    db.close()
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
                                mS3?.setEndpoint(Constants.S3_URL)
                            }

                            if (mS3 != null) {
                                if (v.context != null) {
                                    v.post {
                                        val endDate = Calendar.getInstance()
                                        endDate.add(Calendar.HOUR, 1)
                                        val innerUrl = mS3?.generatePresignedUrl(bucketName, key, endDate.time)
                                        val intent = RotateNoCreateActivity.newInstance(v.context, IjkVideoFragment::class.java
                                                , Bundle().apply { putString(IjkVideoFragment.EXTRA_URI_PATH, innerUrl.toString()) })
                                        v.context.startActivity(intent)
                                        val db = DatabaseManager.getInstance(v.context)?.getDatabase() as SQLiteDatabase
                                        val impl = HistoryTableImpl()
                                        impl.addHistory(db, url)
                                        db.close()
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
}