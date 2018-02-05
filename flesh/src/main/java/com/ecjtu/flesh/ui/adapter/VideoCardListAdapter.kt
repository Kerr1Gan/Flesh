package com.ecjtu.flesh.ui.adapter

import android.database.sqlite.SQLiteDatabase
import android.graphics.Bitmap
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.BitmapImageViewTarget
import com.bumptech.glide.request.target.Target
import com.ecjtu.componentes.activity.RotateNoCreateActivity
import com.ecjtu.flesh.R
import com.ecjtu.flesh.model.models.V33Model
import com.ecjtu.flesh.ui.fragment.IjkVideoFragment
import tv.danmaku.ijk.media.exo.video.AndroidMediaController
import tv.danmaku.ijk.media.exo.video.IjkVideoView
import tv.danmaku.ijk.media.player.IMediaPlayer

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
        val model = pageModel.get(position)
        holder?.textView?.text = model.title

        holder?.ijkVideoView?.release(true)
        holder?.thumb?.visibility = View.VISIBLE

        val videoUrl = model.videoUrl
        holder?.itemView?.setTag(R.id.extra_tag_2, videoUrl)
        holder?.itemView?.setOnClickListener(this)
        holder?.itemView?.setTag(R.id.extra_tag, position)

        val imageView = holder?.thumb
        val options = RequestOptions()
        options.centerCrop()
        val url = pageModel.get(position).imageUrl /*thumb2OriginalUrl(pageModel.itemList[position].imgUrl)*/
        var host = ""
        if (url.startsWith("https://")) {
            host = url.replace("https://", "")
        } else if (url.startsWith("http://")) {
            host = url.replace("http://", "")
        }

        host = host.substring(0, if (host.indexOf("/") >= 0) host.indexOf("/") else host.length)
        val builder = LazyHeaders.Builder()
                .addHeader("User-Agent", "Mozilla/5.0 (Linux; Android 5.1.1; Nexus 6 Build/LYZ28E)  Chrome/60.0.3112.90 Mobile Safari/537.36")
                .addHeader("Accept", "image/webp,image/apng,image/*,*/*;q=0.8")
                .addHeader("Accept-Encoding", "gzip, deflate")
                .addHeader("Accept-Language", "zh-CN,zh;q=0.8")
                .addHeader("Host", host)
                .addHeader("Proxy-Connection", "keep-alive")
                .addHeader("Referer", "http://m.mzitu.com/")
        val glideUrl = GlideUrl(url, builder.build())
        url.let {
            imageView?.setTag(R.id.extra_tag, position)
            Glide.with(context).asBitmap().load(glideUrl).listener(this).apply(options).into(imageView)
            holder?.textView?.setText(pageModel.get(position).title)
        }
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
            var parent: View = target.view
            while (true) {
                if (parent.id == R.id.container) {
                    break
                }
                parent = parent.parent as View
            }
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
            val thumb = v.findViewById(R.id.thumb) as ImageView?
            if (videoView.isPlaying) {
                return@let
            }
            thumb?.visibility = View.INVISIBLE
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
        val thumb = itemView.findViewById(R.id.thumb) as ImageView
        val mediaController = AndroidMediaController(itemView.context)

        init {
//            heart.setOnClickListener { v: View? ->
//                val manager = DatabaseManager.getInstance(v?.context)
//                val db = manager?.getDatabase() as SQLiteDatabase
//                val url = v?.getTag(R.id.extra_tag) as PageModel.ItemModel?
//                if (url != null) {
//                    val impl = LikeTableImpl()
//                    if (impl.isLike(db, url.href)) {
//                        impl.deleteLike(db, url.href)
//                        v?.isActivated = false
//                    } else {
//                        impl.addLike(db, url.href)
//                        v?.isActivated = true
//                    }
//                }
//                db.close()
//            }
            heart.visibility = View.GONE
            ijkVideoView.setMediaController(mediaController)
            ijkVideoView.setOnInfoListener { mp, what, extra ->
                if (what == IMediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
                }
                return@setOnInfoListener false
            }
            mediaController.setMediaPlayerCallback {
                val videoUrl = itemView.getTag(R.id.extra_tag_2) as String?
                val intent = RotateNoCreateActivity.newInstance(itemView.context, IjkVideoFragment::class.java
                        , Bundle().apply { putString(IjkVideoFragment.EXTRA_URI_PATH, videoUrl) })
                itemView.context.startActivity(intent)
            }
        }
    }
}