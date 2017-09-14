package com.ecjtu.heaven.db

import android.content.Context
import android.database.DatabaseErrorHandler
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.ecjtu.heaven.db.table.BaseTable

/**
 * Created by Ethan_Xiang on 2017/8/14.
 */
class DataBaseHelper : SQLiteOpenHelper {

    private var mTables: List<BaseTable>? = null

    constructor(context: Context, name: String, factory: SQLiteDatabase.CursorFactory?, version: Int) : super(context, name, factory, version) {
    }

    constructor(context: Context, name: String, factory: SQLiteDatabase.CursorFactory?, version: Int,
                errorHandler: DatabaseErrorHandler?) : super(context, name, factory, version, errorHandler) {
    }

    fun setTables(tables: List<BaseTable>) {
        mTables = tables
    }

    override fun onCreate(db: SQLiteDatabase?) {
        if (mTables != null) {
            for (table in mTables!!) {
                table.createTable(db!!)
            }
        }
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        if (mTables != null) {
            for (table in mTables!!) {
                table.updateTable(db!!, oldVersion, newVersion)
            }
        }
    }

    override fun onOpen(db: SQLiteDatabase?) {
        super.onOpen(db)
        if (db?.isReadOnly() == false) { // Enable foreign key constraints
            db.execSQL("PRAGMA foreign_keys=ON;");
        }
    }
}