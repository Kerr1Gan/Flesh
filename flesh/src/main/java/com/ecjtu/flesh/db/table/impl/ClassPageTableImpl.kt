package com.ecjtu.flesh.db.table.impl

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.text.TextUtils
import com.ecjtu.netcore.model.PageModel
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Ethan_Xiang on 2017/9/15.
 */
class ClassPageTableImpl : BaseTableImpl() {
    override val sql: String = "CREATE TABLE tb_class_page (\n" +
            "    _id       INTEGER PRIMARY KEY ASC AUTOINCREMENT,\n" +
            "    next_page STRING  UNIQUE,\n" +
            "    time      STRING\n" +
            ");\n"

    companion object {
        const val TABLE_NAME = "tb_class_page"
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

    fun addPage(sqLiteDatabase: SQLiteDatabase, pageModel: PageModel) {
        val value = ContentValues()
        val format = SimpleDateFormat("yyyy-MM-dd")
        if (TextUtils.isEmpty(pageModel.nextPage)) {
            pageModel.nextPage = ""
        }
        value.put("next_page", pageModel.nextPage)
        value.put("time", format.format(Date()))
        var id = 0L
        try {
            id = sqLiteDatabase.update(TABLE_NAME, value, "next_page=?", arrayOf(pageModel.nextPage)) * 1L
            if (id <= 0) {
                id = sqLiteDatabase.insertOrThrow(TABLE_NAME, null, value)
            }
            if (id.toInt() > 0) {
                pageModel.id = id.toInt()
            }
        } catch (ex: Exception) {
        }
        if (pageModel.id <= 0) {
            pageModel.id = getIdByNextPage(sqLiteDatabase, pageModel.nextPage)
        }
        val pageListTable = ClassPageListTableImpl()
        pageListTable.addPageList(sqLiteDatabase, pageModel)
    }

    fun deletePage(sqLiteDatabase: SQLiteDatabase, id: Int) {
        sqLiteDatabase.delete(TABLE_NAME, "_id=?", arrayOf(id.toString()))
    }

    fun getIdByNextPage(sqLiteDatabase: SQLiteDatabase, nextPage: String): Int {
        var ret = 1
        val cursor = sqLiteDatabase.rawQuery("SELECT _id FROM $TABLE_NAME WHERE next_page=\"$nextPage\"", arrayOf())
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast) {
                ret = cursor.getInt(0)
                cursor.moveToNext()
            }
        }
        cursor.close()
        return ret
    }
}