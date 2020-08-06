package com.ecjtu.flesh.userinterface.adapter

import android.database.sqlite.SQLiteDatabase
import android.graphics.Bitmap
import android.text.TextUtils
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
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
import com.ecjtu.flesh.db.DatabaseManager
import com.ecjtu.flesh.db.table.impl.ClassPageTableImpl
import com.ecjtu.flesh.db.table.impl.HistoryTableImpl
import com.ecjtu.flesh.db.table.impl.LikeTableImpl
import com.ecjtu.flesh.userinterface.activity.FullScreenImageActivity
import com.ecjtu.flesh.userinterface.activity.PageDetailActivity
import com.ecjtu.netcore.jsoup.SoupFactory
import com.ecjtu.netcore.jsoup.impl.PageSoup
import com.ecjtu.netcore.model.PageModel
import com.ecjtu.netcore.network.AsyncNetwork
import com.ecjtu.netcore.network.IRequestCallback
import java.net.HttpURLConnection

/**
 * Created by Ethan_Xiang on 2017/9/8.
 */
open class CardListAdapter(var pageModel: PageModel) : RecyclerViewWrapAdapter<CardListAdapter.VH>(), RequestListener<Bitmap>, View.OnClickListener, IChangeTab {

    private var mDatabase: SQLiteDatabase? = null

    private var mLastClickPosition = -1

