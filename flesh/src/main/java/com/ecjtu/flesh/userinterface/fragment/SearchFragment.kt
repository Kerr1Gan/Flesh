package com.ecjtu.flesh.userinterface.fragment

import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import com.ecjtu.flesh.R
import com.ecjtu.flesh.db.DatabaseManager
import com.ecjtu.flesh.db.table.impl.ClassPageTableImpl
import com.ecjtu.flesh.userinterface.adapter.CardListAdapter
import com.ecjtu.netcore.Constants
import com.ecjtu.netcore.jsoup.SoupFactory
import com.ecjtu.netcore.jsoup.impl.PageSoup
import com.ecjtu.netcore.model.PageModel
import okhttp3.*
import java.io.IOException

/**
 * Created by Ethan_Xiang on 2018/4/17.
 */
class SearchFragment : Fragment() {

    private var mHandler: Handler = Handler()

    private var recyclerView: RecyclerView? = null
    private var mPageModel: PageModel? = null
    private var mRefreshLayout: SwipeRefreshLayout? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.layout_list_card_view, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = view?.findViewById<View>(R.id.recycler_view) as RecyclerView?
        recyclerView?.layoutManager = LinearLayoutManager(recyclerView?.context, LinearLayoutManager.VERTICAL, false)
        mRefreshLayout = if (view is SwipeRefreshLayout) view else null
        mRefreshLayout?.setColorSchemeColors(mRefreshLayout!!.context.resources.getColor(R.color.colorPrimary))
        mRefreshLayout?.isEnabled = false
        userVisibleHint = true

        val actionBar = (activity as AppCompatActivity).supportActionBar
        actionBar?.setTitle(R.string.search)
        actionBar?.setDisplayHomeAsUpEnabled(true)
        setHasOptionsMenu(true)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == android.R.id.home) {
            activity.finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        mHandler.post {
            onUserVisibleHintChanged(isVisibleToUser)
        }
    }

    open fun onUserVisibleHintChanged(isVisibleToUser: Boolean) {
        if (isVisibleToUser) {
            requestUrl()
        }
    }

    private fun requestUrl() {
        val url = arguments.get("url") as String?
        if (!TextUtils.isEmpty(url)) {
            val client = OkHttpClient()
            val request = Request.Builder()
                    .url(url)
                    .get()
                    .build()
            val call = client.newCall(request)
            call.enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    mRefreshLayout?.post {
                        mRefreshLayout?.isRefreshing = false
                    }
                }

                override fun onResponse(call: Call, response: Response) {
                    mRefreshLayout?.post {
                        mRefreshLayout?.isRefreshing = false
                    }
                    val values = SoupFactory.parseHtml(PageSoup::class.java, response.body()?.string())
                    if (values != null) {
                        val soups = values[PageSoup::class.java.simpleName] as PageModel

                        var needUpdate = false
                        if (mPageModel != null) {
                            val list = mPageModel!!.itemList
                            soups.itemList.reverse()
                            for (item in soups.itemList) {
                                val index = list.indexOf(item)
                                if (index < 0) {
                                    list.add(0, item)
                                    needUpdate = true
                                } else {
                                    list.set(index, item)
                                }
                            }
                        }
                        val finalNeedUpdate = needUpdate
                        recyclerView?.post {
                            if (mPageModel == null) {
                                recyclerView?.adapter = CardListAdapter(soups)
                                recyclerView?.adapter?.notifyDataSetChanged()
                                mPageModel = soups
                            } else {
                                (recyclerView?.adapter as CardListAdapter).pageModel = mPageModel!!
                                if (finalNeedUpdate) {
                                    recyclerView?.adapter?.notifyDataSetChanged()
                                }
                            }
                        }
                        val impl = ClassPageTableImpl()
                        val db = DatabaseManager.getInstance(mRefreshLayout?.context)?.getDatabase()
                        db?.let {
                            db.beginTransaction()
                            impl.addPage(db, if (mPageModel == null) soups else mPageModel!!)
                            db.setTransactionSuccessful()
                            db.endTransaction()
                        }
                        db?.close()
                    }
                    mRefreshLayout?.post {
                        mRefreshLayout?.isRefreshing = false
                    }
                }
            })
            mRefreshLayout?.isRefreshing = true
        } else {
            mRefreshLayout?.isRefreshing = false
        }
    }
}