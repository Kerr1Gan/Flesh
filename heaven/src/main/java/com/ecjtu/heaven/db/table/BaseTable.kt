package com.ecjtu.heaven.db.table

import android.database.sqlite.SQLiteDatabase

/**
 * Created by Ethan_Xiang on 2017/8/14.
 */
interface BaseTable {
    val _id: String
        get() = "_id"
    val sql : String
    fun createTable(sqLiteDatabase: SQLiteDatabase)
    fun deleteTable(sqLiteDatabase: SQLiteDatabase)
    fun updateTable(sqLiteDatabase: SQLiteDatabase, oldVersion: Int, newVersion: Int)
}