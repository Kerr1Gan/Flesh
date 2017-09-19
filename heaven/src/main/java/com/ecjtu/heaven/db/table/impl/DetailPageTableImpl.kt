package com.ecjtu.heaven.db.table.impl

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import com.ecjtu.netcore.model.PageDetailModel
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Ethan_Xiang on 2017/9/15.
 */
class DetailPageTableImpl : BaseTableImpl() {
    override val sql: String
        get() = "CREATE TABLE tb_detail_page (\n" +
                "    _id      INTEGER PRIMARY KEY ASC AUTOINCREMENT,\n" +
                "    base_url STRING  UNIQUE,\n" +
                "    max_len  INTEGER,\n" +
                "    img_url  STRING,\n" +
                "    time     STRING\n" +
                ");"

    companion object {
        const val TABLE_NAME = "tb_detail_page"
    }

    override fun createTable(sqLiteDatabase: SQLiteDatabase) {
        sqLiteDatabase.execSQL(sql)
    }

    override fun deleteTable(sqLiteDatabase: SQLiteDatabase) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun updateTable(sqLiteDatabase: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        createTable(sqLiteDatabase)
    }

    fun addDetailPage(sqLiteDatabase: SQLiteDatabase, pageDetailModel: PageDetailModel) {
        val value = ContentValues()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        value.put("base_url", pageDetailModel.baseUrl)
        value.put("max_len", pageDetailModel.maxLen)
        value.put("img_url", pageDetailModel.imgUrl)
        value.put("time", dateFormat.format(Date()))
        val id = sqLiteDatabase.insert(TABLE_NAME, null, value)
        if (id >= 0) {
            pageDetailModel.id = id.toInt()
            val pageUrlsImpl = DetailPageUrlsTableImpl()
            pageUrlsImpl.addPageUrls(sqLiteDatabase, id.toInt(), pageDetailModel.backupImgUrl)
        }
    }

    fun deleteDetailPage(sqLiteDatabase: SQLiteDatabase, href: String) {
        sqLiteDatabase.delete(TABLE_NAME, "base_url=?", arrayOf(href))
    }

}