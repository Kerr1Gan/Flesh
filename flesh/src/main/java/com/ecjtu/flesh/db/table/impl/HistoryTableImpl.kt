package com.ecjtu.flesh.db.table.impl

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import com.ecjtu.netcore.model.PageModel
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

    fun getAllHistory(sqLiteDatabase: SQLiteDatabase): List<PageModel.ItemModel> {
        val ret = ArrayList<PageModel.ItemModel>()
        val cursor = sqLiteDatabase.rawQuery("SELECT tb2.href,tb2.description,tb2.image_url FROM $TABLE_NAME tb1,${ClassPageListTableImpl.TABLE_NAME} tb2 where tb1.href_class_page_list = tb2.href", arrayOf())
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast) {
                val model = PageModel.ItemModel(cursor.getString(0), cursor.getString(1), cursor.getString(2))
                ret.add(model)
                cursor.moveToNext()
            }
        }
        cursor.close()
        return ret
    }

    /**
     *  @return yyyy-MM-dd
     */
    fun getHistoryTime(sqLiteDatabase: SQLiteDatabase, href: String): String {
        var ret = ""
        val cursor = sqLiteDatabase.rawQuery("SELECT tb1.time FROM $TABLE_NAME tb1,${ClassPageListTableImpl.TABLE_NAME} tb2 WHERE tb1.href_class_page_list = tb2.href AND tb1.href_class_page_list = \"$href\"", arrayOf())
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast) {
                ret = cursor.getString(0)
                cursor.moveToNext()
            }
        }
        cursor.close()
        return ret
    }

    fun deleteHistory(sqLiteDatabase: SQLiteDatabase, href: String) {
        sqLiteDatabase.delete(TABLE_NAME, "href_class_page_list=?", arrayOf(href))
    }

    fun deleteAllHistory(sqLiteDatabase: SQLiteDatabase) {
        sqLiteDatabase.delete(TABLE_NAME, null, null)
    }

}