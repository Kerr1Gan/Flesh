package com.ecjtu.heaven.db.table.impl

import android.database.sqlite.SQLiteDatabase

/**
 * Created by KerriGan on 2017/9/16.
 */
class ClassPageListTableImpl:BaseTableImpl(){
    override val sql: String
        get() = "CREATE TABLE tb_class_page_list (\n" +
                "    _id           INTEGER PRIMARY KEY ASC AUTOINCREMENT,\n" +
                "    href          STRING  UNIQUE,\n" +
                "    description   STRING,\n" +
                "    image_url     STRING,\n" +
                "    id_class_page INTEGER REFERENCES tb_class_page (_id) ON DELETE CASCADE\n" +
                "                                                         ON UPDATE CASCADE\n" +
                ");"

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