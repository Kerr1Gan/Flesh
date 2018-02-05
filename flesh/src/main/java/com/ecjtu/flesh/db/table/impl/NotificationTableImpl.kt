package com.ecjtu.flesh.db.table.impl

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import com.ecjtu.flesh.model.ModelManager
import com.ecjtu.flesh.model.models.NotificationModel
import java.lang.Exception

/**
 * Created by Ethan_Xiang on 2017/9/22.
 */
class NotificationTableImpl : BaseTableImpl() {

    override val sql: String
        get() = "CREATE TABLE tb_notification (\n" +
                "    _id               INTEGER PRIMARY KEY ASC AUTOINCREMENT,\n" +
                "    title             STRING,\n" +
                "    content           STRING,\n" +
                "    ticker            STRING,\n" +
                "    [limit]           INTEGER,\n" +
                "    time              STRING,\n" +
                "    time_limit        STRING,\n" +
                "    action_detail_url STRING,\n" +
                "    occurs            INTEGER,\n" +
                "    h5_page           STRING\n" +
                ");\n"

    companion object {
        const val TABLE_NAME = "tb_notification"
    }

    override fun createTable(sqLiteDatabase: SQLiteDatabase) {
        sqLiteDatabase.execSQL(sql)
    }

    override fun deleteTable(sqLiteDatabase: SQLiteDatabase) {
    }

    override fun updateTable(sqLiteDatabase: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (newVersion == 4) {
            sqLiteDatabase.execSQL("PRAGMA foreign_keys = 0;\n" +
                    "\n" +
                    "CREATE TABLE sqlitestudio_temp_table AS SELECT *\n" +
                    "                                          FROM tb_notification;\n" +
                    "\n" +
                    "DROP TABLE tb_notification;\n" +
                    "\n" +
                    "CREATE TABLE tb_notification (\n" +
                    "    _id               INTEGER PRIMARY KEY ASC AUTOINCREMENT,\n" +
                    "    title             STRING,\n" +
                    "    content           STRING,\n" +
                    "    ticker            STRING,\n" +
                    "    [limit]           INTEGER,\n" +
                    "    time              STRING,\n" +
                    "    time_limit        STRING,\n" +
                    "    action_detail_url STRING,\n" +
                    "    occurs            INTEGER,\n" +
                    "    h5_page           STRING,\n" +
                    "    type     INTEGER,DEFAULT (0) \n" +
                    ");\n" +
                    "\n" +
                    "INSERT INTO tb_notification (\n" +
                    "                                _id,\n" +
                    "                                title,\n" +
                    "                                content,\n" +
                    "                                ticker,\n" +
                    "                                [limit],\n" +
                    "                                time,\n" +
                    "                                time_limit,\n" +
                    "                                action_detail_url,\n" +
                    "                                occurs,\n" +
                    "                                h5_page\n" +
                    "                            )\n" +
                    "                            SELECT _id,\n" +
                    "                                   title,\n" +
                    "                                   content,\n" +
                    "                                   ticker,\n" +
                    "                                   \"limit\",\n" +
                    "                                   time,\n" +
                    "                                   time_limit,\n" +
                    "                                   action_detail_url,\n" +
                    "                                   occurs,\n" +
                    "                                   h5_page\n" +
                    "                              FROM sqlitestudio_temp_table;\n" +
                    "\n" +
                    "DROP TABLE sqlitestudio_temp_table;\n" +
                    "\n" +
                    "PRAGMA foreign_keys = 1;")
        }
    }

    fun addNotification(sqLiteDatabase: SQLiteDatabase, model: NotificationModel) {
        val content = ContentValues()
        content.put("_id", model.id)
        content.put("title", model.title)
        content.put("content", model.content)
        content.put("ticker", model.ticker)
        content.put("[limit]", model.limit)
        content.put("time", model.time)
        content.put("time_limit", model.timeLimit)
        content.put("action_detail_url", model.actionDetailUrl)
        content.put("occurs", model.occurs)
        content.put("h5_page", model.h5Page)
        content.put("type", model.type)
        try {
            sqLiteDatabase.insertOrThrow(TABLE_NAME, null, content)
        } catch (ex: Exception) {
        }
    }

    fun getAllNotification(sqLiteDatabase: SQLiteDatabase): List<NotificationModel> {
        val ret = ArrayList<NotificationModel>()
        val cursor = sqLiteDatabase.rawQuery("SELECT * FROM $TABLE_NAME", arrayOf())
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast) {
                val model = ModelManager.getNotificationModel(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getString(3),
                        cursor.getInt(4),
                        cursor.getString(5), cursor.getString(6), cursor.getString(7), cursor.getString(9), cursor.getInt(10))
                model.occurs = cursor.getInt(8)
                ret.add(model)
                cursor.moveToNext()
            }
        }
        cursor.close()
        return ret
    }

    fun updateNotification(sqLiteDatabase: SQLiteDatabase, model: NotificationModel) {
        val content = ContentValues()
        content.put("_id", model.id)
        content.put("title", model.title)
        content.put("content", model.content)
        content.put("ticker", model.ticker)
        content.put("[limit]", model.limit)
        content.put("time", model.time)
        content.put("time_limit", model.timeLimit)
        content.put("action_detail_url", model.actionDetailUrl)
        content.put("occurs", model.occurs)
        content.put("h5_page", model.h5Page)
        content.put("type", model.type)
        sqLiteDatabase.update(TABLE_NAME, content, "_id=?", arrayOf(model.id.toString()))
    }
}