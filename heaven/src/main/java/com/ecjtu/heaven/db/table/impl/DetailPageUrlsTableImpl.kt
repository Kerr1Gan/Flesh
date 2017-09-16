package com.ecjtu.heaven.db.table.impl

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase

/**
 * Created by Ethan_Xiang on 2017/9/15.
 */
class DetailPageUrlsTableImpl : BaseTableImpl() {
    override val sql: String
        get() = "CREATE TABLE tb_detail_page_image_list (\n" +
                "    _id       INTEGER PRIMARY KEY,\n" +
                "    page_id   INTEGER REFERENCES tb_detail_page (_id) ON DELETE CASCADE\n" +
                "                                                      ON UPDATE CASCADE,\n" +
                "    image_url STRING\n" +
                ");\n"

    private val mTableName = "tb_image_url_with_detail_page_id"

    override fun createTable(sqLiteDatabase: SQLiteDatabase) {
        sqLiteDatabase.execSQL(sql)
    }

    override fun deleteTable(sqLiteDatabase: SQLiteDatabase) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun updateTable(sqLiteDatabase: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        createTable(sqLiteDatabase)
    }

    fun addPageUrls(sqLiteDatabase: SQLiteDatabase, pageId: Int, imageUrl: String) {
        val value = ContentValues()
        value.put("page_id", pageId)
        value.put("image_url", imageUrl)
        sqLiteDatabase.insert(mTableName, null, value)
    }

    fun deletePageUrls(sqLiteDatabase: SQLiteDatabase, pageId: Int) {
        sqLiteDatabase.delete(mTableName, "page_id=?", arrayOf(pageId.toString()))
    }
}