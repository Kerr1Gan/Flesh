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
                "    _id           INTEGER PRIMARY KEY ASC AUTOINCREMENT,\n" +
                "    time          STRING,\n" +
                "    id_class_page INTEGER REFERENCES tb_class_page (_id) ON DELETE CASCADE\n" +
                "                                                         ON UPDATE CASCADE\n" +
                ");"
    private val mTableName = "tb_history"

    override fun createTable(sqLiteDatabase: SQLiteDatabase) {
        sqLiteDatabase.execSQL(sql)
    }

    override fun deleteTable(sqLiteDatabase: SQLiteDatabase) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun updateTable(sqLiteDatabase: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        createTable(sqLiteDatabase)
    }

    fun addHistory(sqLiteDatabase: SQLiteDatabase, classPageId: Int) {
        val value = ContentValues()
        value.put("id_class_page", classPageId)
        val dateFormat = SimpleDateFormat("yyyy-MM-dd")
        value.put("time", dateFormat.format(Date()))
        sqLiteDatabase.insert(mTableName, null, value)
    }

    fun deleteHistory(sqLiteDatabase: SQLiteDatabase, classPageId: Int) {
        sqLiteDatabase.delete(mTableName, "id_class_page=?", arrayOf(classPageId.toString()))
    }

    fun deleteAllHistory(sqLiteDatabase: SQLiteDatabase) {
        sqLiteDatabase.delete(mTableName, null, null)
    }

}