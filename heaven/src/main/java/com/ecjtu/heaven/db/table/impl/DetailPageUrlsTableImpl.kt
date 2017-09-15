package com.ecjtu.heaven.db.table.impl

import android.database.sqlite.SQLiteDatabase

/**
 * Created by Ethan_Xiang on 2017/9/15.
 */
class DetailPageUrlsTableImpl:BaseTableImpl(){
    override val sql: String
        get() = "CREATE TABLE tb_image_url_with_detail_page_id (\n" +
                "    _id       INTEGER PRIMARY KEY,\n" +
                "    page_id   INTEGER REFERENCES tb_detail_page (_id) ON DELETE CASCADE\n" +
                "                                                      ON UPDATE CASCADE,\n" +
                "    image_url STRING\n" +
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