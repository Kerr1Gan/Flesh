package com.ecjtu.flesh.db.table.impl

import com.ecjtu.flesh.db.table.BaseTable

/**
 * Created by Ethan_Xiang on 2017/8/15.
 */
abstract class BaseTableImpl : BaseTable {

    override fun equals(other: Any?): Boolean {
        if (other !is BaseTableImpl) {
            return false
        }
        return this.sql == other.sql
    }

    override fun hashCode(): Int {
        return this.sql.hashCode()
    }
}