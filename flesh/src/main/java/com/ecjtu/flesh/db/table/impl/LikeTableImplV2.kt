package com.ecjtu.flesh.db.table.impl

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import com.ecjtu.netcore.model.PageModel
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Ethan_Xiang on 2017/9/15.
 */
class LikeTableImplV2 : BaseTableImpl() {
    override val sql: String
        get() = "CREATE TABLE tb_like_v2 (\n" +
                "    _id                  INTEGER PRIMARY KEY,\n" +
                "    href_class_page_list STRING  REFERENCES tb_class_page_list (href) ON DELETE CASCADE\n" +
                "                                                                      ON UPDATE CASCADE,\n" +
                "    time                 STRING\n" +
                ");\n"

    companion object {
        const val TABLE_NAME = "tb_like_v2"
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

    fun addLike(sqLiteDatabase: SQLiteDatabase, href: String) {
        val contentValues = ContentValues()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd")
        contentValues.put("href_class_page_list", href)
        contentValues.put("time", dateFormat.format(Date()))
        sqLiteDatabase.insert(TABLE_NAME, null, contentValues)

    }

    fun deleteLike(sqLiteDatabase: SQLiteDatabase, href: String) {
        sqLiteDatabase.delete(TABLE_NAME, "href_class_page_list=?", arrayOf(href))
    }

    fun isLike(sqLiteDatabase: SQLiteDatabase, href: String): Boolean {
        var ret = false
        val cursor = sqLiteDatabase.rawQuery("SELECT $TABLE_NAME.href_class_page_list FROM $TABLE_NAME,${ClassPageListTableImpl.TABLE_NAME} WHERE $TABLE_NAME.href_class_page_list=${ClassPageListTableImpl.TABLE_NAME}.href and ${ClassPageListTableImpl.TABLE_NAME}.href=?", arrayOf(href))
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast) {
                val id = cursor.getString(0)
                if (id == href) {
                    ret = true
                    break
                }
                cursor.moveToNext()
            }
        }
        cursor.close()
        return ret
    }

    fun getAllLikes(sqLiteDatabase: SQLiteDatabase): List<PageModel.ItemModel> {
        val ret = ArrayList<PageModel.ItemModel>()
        val cursor = sqLiteDatabase.rawQuery("SELECT tb2.href,tb2.description,tb2.image_url FROM $TABLE_NAME tb1,${ClassPageListTableImpl.TABLE_NAME} tb2 WHERE tb1.href_class_page_list = tb2.href", arrayOf())
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

}
