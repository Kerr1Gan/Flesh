package com.ecjtu.flesh.db.table.impl

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.ecjtu.netcore.model.PageModel
import java.lang.Exception

/**
 * Created by KerriGan on 2017/9/16.
 */
class ClassPageListTableImpl : BaseTableImpl() {
    override val sql: String
        get() = "CREATE TABLE tb_class_page_list (\n" +
                "    _id           INTEGER PRIMARY KEY ASC AUTOINCREMENT,\n" +
                "    href          STRING  UNIQUE,\n" +
                "    description   STRING,\n" +
                "    image_url     STRING,\n" +
                "    id_class_page INTEGER REFERENCES tb_class_page (_id) ON DELETE CASCADE\n" +
                "                                                         ON UPDATE CASCADE,\n" +
                "    [index]       INTEGER\n" +
                ");\n"

    companion object {
        const val TABLE_NAME = "tb_class_page_list"
    }

    override fun createTable(sqLiteDatabase: SQLiteDatabase) {
        sqLiteDatabase.execSQL(sql)
    }

    override fun deleteTable(sqLiteDatabase: SQLiteDatabase) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun updateTable(sqLiteDatabase: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (newVersion >= 11) {
            try {
                sqLiteDatabase.execSQL("alter table tb_class_page_list add type INTEGER DEFAULT (0)")
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
    }

    fun addPageList(sqLiteDatabase: SQLiteDatabase, pageModel: PageModel) {
        val itemList = pageModel.itemList
        var index = 0
        for (item in itemList) {
            val value = ContentValues()
            value.put("href", item.href)
            value.put("description", item.description)
            value.put("image_url", item.imgUrl)
            value.put("id_class_page", pageModel.id)
            value.put("[index]", index++)
            value.put("type", item.type)
            try {
                var id = sqLiteDatabase.update(TABLE_NAME, value, "href=?", arrayOf(item.href)) * 1L
                if (id <= 0) {
                    id = sqLiteDatabase.insertOrThrow(TABLE_NAME, null, value)
                    if (id.toInt() >= 0) {
                        item.id = id.toInt()
                    }
                }
            } catch (ex: Exception) {
            }

        }
    }

    /**
     *  @return an array. array[0] is next_page and array[1] is the last href
     */
    fun findNextPageAndLastHref(sqLiteDatabase: SQLiteDatabase, href: String): Array<String>? {
        var cursor: Cursor? = null
        try {
            var sql = "SELECT tb2.next_page FROM $TABLE_NAME tb1,${ClassPageTableImpl.TABLE_NAME} tb2 WHERE tb1.id_class_page = tb2._id AND tb1.href = \"$href\" ORDER BY id_class_page"
            val ret = arrayOf("", "")
            cursor = sqLiteDatabase.rawQuery(sql, arrayOf())
            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast) {
                    ret[0] = cursor.getString(0)
//                    cursor.moveToNext()
                    break
                }
            }
            cursor.close()
            sql = "SELECT tb1.href FROM $TABLE_NAME tb1,${ClassPageTableImpl.TABLE_NAME} tb2 WHERE tb1.id_class_page = tb2._id AND tb2.next_page = \"${ret[0]}\" ORDER BY [index]"
            cursor = sqLiteDatabase.rawQuery(sql, arrayOf())
            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast) {
                    ret[1] = cursor.getString(0)
                    cursor.moveToNext()
                }
            }
            cursor.close()
            return ret
        } catch (ex: Exception) {
            ex.printStackTrace()
            if (cursor != null) {
                cursor.close()
            }
            return null
        }
    }
}