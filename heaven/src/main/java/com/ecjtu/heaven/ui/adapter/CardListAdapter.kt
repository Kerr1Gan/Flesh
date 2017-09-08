package com.ecjtu.heaven.ui.adapter

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.BitmapImageViewTarget
import com.bumptech.glide.request.target.Target
import com.ecjtu.heaven.R
import com.ecjtu.netcore.model.PageModel

/**
 * Created by Ethan_Xiang on 2017/9/8.
 */
class CardListAdapter(var pageModel: PageModel) : RecyclerView.Adapter<CardListAdapter.VH>(), RequestListener<Bitmap> {

    override fun getItemCount(): Int {
        return pageModel.itemList.size
    }

    override fun onBindViewHolder(holder: VH?, position: Int) {
        val context = holder?.itemView?.context
        val params = holder?.itemView?.layoutParams
        params?.height = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,300f,context?.resources?.displayMetrics).toInt()

        val imageView = holder?.mImageView
        val options = RequestOptions()
        options.centerCrop()
        val url = thumb2OriginalUrl(pageModel.itemList[position].imgUrl)

        url.let {
            Glide.with(context).asBitmap().load(url).listener(this).apply(options).into(imageView)
        }
    }

    override fun onViewDetachedFromWindow(holder: VH?) {
        super.onViewDetachedFromWindow(holder)
        val context = holder?.itemView?.context
        val params = holder?.itemView?.layoutParams
        params?.height = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,300f,context?.resources?.displayMetrics).toInt()
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): VH {
        val v = LayoutInflater.from(parent?.context).inflate(R.layout.layout_card_view, parent, false)
        return VH(v)
    }

    override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Bitmap>?, isFirstResource: Boolean): Boolean {
        return false
    }

    override fun onResourceReady(resource: Bitmap?, model: Any?, target: Target<Bitmap>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
        if (target is BitmapImageViewTarget) {
            var layoutParams = (target.view.parent as View).layoutParams
            layoutParams.height = resource?.height ?:LinearLayout.LayoutParams.WRAP_CONTENT
            target.view.setImageBitmap(resource)
        }
        return true
    }

    private fun thumb2OriginalUrl(url: String): String? {
        try {
            var localUrl = url.replace("/thumbs", "")
            var suffix = localUrl.substring(localUrl.lastIndexOf("/"))
            suffix = suffix.substring(suffix.indexOf("_") + 1)
            var end = suffix.substring(suffix.lastIndexOf("."))
            suffix = suffix.substring(0, suffix.lastIndexOf("_"))
            suffix += end
            return localUrl.substring(0, localUrl.lastIndexOf("/") + 1) + suffix
        } catch (ex: Exception) {
            return null
        }
    }

    override fun getItemViewType(position: Int): Int {
        return super.getItemViewType(position)
    }

    class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mImageView = itemView.findViewById<ImageView?>(R.id.image)

        init {
            mImageView?.adjustViewBounds = true
        }


    }
}