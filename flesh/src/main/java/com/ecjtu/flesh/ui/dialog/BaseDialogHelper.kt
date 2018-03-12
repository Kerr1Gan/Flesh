package com.ecjtu.flesh.ui.dialog

import android.content.Context
import android.os.Handler
import android.support.v7.app.AlertDialog

/**
 * Created by xiang on 2018/3/9.
 */
abstract class BaseDialogHelper {
    private val mContext: Context

    private var mAlertDialogBuilder: AlertDialog.Builder? = null

    private var mDialog: AlertDialog? = null

    private val mHandler: Handler = Handler()

    constructor(context: Context) {
        mContext = context
        init()
    }

    open protected fun init() {
        mAlertDialogBuilder = AlertDialog.Builder(mContext)
    }

    open protected fun initLater() {
    }

    fun getDialog(): AlertDialog? = mDialog

    fun getContext(): Context = mContext

    fun getBuilder(): AlertDialog.Builder? = mAlertDialogBuilder

    fun setDialog(dialog: AlertDialog?) {
        mDialog = dialog
    }

    fun getHandler(): Handler {
        return mHandler
    }
}