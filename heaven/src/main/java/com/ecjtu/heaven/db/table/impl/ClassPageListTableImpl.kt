package com.ecjtu.heaven.db.table.impl

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import com.ecjtu.netcore.model.PageModel

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
                "                                                         ON UPDATE CASCADE\n" +
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
        createTable(sqLiteDatabase)
    }

    fun addPageList(sqLiteDatabase: SQLiteDatabase, pageModel: PageModel) {
        val itemList = pageModel.itemList
        for (item in itemList) {
            val value = ContentValues()
            value.put("href", item.href)
            value.put("description", item.description)
            value.put("image_url", item.imgUrl)
            value.put("id_class_page", pageModel.id)
            val id = sqLiteDatabase.insert(TABLE_NAME, null, value)
            item.id = id.toInt()
        }
    }

    fun findNextPageAndLastHref(sqLiteDatabase: SQLiteDatabase, href: String): Array<String> {
        val sql = "SELECT tb2.next_page,tb1.href FROM $TABLE_NAME tb1,${ClassPageTableImpl.TABLE_NAME} tb2 WHERE tb1.id_class_page = tb2._id and tb1.href = \"$href\""
        var ret = arrayOf("","")
        val cursor = sqLiteDatabase.rawQuery(sql, arrayOf())
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast) {
                ret[0] = cursor.getString(0)
                ret[1] = cursor.getString(1)
                cursor.moveToNext()
            }
        }
        cursor.close()
        return ret
    }
}