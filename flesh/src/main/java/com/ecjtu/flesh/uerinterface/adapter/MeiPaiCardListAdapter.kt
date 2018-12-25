package com.ecjtu.flesh.uerinterface.adapter

import android.database.sqlite.SQLiteDatabase
import android.graphics.Bitmap
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.BitmapImageViewTarget
import com.bumptech.glide.request.target.Target
import com.ecjtu.flesh.R
import com.ecjtu.flesh.model.models.MeiPaiModel

/**
 * Created by Ethan_Xiang on 2018/2/9.
 */
class MeiPaiCardListAdapter(pageModel: List<MeiPaiModel>, val recyclerView: RecyclerView) : VideoCardListAdapter(pageModel, recyclerView){
    private var mDatabase: SQLiteDatabase? = null

    private var mLastClickPosition = -1

    private val linearLayoutManager: LinearLayoutManager? = if (recyclerView.layoutManager is LinearLayoutManager) recyclerView.layoutManager as LinearLayoutManager? else null

    private var mIsInForeground = true

    private var mPlayViewHolder: VH? = null

    override fun getItemCount(): Int {
        return pageModel.size
    }

    override fun onBindViewHolder(holder: VH?, position: Int) {
        if (mLastClickPosition < linearLayoutManager?.findFirstVisibleItemPosition() ?: 0 ||
                mLastClickPosition > linearLayoutManager?.findLastVisibleItemPosition() ?: 0) {
            if (mLastClickPosition >= 0) {
                mPlayViewHolder?.ijkVideoView?.apply {
                    pause()
                    this.mediaController?.hide()
                    Log.i("VideoCardListAdapter", "pause 1 video position " + mLastClickPosition)
                }
            }
        }
        if (!mIsInForeground) {
            mPlayViewHolder?.ijkVideoView?.pause()
            holder?.ijkVideoView?.pause()
            holder?.thumb?.visibility = View.VISIBLE
            Log.i("VideoCardListAdapter", "pause video 2 position " + mLastClickPosition)
        }
        val context = holder?.itemView?.context
        val model = pageModel.get(position)
        holder?.textView?.text = model.title

        if (mLastClickPosition != position) {
            holder?.ijkVideoView?.pause()
            holder?.thumb?.visibility = View.VISIBLE
            Log.i("VideoCardListAdapter", "pause 3 video position " + mLastClickPosition)
        } else {
            holder?.thumb?.visibility = View.INVISIBLE
        }

        val videoUrl = model.videoUrl
        holder?.itemView?.setTag(R.id.extra_tag_2, videoUrl)
        holder?.itemView?.setTag(R.id.extra_tag_3, holder)
        holder?.itemView?.setOnClickListener(this)
        holder?.itemView?.setTag(R.id.extra_tag, position)

        val imageView = holder?.thumb
        val options = RequestOptions()
        options.centerCrop()
        val url = (pageModel.get(position) as MeiPaiModel).videoImageUrl /*thumb2OriginalUrl(pageModel.itemList[position].imgUrl)*/
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

            val bottom = parent.findViewById<View>(R.id.bottom)
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
        val isInSamePos = mLastClickPosition == position
        position?.let {
            val lastPos = mLastClickPosition
            mLastClickPosition = position
            if (!isInSamePos) {
                mPlayViewHolder?.ijkVideoView?.pause()
                Log.i("VideoCardListAdapter", "pause 5 video position " + lastPos)
            }
        }
        val videoUrl = v?.getTag(R.id.extra_tag_2) as String?
//        videoUrl?.let {
//            val videoView = v?.findViewById(R.id.ijk_video) as IjkVideoView
//            val thumb = v.findViewById(R.id.thumb) as ImageView?
//            (videoView.mediaController as SimpleMediaController?)?.updatePausePlay()
//            if (videoView.isPlaying) {
//                thumb?.visibility = View.INVISIBLE
//                return@let
//            }
//            mPlayViewHolder = v.getTag(R.id.extra_tag_3) as VH?
//            thumb?.visibility = View.INVISIBLE
//            if (isInSamePos && videoView.isInPlaybackState) {
//                videoView.start()
//                Log.i("VideoCardListAdapter", "start 1 video position " + mLastClickPosition)
//            } else {
//                videoView.setVideoPath(videoUrl)
//                videoView.start()
//                Log.i("VideoCardListAdapter", "start 2 video position " + mLastClickPosition)
//            }
//            (videoView.mediaController as SimpleMediaController?)?.updatePausePlay()
//        }
    }

    override fun setHeight(position: Int, height: Int) {
        super.setHeight(position, height)
    }

    override fun onRelease() {
        mDatabase?.close()
        mPlayViewHolder?.ijkVideoView?.apply {
            release(true)
            Log.i("VideoCardListAdapter", "release video position " + mLastClickPosition)
        }
    }

    override fun onResume() {
        mIsInForeground = true
        if (mLastClickPosition >= 0) {
            notifyItemChanged(mLastClickPosition)
        }
        mLastClickPosition = -1
    }

    override fun onStop() {
        mIsInForeground = false
        notifyDataSetChanged()
    }

    override fun onDestroy() {
        onRelease()
    }

    override fun onSelectTab() {
    }

    override fun onUnSelectTab() {
        onRelease()
    }
}