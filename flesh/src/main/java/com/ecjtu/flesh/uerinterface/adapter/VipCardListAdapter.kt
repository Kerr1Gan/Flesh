package com.ecjtu.flesh.uerinterface.adapter

import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.net.Uri
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.View
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.Bucket
import com.bumptech.glide.Glide
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import com.bumptech.glide.request.RequestOptions
import com.ecjtu.componentes.activity.RotateNoCreateActivity
import com.ecjtu.flesh.R
import com.ecjtu.flesh.db.DatabaseManager
import com.ecjtu.flesh.db.table.impl.ClassPageTableImpl
import com.ecjtu.flesh.db.table.impl.HistoryTableImpl
import com.ecjtu.flesh.db.table.impl.LikeTableImpl
import com.ecjtu.flesh.model.models.VideoModel
import com.ecjtu.flesh.uerinterface.fragment.IjkVideoFragment
import com.ecjtu.netcore.model.PageModel
import java.util.*
import kotlin.concurrent.thread

/**
 * Created by Ethan_Xiang on 2018/2/22.
 */
class VipCardListAdapter(pageModel: List<VideoModel>, recyclerView: RecyclerView, private val s3Client: AmazonS3Client, private val bucket: Bucket) : VideoCardListAdapter(pageModel, recyclerView) {

    override fun onBindViewHolder(holder: VH?, position: Int) {
        super.onBindViewHolder(holder, position)
        val url = pageModel.get(position).videoUrl
        holder?.heart?.setTag(R.id.extra_tag_2, url)
        holder?.heart?.setTag(R.id.extra_tag_3, position)
        //db
        val href = url
        if (getDatabase() != null && getDatabase()?.isOpen == true) {
            val impl = LikeTableImpl()
            holder?.heart?.isActivated = impl.isLike(getDatabase()!!, href)
        }
        if (!TextUtils.isEmpty(url)) {
            val options = RequestOptions()
            options.centerCrop()
            val imageUrl = pageModel[position].imageUrl
            val builder = LazyHeaders.Builder()
            val glideUrl = GlideUrl(imageUrl, builder.build())
            url.let {
                Glide.with(holder?.itemView?.context).asBitmap().load(glideUrl).listener(this).apply(options).into(holder?.thumb)
            }
        }
    }

    override fun onClick(v: View?) {
        val position = v?.getTag(R.id.extra_tag) as Int?
        thread {
            try {
                val endDate = Calendar.getInstance()
                endDate.add(Calendar.HOUR, 1)
                val url = s3Client?.generatePresignedUrl(bucket.name, pageModel.get(position!!).title, endDate.time)
                v?.post {
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
                    impl2.addHistory(db, this@VipCardListAdapter.pageModel.get(position).videoUrl)
                    db.close()

                    if (url.toString().startsWith("https")) {
                        val intent = Intent("android.intent.action.VIEW")
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        intent.putExtra("oneshot", 0)
                        intent.putExtra("configchange", 0)
                        val uri = Uri.parse(url.toString())
                        intent.setDataAndType(uri, "video/*")
                        v.context.startActivity(intent)
                    } else {
                        val intent = RotateNoCreateActivity.newInstance(v.context, IjkVideoFragment::class.java
                                , Bundle().apply { putString(IjkVideoFragment.EXTRA_URI_PATH, url.toString()) })
                        v.context.startActivity(intent)
                    }
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
    }

    override fun getHeartClickListener(): (View?) -> Unit {
        return { v ->
            val manager = DatabaseManager.getInstance(v?.context)
            val db = manager?.getDatabase() as SQLiteDatabase
            val url = v?.getTag(R.id.extra_tag_2) as String?
            if (url != null) {
                val impl = LikeTableImpl()
                if (impl.isLike(db, url)) {
                    impl.deleteLike(db, url)
                    v?.isActivated = false
                } else {
                    impl.addLike(db, url)
                    v?.isActivated = true
                }
            }
            db.close()
        }
    }
}
