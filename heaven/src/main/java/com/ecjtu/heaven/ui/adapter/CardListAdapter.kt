package com.ecjtu.heaven.ui.adapter

import android.graphics.Bitmap
import android.support.v7.widget.RecyclerView
import android.util.TypedValue
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
import com.ecjtu.heaven.R
import com.ecjtu.heaven.ui.activity.PageDetailActivity
import com.ecjtu.netcore.jsoup.PageSoup
import com.ecjtu.netcore.jsoup.SoupFactory
import com.ecjtu.netcore.model.PageModel
import com.ecjtu.netcore.network.AsyncNetwork
import com.ecjtu.netcore.network.IRequestCallback
import java.net.HttpURLConnection

/**
 * Created by Ethan_Xiang on 2017/9/8.
 */
class CardListAdapter(var pageModel: PageModel) : RecyclerView.Adapter<CardListAdapter.VH>(), RequestListener<Bitmap>, View.OnClickListener {

    private val mListHeight = ArrayList<Int>()

    override fun getItemCount(): Int {
        return pageModel.itemList.size
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
        val url = pageModel.itemList[position].imgUrl /*thumb2OriginalUrl(pageModel.itemList[position].imgUrl)*/
        val builder = LazyHeaders.Builder().addHeader("User-Agent", "Mozilla/5.0 (Linux; Android 5.1.1; Nexus 6 Build/LYZ28E) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.90 Mobile Safari/537.36")
                .addHeader("Accept", "image/webp,image/apng,image/*,*/*;q=0.8")
                .addHeader("Accept-Encoding", "gzip, deflate")
                .addHeader("Accept-Language", "zh-CN,zh;q=0.8")
                .addHeader("Host", "i.meizitu.net")
                .addHeader("Proxy-Connection", "keep-alive")
                .addHeader("Referer", "http://m.mzitu.com/")
        val glideUrl = GlideUrl(url, builder.build())
        url.let {
            imageView?.setTag(R.id.extra_tag, position)
            Glide.with(context).asBitmap().load(glideUrl).listener(this).apply(options).into(imageView)
            holder?.mTextView?.setText(pageModel.itemList[position].description)
        }

        if (position == itemCount - 1) {
            val request = AsyncNetwork()
            request.request(pageModel.nextPage,null)
            request.setRequestCallback(object : IRequestCallback {
                override fun onSuccess(httpURLConnection: HttpURLConnection?, response: String) {
                    val values = SoupFactory.parseHtml(PageSoup::class.java, response)
                    if (values != null) {
                        val soups = values[PageSoup::class.java.simpleName] as PageModel
                        pageModel.itemList.addAll(soups.itemList)
                        pageModel.nextPage = soups.nextPage
                        holder?.itemView?.post {
                            if (position + 1 < itemCount) {
                                notifyItemChanged(position + 1)
                            }
                        }
                    }
                }
            })
        }
        holder?.itemView?.setTag(R.id.extra_tag, position)
        holder?.itemView?.setOnClickListener(this)
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
            val parent = target.view.parent?.parent
            val layoutParams = (parent as View).layoutParams
            var height = resource?.height ?: LinearLayout.LayoutParams.WRAP_CONTENT
            height += parent.findViewById(R.id.title).height
            if (layoutParams.height != height) {
                layoutParams.height = height
            }
            val position = target.view.getTag(R.id.extra_tag) as Int
            target.view.setImageBitmap(resource)
            setHeight(position, height)
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
        val position = v?.getTag(R.id.extra_tag)
        position?.let {
            val url = pageModel.itemList[position as Int].href
            val intent = PageDetailActivity.newInstance(v.context, url)
            v.context.startActivity(intent)
        }
    }

    private fun getHeight(position: Int): Int {
        if (position >= mListHeight.size || position < 0) {
            return 0
        }
        return mListHeight[position]
    }

    private fun setHeight(position: Int, height: Int) {
        if (position >= mListHeight.size) {
            val diff = position - mListHeight.size + 1
            mListHeight.addAll(Array<Int>(diff, { 0 }))
        }
        mListHeight.set(position, height)
    }



    class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mImageView = itemView.findViewById(R.id.image) as ImageView
        val mTextView = itemView.findViewById(R.id.title) as TextView

        init {
            mImageView?.adjustViewBounds = true
        }
    }
}