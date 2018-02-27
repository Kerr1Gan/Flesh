package com.ecjtu.flesh.ui.adapter

import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import com.ecjtu.componentes.activity.RotateNoCreateActivity
import com.ecjtu.flesh.R
import com.ecjtu.flesh.db.DatabaseManager
import com.ecjtu.flesh.db.table.impl.HistoryTableImpl
import com.ecjtu.flesh.ui.activity.FullScreenImageActivity
import com.ecjtu.flesh.ui.activity.PageDetailActivity
import com.ecjtu.flesh.ui.fragment.IjkVideoFragment
import com.ecjtu.netcore.model.PageModel
import java.text.SimpleDateFormat

/**
 * Created by Ethan_Xiang on 2017/9/19.
 */
class HistoryCardListAdapter(pageModel: PageModel) : CardListAdapter(pageModel) {

    private val mDateFormat1 = SimpleDateFormat("yyyy-MM-dd")
    private val mDateFormat2 = SimpleDateFormat("yyyy年M月d日")

    override fun onBindViewHolder(holder: VH?, position: Int) {
        super.onBindViewHolder(holder, position)
        val db = DatabaseManager.getInstance(holder?.itemView?.context)?.getDatabase()
        val impl = HistoryTableImpl()
        var time = ""
        if (db != null) {
            time = impl.getHistoryTime(db, pageModel.itemList[position].href)
        }
        db?.close()

        if (!TextUtils.isEmpty(time)) {
            val date = mDateFormat1.parse(time)
            holder?.description?.text = mDateFormat2.format(date)
        }
    }

    override fun onClick(v: View?) {
        val position = v?.getTag(R.id.extra_tag)
        position?.let {
            val item = pageModel.itemList[position as Int]
            val url = item.href
            if (item.type == 0) {
                if (!TextUtils.isEmpty(url) && url.startsWith("http://")) {
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
            } else {
                val db = DatabaseManager.getInstance(v.context)?.getDatabase() as SQLiteDatabase
                val impl = HistoryTableImpl()
                impl.addHistory(db, url.toString())
                db.close()
                val intent = RotateNoCreateActivity.newInstance(v.context, IjkVideoFragment::class.java
                        , Bundle().apply { putString(IjkVideoFragment.EXTRA_URI_PATH, url.toString()) })
                v.context.startActivity(intent)
            }
            setLastClickPosition(position)
        }
    }
}