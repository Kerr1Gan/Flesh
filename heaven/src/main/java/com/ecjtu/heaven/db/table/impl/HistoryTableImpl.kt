package com.ecjtu.heaven.db.table.impl

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Ethan_Xiang on 2017/9/15.
 */
class HistoryTableImpl : BaseTableImpl() {
    override val sql: String
        get() = "CREATE TABLE tb_history (\n" +
                "    _id                  INTEGER PRIMARY KEY ASC AUTOINCREMENT,\n" +
                "    time                 STRING,\n" +
                "    href_class_page_list STRING  REFERENCES tb_class_page_list (href) ON DELETE CASCADE\n" +
                "                                                                      ON UPDATE CASCADE\n" +
                ");\n"

    companion object {
        const val TABLE_NAME = "tb_history"
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

    fun addHistory(sqLiteDatabase: SQLiteDatabase, href: String) {
        val value = ContentValues()
        value.put("href_class_page_list", href)
        val dateFormat = SimpleDateFormat("yyyy-MM-dd")
        value.put("time", dateFormat.format(Date()))
        sqLiteDatabase.insert(TABLE_NAME, null, value)
    }

    fun deleteHistory(sqLiteDatabase: SQLiteDatabase, href: String) {
        sqLiteDatabase.delete(TABLE_NAME, "href_class_page_list=?", arrayOf(href))
    }

    fun deleteAllHistory(sqLiteDatabase: SQLiteDatabase) {
        sqLiteDatabase.delete(TABLE_NAME, null, null)
    }

}