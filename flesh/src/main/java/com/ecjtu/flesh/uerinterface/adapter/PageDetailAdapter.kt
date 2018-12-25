package com.ecjtu.flesh.uerinterface.adapter

import android.content.Intent
import android.graphics.Bitmap
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.BitmapImageViewTarget
import com.bumptech.glide.request.target.Target
import com.ecjtu.flesh.R
import com.ecjtu.flesh.uerinterface.activity.FullScreenImageActivity
import com.ecjtu.netcore.jsoup.SoupFactory
import com.ecjtu.netcore.jsoup.impl.PageDetailSoup
import com.ecjtu.netcore.model.PageDetailModel
import com.ecjtu.netcore.network.AsyncNetwork
import com.ecjtu.netcore.network.IRequestCallback
import java.lang.Exception
import java.net.HttpURLConnection

/**
 * Created by Ethan_Xiang on 2017/9/11.
 */
class PageDetailAdapter(var pageModel: PageDetailModel) : RecyclerViewWrapAdapter<PageDetailAdapter.VH>(), RequestListener<Bitmap>, View.OnClickListener {

    override fun getItemCount(): Int {
        return pageModel.maxLen
    }

    override fun onBindViewHolder(holder: VH?, position: Int) {
        val context = holder?.itemView?.context
        val params = holder?.itemView?.layoutParams
        if (getHeight(position) != 0) {
            params?.height = getHeight(position)/*mLastHeight*/ // 防止上滑时 出现跳动的情况
        } else {
            val next = getHeight(position + 1)
            val last = getHeight(position - 1)
            if (next != 0) {
                params?.height = next
            } else if (last != 0) {
                params?.height = last
            } else {
                params?.height = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 300f, context?.resources?.displayMetrics).toInt()
            }
        }

        val imageView = holder?.mImageView
        val options = RequestOptions()
        options.centerCrop()
        var temp = pageModel.backupImgUrl[position]
        var url: String? = null
        url = if (!TextUtils.isEmpty(temp)) {
            temp
        } else {
            String.format(pageModel.imgUrl, position + 1)
        }
        var host = url?.replace("http://", "")
        host = host?.substring(0, host.indexOf("/"))
        val builder = LazyHeaders.Builder().addHeader("User-Agent", "Mozilla/5.0 (Linux; Android 5.1.1; Nexus 6 Build/LYZ28E) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.90 Mobile Safari/537.36")
                .addHeader("Accept", "image/webp,image/apng,image/*,*/*;q=0.8")
                .addHeader("Accept-Encoding", "gzip, deflate")
                .addHeader("Accept-Language", "zh-CN,zh;q=0.8")
                .addHeader("Host", host)
                .addHeader("Proxy-Connection", "keep-alive")
                .addHeader("Referer", "http://m.mzitu.com/")
                .addHeader("Base-Url", pageModel.baseUrl)
                .addHeader("Base-Position", position.toString())
        val glideUrl = GlideUrl(url, builder.build())
        url.let {
            imageView?.setTag(R.id.extra_tag, position)
            holder?.itemView?.setOnClickListener(this)
            Glide.with(context).asBitmap().load(glideUrl).listener(this).apply(options).into(imageView)
        }

        holder?.itemView?.setTag(R.id.extra_tag, position)
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): VH {
        val v = LayoutInflater.from(parent?.context).inflate(R.layout.layout_card_view, parent, false)
        return VH(v)
    }

    override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Bitmap>?, isFirstResource: Boolean): Boolean {
        val request = AsyncNetwork()
        val header = model as GlideUrl

        var base: String? = header.headers["Base-Url"] ?: return false
        val pos = header.headers["Base-Position"] ?: return false
        val url = base + "/" + (pos.toInt() + 1)
        request.request(url, null)
        request.setRequestCallback(object : IRequestCallback {
            override fun onSuccess(httpURLConnection: HttpURLConnection?, response: String) {
                val ret = SoupFactory.parseHtml(PageDetailSoup::class.java, response, url)
                val imgUrl = ret.get("origin_img") as String?
                if (imgUrl != null) {
                    pageModel.backupImgUrl.set(pos.toInt(), imgUrl)
                    if (target is BitmapImageViewTarget) {
                        target.view.post {
                            val builder = LazyHeaders.Builder().addHeader("User-Agent", "Mozilla/5.0 (Linux; Android 5.1.1; Nexus 6 Build/LYZ28E) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.90 Mobile Safari/537.36")
                                    .addHeader("Accept", "image/webp,image/apng,image/*,*/*;q=0.8")
                                    .addHeader("Accept-Encoding", "gzip, deflate")
                                    .addHeader("Accept-Language", "zh-CN,zh;q=0.8")
                                    .addHeader("Host", "i.meizitu.net")
                                    .addHeader("Proxy-Connection", "keep-alive")
                                    .addHeader("Referer", "http://m.mzitu.com/")
                            val glideUrl = GlideUrl(imgUrl, builder.build())
                            try {
                                Glide.with(target.view.context).asBitmap().load(glideUrl).
                                        apply(RequestOptions().apply { centerCrop() }).
                                        listener(this@PageDetailAdapter).
                                        into(target.view)
                            } catch (ex: Exception) {
                                // java.lang.IllegalArgumentException You cannot start a load for a destroyed activity #56
                                ex.printStackTrace()
                            }
                        }
                    }
                }
            }
        })
        return true
    }

    override fun onResourceReady(resource: Bitmap?, model: Any?, target: Target<Bitmap>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
        if (target is BitmapImageViewTarget) {
            val parent = target.view.parent?.parent
            val layoutParams = (parent as View).layoutParams
            var height = resource?.height ?: LinearLayout.LayoutParams.WRAP_CONTENT
            height += parent.findViewById<View>(R.id.title).height
            if (layoutParams.height != height) {
                layoutParams.height = height
            }
            val position = target.view.getTag(R.id.extra_tag) as Int
            target.view.setImageBitmap(resource)
            setHeight(position, height)
        }
        return true
    }

    override fun getItemViewType(position: Int): Int {
        return 0
    }

    override fun onClick(v: View?) {
        val position = v?.getTag(R.id.extra_tag)
        if (position != null) {
            var intent: Intent? = null
            if (!TextUtils.isEmpty(pageModel.backupImgUrl.get(position as Int))) {
                intent = FullScreenImageActivity.newInstance(v.context, pageModel.backupImgUrl[position])
            } else {
                intent = FullScreenImageActivity.newInstance(v.context, String.format(pageModel.imgUrl, position as Int + 1))
            }
            v.context.startActivity(intent)
        }
    }


    class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mImageView = itemView.findViewById<View>(R.id.image) as ImageView
        val mBottom = itemView.findViewById<View>(R.id.bottom) as View

        init {
            mImageView.adjustViewBounds = true
            mBottom.visibility = View.GONE
        }
    }
}