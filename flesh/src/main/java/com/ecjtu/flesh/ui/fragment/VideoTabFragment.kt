package com.ecjtu.flesh.ui.fragment

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.ecjtu.componentes.activity.ImmersiveFragmentActivity
import com.ecjtu.flesh.R

/**
 * Created by KerriGan on 2018/2/15.
 */
class VideoTabFragment : BaseTabPagerFragment() {

    private var mRecyclerView: RecyclerView? = null
    private val mItemInfo = arrayOf(ItemInfo("爱恋", arrayOf(), V33Fragment::class.java),
            ItemInfo("美拍", arrayOf(), MeiPaiFragment::class.java),
            ItemInfo("22CM", arrayOf(), String::class.java))

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_video_tab, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        if (getDelegate()?.getTabLayout() != null) {
            setTabLayout(getDelegate()?.getTabLayout()!!)
        }
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
                val intent = ImmersiveFragmentActivity.newInstance(context, mItemInfo[position].clazz)
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

    data class ItemInfo(val title: String, val images: Array<String>, val clazz: Class<*>)
}