    override fun getItemCount(): Int {
        return pageModel.itemList.size
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val context = holder?.itemView?.context
        val params = holder?.itemView?.layoutParams
        if (getHeight(position) != 0) {
            params?.height = getHeight(position)/*mLastHeight*/ // 防止上滑时 出现跳动的情况
        } else {
            val cache = pageModel.itemList[position].height
            if (cache != 0) {
                params?.height = cache
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
        }
        Log.i("CardListAdapter", "onBindViewHolder position = $position ,height = ${params?.height}")

        //set empty
        holder?.itemView?.findViewById<View>(R.id.bottom)?.visibility = View.INVISIBLE

        //db
        if (mDatabase == null) {
            val manager = DatabaseManager.getInstance(context)
            mDatabase = manager?.getDatabase()
        }

        val href = pageModel.itemList[position].href
        if (mDatabase != null && mDatabase?.isOpen == true) {
            val impl = LikeTableImpl()
            holder?.heart?.isActivated = impl.isLike(mDatabase!!, href)
        }

        val imageView = holder?.imageView
        val options = RequestOptions()
        options.centerCrop()
        val url = pageModel.itemList[position].imgUrl /*thumb2OriginalUrl(pageModel.itemList[position].imgUrl)*/
        var host = ""
        if (url.startsWith("https://")) {
            host = url.replace("https://", "")
        } else if (url.startsWith("http://")) {
            host = url.replace("http://", "")
        }
        if (host.indexOf("/") >= 0) {
            host = host.substring(0, host.indexOf("/"))
        }
        val builder = LazyHeaders.Builder()
                .addHeader("User-Agent", "Mozilla/5.0 (Linux; Android 5.1.1; Nexus 6 Build/LYZ28E)  Chrome/60.0.3112.90 Mobile Safari/537.36")
                .addHeader("Accept", "image/webp,image/apng,image/*,*/*;q=0.8")
                .addHeader("Accept-Encoding", "gzip, deflate")
                .addHeader("Accept-Language", "zh-CN,zh;q=0.8")
                .addHeader("Host", host)
                .addHeader("Proxy-Connection", "keep-alive")
                .addHeader("Referer", "http://m.mzitu.com/")

        if (!TextUtils.isEmpty(url)) {
            val glideUrl = GlideUrl(url, builder.build())
            url.let {
                imageView?.setTag(R.id.extra_tag, position)
                Glide.with(context).asBitmap().load(glideUrl).listener(this).apply(options).into(imageView)
                holder?.textView?.setText(pageModel.itemList[position].description)
            }
        } else {
            Glide.with(context).asBitmap().load(R.drawable.shape_drawable_white).listener(this).apply(options).into(imageView)
            holder?.textView?.setText(pageModel.itemList[position].description)
//            holder?.imageView?.setImageDrawable(ColorDrawable(Color.WHITE))
            val bottom = holder?.itemView?.findViewById<View>(R.id.bottom)
            bottom?.visibility = View.VISIBLE
        }

        if (position == itemCount - 1) {
            val request = AsyncNetwork()
            if (!TextUtils.isEmpty(pageModel.nextPage)) {
                request.request(pageModel.nextPage, null)
            }
            request.setRequestCallback(object : IRequestCallback {
                override fun onSuccess(httpURLConnection: HttpURLConnection?, response: String) {
                    val values = SoupFactory.parseHtml(PageSoup::class.java, response)
                    if (values != null) {
                        val soups = values[PageSoup::class.java.simpleName] as PageModel
                        val list = pageModel.itemList
                        for (item in soups.itemList) {
                            val index = list.indexOf(item)
                            if (index < 0) {
                                list.add(item)
                            } else {
                                list.set(index, item)
                            }
                        }
                        pageModel.nextPage = soups.nextPage
                        holder?.itemView?.post {
                            if (position + 1 < itemCount) {
                                notifyItemChanged(position + 1)
                            }
                        }
                        val impl = ClassPageTableImpl()
                        val db = DatabaseManager.getInstance(holder?.itemView?.context)?.getDatabase()
                        db?.let {
                            db.beginTransaction()
                            impl.addPage(db, pageModel)
                            db.setTransactionSuccessful()
                            db.endTransaction()
                        }
                        db?.close()
                    }
                }
            })
        }
        holder?.itemView?.setTag(R.id.extra_tag, position)
        holder?.itemView?.setOnClickListener(this)
        holder?.heart?.setTag(R.id.extra_tag, pageModel.itemList[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent?.context).inflate(R.layout.layout_card_view, parent, false)
        return VH(v)
    }

    override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Bitmap>?, isFirstResource: Boolean): Boolean {
        if (target is BitmapImageViewTarget) {
            val parent = target.view.parent?.parent as View
            val bottom = parent.findViewById<View>(R.id.bottom)
            bottom.visibility = View.VISIBLE
        }
        return false
    }

    override fun onResourceReady(resource: Bitmap?, model: Any?, target: Target<Bitmap>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
        if (target is BitmapImageViewTarget) {
            val parent = target.view.parent?.parent
            val layoutParams = (parent as View).layoutParams
            var height = resource?.height ?: LinearLayout.LayoutParams.WRAP_CONTENT
            Log.i("CardListAdapter", "onResourceReady height = $height")
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
            val item = pageModel.itemList[position as Int]
            val url = item.href
            if (!TextUtils.isEmpty(url) && (url.startsWith("http://") || url.startsWith("https://"))) {
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
            setLastClickPosition(position)
        }
    }

    override fun setHeight(position: Int, height: Int) {
        super.setHeight(position, height)
        pageModel.itemList[position].height = height
    }

    open fun onRelease() {
        mDatabase?.close()
    }

    open fun onResume() {
        if (getLastClickPosition() >= 0) {
            notifyItemChanged(getLastClickPosition())
        }
        setLastClickPosition(-1)
    }

    open fun onStop() {

    }

    override fun onSelectTab() {
    }

    override fun onUnSelectTab() {
    }

    open fun getLastClickPosition(): Int {
        return mLastClickPosition
    }

    open fun setLastClickPosition(position: Int) {
        mLastClickPosition = position
    }

    class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView = itemView.findViewById<View>(R.id.image) as ImageView
        val textView = itemView.findViewById<View>(R.id.title) as TextView
        val heart = itemView.findViewById<View>(R.id.heart) as ImageView
        val description = itemView.findViewById<View>(R.id.description) as TextView

        init {
            imageView.adjustViewBounds = true
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