package com.ecjtu.flesh.ui.adapter

import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.View
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.Bucket
import com.ecjtu.componentes.activity.RotateNoCreateActivity
import com.ecjtu.flesh.R
import com.ecjtu.flesh.db.DatabaseManager
import com.ecjtu.flesh.db.table.impl.ClassPageTableImpl
import com.ecjtu.flesh.db.table.impl.HistoryTableImpl
import com.ecjtu.flesh.model.models.VideoModel
import com.ecjtu.flesh.ui.fragment.IjkVideoFragment
import com.ecjtu.netcore.model.PageModel
import java.util.*
import kotlin.concurrent.thread

/**
 * Created by Ethan_Xiang on 2018/2/22.
 */
class VipCardListAdapter(pageModel: List<VideoModel>, recyclerView: RecyclerView, private val s3Client: AmazonS3Client, private val bucket: Bucket) : VideoCardListAdapter(pageModel, recyclerView) {
    override fun onClick(v: View?) {
        val position = v?.getTag(R.id.extra_tag) as Int?
        thread {
            val endDate = Calendar.getInstance()
            endDate.add(Calendar.HOUR, 1)
            val url = s3Client?.generatePresignedUrl(bucket.name, pageModel.get(position!!).title, endDate.time)
            v?.post {
                //                val intent = Intent("android.intent.action.VIEW")
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//                intent.putExtra("oneshot", 0)
//                intent.putExtra("configchange", 0)
//                val uri = Uri.parse(url.toString())
//                intent.setDataAndType(uri, "video/*")
                val itemListModel = arrayListOf<PageModel.ItemModel>()
                val vModel = pageModel.get(position!!)
                val model = PageModel.ItemModel(vModel.videoUrl, vModel.title, vModel.imageUrl, 1)
                itemListModel.add(model)
                val pageModel = PageModel(itemListModel)
                pageModel.nextPage = ""

                val db = DatabaseManager.getInstance(v.context)?.getDatabase() as SQLiteDatabase
                db.beginTransaction()
                val impl = ClassPageTableImpl()
                impl.addPage(db, pageModel)
                db.setTransactionSuccessful()
                db.endTransaction()

                val impl2 = HistoryTableImpl()
                impl2.addHistory(db, url.toString())
                db.close()
                val intent = RotateNoCreateActivity.newInstance(v.context, IjkVideoFragment::class.java
                        , Bundle().apply { putString(IjkVideoFragment.EXTRA_URI_PATH, url.toString()) })
                v.context.startActivity(intent)
            }
        }
    }
}
