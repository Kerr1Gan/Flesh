package com.ecjtu.heaven.ui.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.ecjtu.heaven.R
import com.ecjtu.heaven.db.DatabaseManager
import com.ecjtu.heaven.db.table.impl.HistoryTableImpl
import com.ecjtu.heaven.presenter.PageHistoryActivityDelegate

/**
 * Created by Ethan_Xiang on 2017/9/18.
 */
class PageHistoryActivity : AppCompatActivity() {
    private var mDelegate: PageHistoryActivityDelegate? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_page_like)
        val db = DatabaseManager.getInstance(this)?.getDatabase()
        if (db != null) {
            val impl = HistoryTableImpl()
            val list = impl.getAllHistory(db)
            db.close()
            mDelegate = PageHistoryActivityDelegate(this, list)
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        mDelegate?.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        mDelegate?.onRelease()
    }
}
