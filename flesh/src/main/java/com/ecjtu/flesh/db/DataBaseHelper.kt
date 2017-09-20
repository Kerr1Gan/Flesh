package com.ecjtu.flesh.db

import android.content.Context
import android.database.DatabaseErrorHandler
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.ecjtu.flesh.db.table.BaseTable

/**
 * Created by Ethan_Xiang on 2017/8/14.
 */
class DataBaseHelper : SQLiteOpenHelper {
    companion object {
        private const val TAG = "DataBaseHelper"
    }

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
        Log.e(TAG, "onCreate db table size " + mTables?.size)
        if (mTables != null) {
            for (table in mTables!!) {
                Log.e(TAG, "create db " + table::class.java)
                table.createTable(db!!)
            }
        }
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        Log.e(TAG, "onUpgrade db table size " + mTables?.size)
        if (mTables != null) {
            for (table in mTables!!) {
                Log.e(TAG, "upgrade db " + table::class.java)
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