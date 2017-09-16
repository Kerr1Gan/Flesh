package com.ecjtu.heaven.db.table.impl

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
                "    _id            INTEGER PRIMARY KEY,\n" +
                "    id_detail_page INTEGER REFERENCES tb_detail_page (_id) ON DELETE CASCADE\n" +
                "                                                           ON UPDATE CASCADE,\n" +
                "    time           STRING\n" +
                ");\n"

    private val mTableName = "tb_like_v2"

    override fun createTable(sqLiteDatabase: SQLiteDatabase) {
        sqLiteDatabase.execSQL(sql)
    }

    override fun deleteTable(sqLiteDatabase: SQLiteDatabase) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun updateTable(sqLiteDatabase: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        createTable(sqLiteDatabase)
    }

    fun addLike(sqLiteDatabase: SQLiteDatabase, detailPageId: Int) {
        val contentValues = ContentValues()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd")
        contentValues.put("id_detail_page", detailPageId)
        contentValues.put("time", dateFormat.format(Date()))
        sqLiteDatabase.insert(mTableName, null, contentValues)
    }

    fun deleteLike(sqLiteDatabase: SQLiteDatabase, detailPageId: Int) {
        sqLiteDatabase.delete(mTableName, "id_detail_page=?", arrayOf(detailPageId.toString()))
    }

    fun isLike(sqLiteDatabase: SQLiteDatabase, detailPageId: Int): Boolean {
        var ret = false
        val cursor = sqLiteDatabase.rawQuery("SELECT page_url FROM $mTableName WHERE id_detail_page=?", arrayOf(detailPageId.toString()))
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast) {
//                val id = cursor.getString(0)
//                if (id == pageUrl) {
//                    ret = true
//                    break
//                }
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
