package com.ecjtu.flesh.ui.adapter

import android.content.Context
import android.preference.PreferenceManager
import android.support.v4.view.ViewPager
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.Bucket
import com.amazonaws.services.s3.model.ObjectListing
import com.ecjtu.flesh.R
import com.ecjtu.flesh.model.models.V33Model
import com.ecjtu.netcore.model.MenuModel
import kotlin.concurrent.thread

/**
 * Created by Ethan_Xiang on 2018/2/22.
 */
class VipTabPagerAdapter(menu: List<MenuModel>, viewPager: ViewPager) : VideoTabPagerAdapter(menu, viewPager) {
    private val KEY_LAST_POSITION = "vip_last_position_"
    private val KEY_LAST_POSITION_OFFSET = "vip_last_position_offset_"

    private val mViewStub = HashMap<String, VH>()

    private var mBuckets: List<Bucket>? = null
    private var mS3: AmazonS3Client? = null
    override fun instantiateItem(container: ViewGroup?, position: Int): Any {
        val item = LayoutInflater.from(container?.context).inflate(R.layout.layout_list_card_view, container, false)
        container?.addView(item)
        val title = getPageTitle(position) as String
        val vh = VH(item, menu[position], title)
        thread {
            val listing = mS3?.listObjects(mBuckets?.get(position)?.name)
            container?.post {
                if (listing != null) {
                    vh.load(listing)
                }
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
                    val v33List = arrayListOf<V33Model>()
                    for (s3Obj in summary) {
                        val v33 = V33Model()
                        v33.title = s3Obj.key
                        v33.videoUrl = ""
                        v33.imageUrl = ""
                        v33List.add(v33)
                    }
                    recyclerView?.adapter = VipCardListAdapter(v33List, recyclerView!!, mS3!!, mBuckets!![menu.indexOf(this.innerMenu)])
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

    fun setBucketsList(buckets: List<Bucket>, s3: AmazonS3Client) {
        mBuckets = buckets
        mS3 = s3
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
}