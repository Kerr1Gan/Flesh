package com.ecjtu.flesh.db.table.impl

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.text.TextUtils
import java.lang.Exception

/**
 * Created by Ethan_Xiang on 2017/9/15.
 */
class DetailPageUrlsTableImpl : BaseTableImpl() {
    override val sql: String
        get() = "CREATE TABLE tb_detail_page_list (\n" +
                "    _id            INTEGER PRIMARY KEY ASC AUTOINCREMENT,\n" +
                "    image_url      STRING,\n" +
                "    [index]        INTEGER,\n" +
                "    id_detail_page INTEGER REFERENCES tb_detail_page (_id) ON DELETE CASCADE\n" +
                "                                                           ON UPDATE CASCADE\n" +
                ");\n"

    companion object {
        const val TABLE_NAME = "tb_detail_page_list"
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

    fun addPageUrls(sqLiteDatabase: SQLiteDatabase, pageId: Int, imageUrl: List<String>) {
        for (item in imageUrl) {
            if (!TextUtils.isEmpty(item)) {
                val value = ContentValues()
                value.put("id_detail_page", pageId)
                value.put("image_url", item)
                value.put("[index]", imageUrl.indexOf(item))
                try {
                    val id = sqLiteDatabase.update(TABLE_NAME, value, "image_url=?", arrayOf(item))
                    if (id <= 0) {
                        sqLiteDatabase.insertOrThrow(TABLE_NAME, null, value)
                    }
                } catch (ex: Exception) {
                }
            }
        }
    }

    fun deletePageUrls(sqLiteDatabase: SQLiteDatabase, pageId: Int) {
        sqLiteDatabase.delete(TABLE_NAME, "id_detail_page=?", arrayOf(pageId.toString()))
    }
}