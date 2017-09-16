package com.ecjtu.heaven.db.table.impl

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import com.ecjtu.netcore.model.PageModel

/**
 * Created by Ethan_Xiang on 2017/9/15.
 */
class ClassPageTableImpl :BaseTableImpl(){
    override val sql: String = "CREATE TABLE tb_class_page (\n" +
            "    _id         INTEGER PRIMARY KEY ASC AUTOINCREMENT,\n" +
            "    next_page   STRING,\n" +
            "    href        STRING  UNIQUE,\n" +
            "    description STRING,\n" +
            "    img_url     STRING,\n" +
            "    time        STRING\n" +
            ");"

    private val mTableName = "tb_class_page"
    override fun createTable(sqLiteDatabase: SQLiteDatabase) {
        sqLiteDatabase.execSQL(sql)
    }

    override fun deleteTable(sqLiteDatabase: SQLiteDatabase) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun updateTable(sqLiteDatabase: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        createTable(sqLiteDatabase)
    }

    fun addPage(sqLiteDatabase: SQLiteDatabase,pageModel: PageModel){
        val value = ContentValues()
        value.put("next_page",pageModel.nextPage)
        sqLiteDatabase.insert(mTableName,null,null)
    }
}