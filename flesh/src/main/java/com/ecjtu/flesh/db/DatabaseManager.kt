package com.ecjtu.flesh.db

import android.content.Context
import android.database.DatabaseErrorHandler
import android.database.sqlite.SQLiteDatabase
import com.ecjtu.flesh.db.table.BaseTable
import com.ecjtu.flesh.db.table.impl.BaseTableImpl

/**
 * Created by Ethan_Xiang on 2017/8/15.
 */
class DatabaseManager(context: Context? = null) {

    private var mContext: Context? = null

    private var mDatabaseHelper: DataBaseHelper? = null

    init {
        if (context != null) {
            mContext = context
        }
    }

    @JvmOverloads
    fun getHelper(context: Context, name: String, version: Int = 1, factory: SQLiteDatabase.CursorFactory? = null,
                  errorHandler: DatabaseErrorHandler? = null): DataBaseHelper? {
        mDatabaseHelper = DataBaseHelper(context, name, factory, if (version >= 1) version else 0, errorHandler)
        mDatabaseHelper?.setTables(mTableList)
        return mDatabaseHelper
    }

    fun getDatabase(): SQLiteDatabase? {
        return getDatabase(2)
    }

    fun getDatabase(version: Int): SQLiteDatabase? {
        if (mContext != null) {
            return getHelper(mContext!!, "heaven", version)?.writableDatabase
        }
        return null
    }

    fun <T : BaseTableImpl> registerTable(obj: T) {
        if (getTables().indexOf(obj) < 0) {
            (getTables() as MutableList<BaseTable>).add(obj)
        }
    }

    fun getTables(): List<BaseTable> {
        return mTableList
    }

    companion object {
        private val mTableList = ArrayList<BaseTableImpl>()

        @JvmStatic
        fun getInstance(context: Context? = null): DatabaseManager? {
            return DatabaseManager(context)
        }

        //C
        @JvmStatic
        fun <T : BaseTable> create(dataBaseHelper: DataBaseHelper, obj: T) {
        }

        //R
        @JvmStatic
        fun <T : BaseTable> getById(dataBaseHelper: DataBaseHelper, obj: T) {
        }


        //U
        @JvmStatic
        fun <T : BaseTable> update(dataBaseHelper: DataBaseHelper, obj: T) {
        }

        //D
        @JvmStatic
        fun <T : BaseTable> delete(dataBaseHelper: DataBaseHelper, obj: T) {
        }

    }
}