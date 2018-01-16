package com.ecjtu.flesh.ui.adapter

import android.database.sqlite.SQLiteDatabase
import android.graphics.Bitmap
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.BitmapImageViewTarget
import com.bumptech.glide.request.target.Target
import com.ecjtu.flesh.R
import com.ecjtu.flesh.db.DatabaseManager
import com.ecjtu.flesh.db.table.impl.LikeTableImpl
import com.ecjtu.flesh.model.models.V33Model
import com.ecjtu.netcore.model.PageModel
import tv.danmaku.ijk.media.exo.video.IjkVideoView

/**
 * Created by Ethan_Xiang on 2018/1/16.
 */
open class VideoCardListAdapter(var pageModel: List<V33Model>) : RecyclerViewWrapAdapter<VideoCardListAdapter.VH>(), RequestListener<Bitmap>, View.OnClickListener {

    private var mDatabase: SQLiteDatabase? = null

    private var mLastClickPosition = -1

    override fun getItemCount(): Int {
        return pageModel.size
    }

    override fun onBindViewHolder(holder: VH?, position: Int) {
        val context = holder?.itemView?.context
        val params = holder?.itemView?.layoutParams
        val model = pageModel.get(position)
        holder?.textView?.text = model.title

        val videoUrl = model.videoUrl
        holder?.itemView?.setTag(R.id.extra_tag_2, videoUrl)
        holder?.itemView?.setOnClickListener(this)
        holder?.itemView?.setTag(R.id.extra_tag, position)
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): VH {
        val v = LayoutInflater.from(parent?.context).inflate(R.layout.layout_video_card_view, parent, false)
        return VH(v)
    }

    override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Bitmap>?, isFirstResource: Boolean): Boolean {
        return false
    }

    override fun onResourceReady(resource: Bitmap?, model: Any?, target: Target<Bitmap>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
        if (target is BitmapImageViewTarget) {
            val parent = target.view.parent?.parent
            val layoutParams = (parent as View).layoutParams
            var height = resource?.height ?: LinearLayout.LayoutParams.WRAP_CONTENT

            val bottom = parent.findViewById(R.id.bottom)
            height += bottom.height
            if (layoutParams.height != height) {
                layoutParams.height = height
            }
            val position = target.view.getTag(R.id.extra_tag) as Int
            target.view.setImageBitmap(resource)
            setHeight(position, height)
            bottom.visibility = View.VISIBLE
        }
        return true
    }

    private fun thumb2OriginalUrl(url: String): String? {
        return try {
            var localUrl = url.replace("/thumbs", "")
            var suffix = localUrl.substring(localUrl.lastIndexOf("/"))
            suffix = suffix.substring(suffix.indexOf("_") + 1)
            var end = suffix.substring(suffix.lastIndexOf("."))
            suffix = suffix.substring(0, suffix.lastIndexOf("_"))
            suffix += end
            localUrl.substring(0, localUrl.lastIndexOf("/") + 1) + suffix
        } catch (ex: Exception) {
            null
        }
    }

    override fun getItemViewType(position: Int): Int {
        return super.getItemViewType(position)
    }

    override fun onClick(v: View?) {
        val position = v?.getTag(R.id.extra_tag) as Int?
        position?.let {
            mLastClickPosition = position
        }
        val videoUrl = v?.getTag(R.id.extra_tag_2) as String?
        videoUrl?.let {
            val videoView = v?.findViewById(R.id.ijk_video) as IjkVideoView
            videoView.setVideoPath(videoUrl)
            videoView.requestFocus()
            videoView.start()
        }
    }

    override fun setHeight(position: Int, height: Int) {
        super.setHeight(position, height)
    }

    open fun onRelease() {
        mDatabase?.close()
    }

    open fun onResume() {
        if (mLastClickPosition >= 0) {
            notifyItemChanged(mLastClickPosition)
        }
        mLastClickPosition = -1
    }

    class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ijkVideoView = itemView.findViewById(R.id.ijk_video) as IjkVideoView
        val textView = itemView.findViewById(R.id.title) as TextView
        val heart = itemView.findViewById(R.id.heart) as ImageView
        val description = itemView.findViewById(R.id.description) as TextView

        init {
            heart.setOnClickListener { v: View? ->
                val manager = DatabaseManager.getInstance(v?.context)
                val db = manager?.getDatabase() as SQLiteDatabase
                val url = v?.getTag(R.id.extra_tag) as PageModel.ItemModel?
                if (url != null) {
                    val impl = LikeTableImpl()
                    if (impl.isLike(db, url.href)) {
                        impl.deleteLike(db, url.href)
                        v?.isActivated = false
                    } else {
                        impl.addLike(db, url.href)
                        v?.isActivated = true
                    }
                }
                db.close()
            }
        }
    }
}