package com.ecjtu.flesh.userinterface.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.ecjtu.flesh.R
import com.ecjtu.flesh.db.DatabaseManager
import com.ecjtu.flesh.db.table.impl.LikeTableImpl
import com.ecjtu.flesh.mvp.presenter.PageLikeFragmentDelegate

/**
 * Created by Ethan_Xiang on 2017/9/18.
 */
class PageLikeFragment : androidx.fragment.app.Fragment() {

    private var mDelegate: PageLikeFragmentDelegate? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_page_like, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val db = DatabaseManager.getInstance(activity)?.getDatabase()
        if (db != null) {
            val impl = LikeTableImpl()
            val list = impl.getAllLikes(db)
            db.close()
            mDelegate = PageLikeFragmentDelegate(activity!!, list)
        }
        val actionBar = (activity as AppCompatActivity).supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
        actionBar?.setTitle(R.string.collection)
        setHasOptionsMenu(true)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == android.R.id.home) {
            activity?.finish()
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