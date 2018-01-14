package com.ecjtu.flesh.db.table.impl

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import com.ecjtu.netcore.model.PageDetailModel
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Ethan_Xiang on 2017/9/15.
 */
class DetailPageTableImpl : BaseTableImpl() {
    override val sql: String
        get() = "CREATE TABLE tb_detail_page (\n" +
                "    _id      INTEGER PRIMARY KEY ASC AUTOINCREMENT,\n" +
                "    base_url STRING  UNIQUE,\n" +
                "    max_len  INTEGER,\n" +
                "    img_url  STRING,\n" +
                "    time     STRING\n" +
                ");"

    companion object {
        const val TABLE_NAME = "tb_detail_page"
    }

    override fun createTable(sqLiteDatabase: SQLiteDatabase) {
        sqLiteDatabase.execSQL(sql)
    }

    override fun deleteTable(sqLiteDatabase: SQLiteDatabase) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun updateTable(sqLiteDatabase: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
//        createTable(sqLiteDatabase)
        if (newVersion == 2) {
            sqLiteDatabase.execSQL("PRAGMA foreign_keys = 0;\n" +
                    "\n" +
                    "CREATE TABLE sqlitestudio_temp_table AS SELECT *\n" +
                    "                                          FROM tb_detail_page;\n" +
                    "\n" +
                    "DROP TABLE tb_detail_page;\n" +
                    "\n" +
                    "CREATE TABLE tb_detail_page (\n" +
                    "    _id      INTEGER PRIMARY KEY ASC AUTOINCREMENT,\n" +
                    "    base_url STRING  UNIQUE,\n" +
                    "    max_len  INTEGER,\n" +
                    "    img_url  STRING,\n" +
                    "    time     STRING,\n" +
                    "    type     INTEGER,DEFAULT (0) \n" +
                    ");\n" +
                    "\n" +
                    "INSERT INTO tb_detail_page (\n" +
                    "                               _id,\n" +
                    "                               base_url,\n" +
                    "                               max_len,\n" +
                    "                               img_url,\n" +
                    "                               time\n" +
                    "                           )\n" +
                    "                           SELECT _id,\n" +
                    "                                  base_url,\n" +
                    "                                  max_len,\n" +
                    "                                  img_url,\n" +
                    "                                  time\n" +
                    "                             FROM sqlitestudio_temp_table;\n" +
                    "\n" +
                    "DROP TABLE sqlitestudio_temp_table;\n" +
                    "\n" +
                    "PRAGMA foreign_keys = 1;")
        }
    }

    fun addDetailPage(sqLiteDatabase: SQLiteDatabase, pageDetailModel: PageDetailModel) {
        val value = ContentValues()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        value.put("base_url", pageDetailModel.baseUrl)
        value.put("max_len", pageDetailModel.maxLen)
        value.put("img_url", pageDetailModel.imgUrl)
        value.put("time", dateFormat.format(Date()))
        value.put("type",pageDetailModel.type)
        try {
            var id = sqLiteDatabase.update(TABLE_NAME, value, "base_url=?", arrayOf(pageDetailModel.baseUrl)) * 1L
            if (id <= 0) {
                id = sqLiteDatabase.insertOrThrow(TABLE_NAME, null, value)
            }
            if (id >= 0) {
                pageDetailModel.id = id.toInt()
                val pageUrlsImpl = DetailPageUrlsTableImpl()
                pageUrlsImpl.addPageUrls(sqLiteDatabase, id.toInt(), pageDetailModel.backupImgUrl)
            }
        } catch (ex: Exception) {
        }
    }

    fun deleteDetailPage(sqLiteDatabase: SQLiteDatabase, href: String) {
        sqLiteDatabase.delete(TABLE_NAME, "base_url=?", arrayOf(href))
    }

}