package com.ecjtu.flesh.ui.fragment

import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.ecjtu.componentes.activity.ImmersiveFragmentActivity
import com.ecjtu.flesh.Constants
import com.ecjtu.flesh.R
import com.ecjtu.netcore.network.AsyncNetwork
import com.ecjtu.netcore.network.IRequestCallbackV2
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection

/**
 * Created by KerriGan on 2018/2/15.
 */
class VideoTabFragment : BaseTabPagerFragment() {

    companion object {
        private const val PREF_VIDEO_TAB_JSON = "pref_video_tab_json"
    }

    private var mRecyclerView: RecyclerView? = null
    private val mItemInfo = mutableListOf<ItemInfo>(
            /*ItemInfo("爱恋", arrayOf(), V33Fragment::class.java, ""),
            ItemInfo("OfO", arrayOf(), OfO91Fragment::class.java, ""),
            ItemInfo("Vip", arrayOf(), VipFragment::class.java, "")*/)

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_video_tab, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        if (getDelegate()?.getTabLayout() != null) {
            setTabLayout(getDelegate()?.getTabLayout()!!)
        }
        val tabJson = PreferenceManager.getDefaultSharedPreferences(context).getString(PREF_VIDEO_TAB_JSON, "")
        try {
            val jArray = JSONArray(tabJson)
            mItemInfo.clear()
            for (i in 0 until jArray.length()) {
                val jObj = jArray.get(i) as JSONObject
                val title = jObj.optString("title")
                val fragment = jObj.optString("fragment")
                val url = jObj.optString("url")
                val info = ItemInfo(title, arrayOf(), Class.forName(fragment), url)
                mItemInfo.add(info)
            }
            notifyRecyclerView()
        } catch (ex: Exception) {
        }
        AsyncNetwork().request(Constants.VIDEO_TAB_URL)
                .setRequestCallback(object : IRequestCallbackV2 {
                    override fun onSuccess(httpURLConnection: HttpURLConnection?, response: String) {
                        try {
                            val jArray = JSONArray(response)
                            mItemInfo.clear()
                            for (i in 0 until jArray.length()) {
                                val jObj = jArray.get(i) as JSONObject
                                val title = jObj.optString("title")
                                val fragment = jObj.optString("fragment")
                                val url = jObj.optString("url")
                                val info = ItemInfo(title, arrayOf(), Class.forName(fragment), url)
                                mItemInfo.add(info)
                            }
                            if (context != null) {
                                PreferenceManager.getDefaultSharedPreferences(context).edit()
                                        .putString(PREF_VIDEO_TAB_JSON, response).apply()
                            }
                        } catch (ex: Exception) {
                        }
                        getHandler().post {
                            notifyRecyclerView()
                        }
                    }

                    override fun onError(httpURLConnection: HttpURLConnection?, exception: Exception) {
                        getHandler().post {
                            notifyRecyclerView()
                        }
                    }
                })

    }

    private fun notifyRecyclerView() {
        mRecyclerView = view?.findViewById(R.id.recycler_view) as RecyclerView?
        mRecyclerView?.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        mRecyclerView?.adapter = SimpleAdapter()
    }

    override fun onUserVisibleHintChanged(isVisibleToUser: Boolean) {
        if (isVisibleToUser) {
            getTabLayout()?.removeAllTabs()
        }
    }

    inner class SimpleAdapter : RecyclerView.Adapter<VH>() {
        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): VH {
            return VH(LayoutInflater.from(context).inflate(R.layout.layout_video_fragment_item, parent, false))
        }

        override fun onBindViewHolder(holder: VH?, position: Int) {
            holder?.textView?.text = mItemInfo[position].title
            holder?.itemView?.setOnClickListener {
                val intent = ImmersiveFragmentActivity.newInstance(context, mItemInfo[position].clazz,
                        Bundle().apply {
                            putString("url", mItemInfo[position].url);
                            putString("title", mItemInfo[position].title)
                        })
                startActivity(intent)
            }
        }

        override fun getItemCount(): Int {
            return mItemInfo.size
        }

    }

    class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView: TextView = itemView.findViewById(R.id.text) as TextView

        init {

        }
    }

    data class ItemInfo(val title: String, val images: Array<String>, val clazz: Class<*>, val url: String)
}