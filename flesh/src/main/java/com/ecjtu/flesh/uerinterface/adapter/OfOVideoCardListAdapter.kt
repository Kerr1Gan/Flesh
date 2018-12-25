package com.ecjtu.flesh.uerinterface.adapter

import android.database.sqlite.SQLiteDatabase
import android.support.v7.widget.RecyclerView
import android.view.View
import com.ecjtu.componentes.activity.RotateNoCreateActivity
import com.ecjtu.flesh.R
import com.ecjtu.flesh.db.DatabaseManager
import com.ecjtu.flesh.db.table.impl.HistoryTableImpl
import com.ecjtu.flesh.model.models.VideoModel
import com.ecjtu.flesh.uerinterface.fragment.WebViewFragment

/**
 * Created by Ethan_Xiang on 2018/3/27.
 */
class OfOVideoCardListAdapter(pageModel: List<VideoModel>, recyclerView: RecyclerView, url: String? = null) : VideoCardListAdapter(pageModel, recyclerView, url) {

    override fun onClick(v: View?) {
        val position = v?.getTag(R.id.extra_tag) as Int?

        val videoUrl = v?.getTag(R.id.extra_tag_2) as String?
        val context = v?.context
        if (videoUrl != null && context != null && position != null) {
            val db = DatabaseManager.getInstance(v.context)?.getDatabase() as SQLiteDatabase
            val impl = HistoryTableImpl()
            impl.addHistory(db, videoUrl)
            db.close()
            val intent = RotateNoCreateActivity.newInstance(context, WebViewFragment::class.java
                    , WebViewFragment.openUrl(pageModel.get(position).baseUrl))
            context.startActivity(intent)
        }
    }
}