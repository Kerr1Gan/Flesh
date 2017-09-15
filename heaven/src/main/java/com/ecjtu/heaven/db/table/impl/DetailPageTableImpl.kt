package com.ecjtu.heaven.db.table.impl

import android.database.sqlite.SQLiteDatabase

/**
 * Created by Ethan_Xiang on 2017/9/15.
 */
class DetailPageTableImpl:BaseTableImpl(){
    override val sql: String
        get() = "CREATE TABLE tb_detail_page (\n" +
                "    _id      INTEGER PRIMARY KEY ASC AUTOINCREMENT,\n" +
                "    base_url STRING  UNIQUE,\n" +
                "    max_len  INTEGER,\n" +
                "    img_url  STRING,\n" +
                "    time     STRING\n" +
                ");\n"

    override fun createTable(sqLiteDatabase: SQLiteDatabase) {
        sqLiteDatabase.execSQL(sql)
    }

    override fun deleteTable(sqLiteDatabase: SQLiteDatabase) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun updateTable(sqLiteDatabase: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        createTable(sqLiteDatabase)

    }

}