package com.ecjtu.flesh.db.table.impl

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import com.ecjtu.netcore.model.PageModel

/**
 * Created by Ethan_Xiang on 2017/9/13.
 */
class LikeTableImpl : BaseTableImpl() {
    override val sql: String
        get() = "CREATE TABLE tb_like (\n" +
                "    _id         INTEGER PRIMARY KEY,\n" +
                "    page_url    STRING  UNIQUE\n" +
                "                        NOT NULL,\n" +
                "    href        STRING,\n" +
                "    description STRING,\n" +
                "    img_url     STRING\n" +
                ");\n"

    private val mTableName = "tb_like"

    override fun createTable(sqLiteDatabase: SQLiteDatabase) {
        sqLiteDatabase.execSQL(sql)
    }

    override fun deleteTable(sqLiteDatabase: SQLiteDatabase) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun updateTable(sqLiteDatabase: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
    }

    fun addLike(sqLiteDatabase: SQLiteDatabase, pageUrl: String, href: String, description: String, imgUrl: String) {
        val contentValues = ContentValues()
        contentValues.put("page_url", pageUrl)
        contentValues.put("href", href)
        contentValues.put("description", description)
        contentValues.put("img_url", imgUrl)
        sqLiteDatabase.insert(mTableName, null, contentValues)
    }

    fun deleteLike(sqLiteDatabase: SQLiteDatabase, pageUrl: String) {
        sqLiteDatabase.delete(mTableName, "page_url=?", arrayOf(pageUrl))
    }

    fun isLike(sqLiteDatabase: SQLiteDatabase, pageUrl: String): Boolean {
        var ret = false
        val cursor = sqLiteDatabase.rawQuery("SELECT page_url FROM $mTableName WHERE page_url=?", arrayOf(pageUrl))
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast) {
                val id = cursor.getString(0)
                if (id == pageUrl) {
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
        val cursor = sqLiteDatabase.rawQuery("SELECT * FROM $mTableName", arrayOf())
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast) {
                val model = PageModel.ItemModel(cursor.getString(2), cursor.getString(3), cursor.getString(4))
                ret.add(model)
                cursor.moveToNext()
            }
        }
        cursor.close()
        return ret
    }

}