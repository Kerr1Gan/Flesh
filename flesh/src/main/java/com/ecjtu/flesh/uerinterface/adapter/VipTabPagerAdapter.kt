package com.ecjtu.flesh.uerinterface.adapter

import android.content.Context
import android.preference.PreferenceManager
import android.support.v4.view.ViewPager
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.amazonaws.Protocol
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.Bucket
import com.amazonaws.services.s3.model.ObjectListing
import com.ecjtu.flesh.Constants
import com.ecjtu.flesh.R
import com.ecjtu.flesh.db.DatabaseManager
import com.ecjtu.flesh.db.table.impl.ClassPageTableImpl
import com.ecjtu.flesh.model.models.VideoModel
import com.ecjtu.netcore.model.MenuModel
import com.ecjtu.netcore.model.PageModel
import kotlin.concurrent.thread

/**
 * Created by Ethan_Xiang on 2018/2/22.
 */
class VipTabPagerAdapter(menu: List<MenuModel>, viewPager: ViewPager) : VideoTabPagerAdapter(menu, viewPager) {
    companion object {
        const val S3_URL_FORMAT = "%s://${Constants.S3_URL}/%s/%s"
        const val S3_IMAGE_FORMAT = "%s_image_%s.png"
        const val S3_IMAGE_BUCKET = "fleshbucketimage"
    }

    private val KEY_LAST_POSITION = "vip_last_position_"
    private val KEY_LAST_POSITION_OFFSET = "vip_last_position_offset_"

    private val mViewStub = HashMap<String, VH>()

    private var mBuckets: List<Bucket>? = null
    private var mS3: AmazonS3Client? = null
    private var mProtocol: Protocol? = null

    override fun instantiateItem(container: ViewGroup?, position: Int): Any {
        val item = LayoutInflater.from(container?.context).inflate(R.layout.layout_list_card_view, container, false)
        container?.addView(item)
        val title = getPageTitle(position) as String
        val vh = VH(item, menu[position], title)
        thread {
            try {
                val listing = mS3?.listObjects(mBuckets?.get(position)?.name)
                container?.post {
                    if (listing != null) {
                        vh.load(listing)
                    }
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
            }

        }
        mViewStub.put(getPageTitle(position).toString(), vh)
        return item
    }

    private inner class VH(itemView: View, menu: MenuModel, key: String) : VideoTabPagerAdapter.VH(itemView, menu, key) {
        private var mObjectListing: ObjectListing? = null
        private val innerMenu = menu
        fun load(objListing: ObjectListing) {
            mObjectListing = objListing
            loadCache(itemView.context, key)
        }

        fun getPageModel(): ObjectListing? {
            return mObjectListing
        }

        private fun loadCache(context: Context, key: String) {
            if (mObjectListing != null) {
                val summary = mObjectListing?.objectSummaries
                if (summary != null) {
                    val v33List = arrayListOf<VideoModel>()
                    for (s3Obj in summary) {
                        val v33 = VideoModel()
                        v33.title = s3Obj.key
                        if (mObjectListing != null) {
                            v33.videoUrl = mObjectListing?.bucketName + "@,@" + s3Obj.key
                            v33.imageUrl = getImageUrlByS3(s3Obj.key, mObjectListing?.bucketName ?: "")
                        }
                        v33List.add(v33)
                    }
                    val impl = ClassPageTableImpl()
                    val db = DatabaseManager.getInstance(context)?.getDatabase()
                    val itemListModel = arrayListOf<PageModel.ItemModel>()
                    for (videoModel in v33List) {
                        val model = PageModel.ItemModel(videoModel.videoUrl, videoModel.title, videoModel.imageUrl, 1)
                        itemListModel.add(model)
                    }
                    val pageModel = PageModel(itemListModel)
                    pageModel.nextPage = ""
                    db?.let {
                        db.beginTransaction()
                        impl.addPage(db, pageModel)
                        db.setTransactionSuccessful()
                        db.endTransaction()
                    }
                    db?.close()

                    var index = 0
                    for (model in this@VipTabPagerAdapter.menu) {
                        if (model.title.equals(innerMenu.title)) {
                            break
                        }
                        index++
                    }

                    recyclerView?.adapter = VipCardListAdapter(v33List, recyclerView!!, mS3!!, mBuckets!![index])
                    val lastPosition = PreferenceManager.getDefaultSharedPreferences(context).getInt(getLastPositionKey() + key, -1)
                    if (lastPosition >= 0) {
                        val yOffset = PreferenceManager.getDefaultSharedPreferences(context).getInt(getLastPositionOffsetKey() + key, 0)
                        (recyclerView.layoutManager as LinearLayoutManager).scrollToPositionWithOffset(lastPosition, yOffset)
                    }
                }
            }
        }

        override fun getSize(): Int {
            return mObjectListing?.objectSummaries?.size ?: 0
        }
    }

    fun setBucketsList(buckets: List<Bucket>, s3: AmazonS3Client, protocal: Protocol) {
        mBuckets = buckets
        mS3 = s3
        mProtocol = protocal
    }

    override fun getLastPositionKey(): String {
        return KEY_LAST_POSITION
    }

    override fun getLastPositionOffsetKey(): String {
        return KEY_LAST_POSITION_OFFSET
    }

    override fun getViewStub(): HashMap<String, out VideoTabPagerAdapter.VH>? {
        return mViewStub
    }

    override fun getViewStub(position: Int): View? {
        return mViewStub.get(menu[position].title)?.recyclerView
    }

    override fun getListSize(position: Int): Int {
        return mViewStub.get(menu[position].title)?.getSize() ?: 0
    }

    private fun getImageUrlByS3(title: String, bucketName: String): String {
        return String.format(S3_URL_FORMAT, mProtocol.toString(), S3_IMAGE_BUCKET, String.format(S3_IMAGE_FORMAT, bucketName, title))
    }
}