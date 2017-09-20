package com.ecjtu.flesh.ui.adapter

import android.text.TextUtils
import com.ecjtu.flesh.db.DatabaseManager
import com.ecjtu.flesh.db.table.impl.HistoryTableImpl
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
}