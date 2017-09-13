package com.ecjtu.heaven.db.table.impl

import android.database.sqlite.SQLiteDatabase

/**
 * Created by Ethan_Xiang on 2017/9/13.
 */
class LikeTableImpl : BaseTableImpl() {
    override val sql: String
        get() = "CREATE TABLE IF NOT EXISTS tb_like (\n" +
                "    _id     INTEGER PRIMARY KEY,\n" +
                "    page_id         UNIQUE\n" +
                "                    NOT NULL\n" +
                ");"

    override fun createTable(sqLiteDatabase: SQLiteDatabase) {
        sqLiteDatabase.execSQL(sql)
    }

    override fun deleteTable(sqLiteDatabase: SQLiteDatabase) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun updateTable(sqLiteDatabase: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}