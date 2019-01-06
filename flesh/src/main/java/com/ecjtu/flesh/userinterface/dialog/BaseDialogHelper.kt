package com.ecjtu.flesh.userinterface.dialog

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

    protected open fun init() {
        mAlertDialogBuilder = AlertDialog.Builder(mContext)
        setDialog(onCreateDialog())
        getDialog()?.setOnShowListener {
            onDialogShow(getDialog()!!)
        }
        getDialog()?.setOnCancelListener {
            onDialogCancel(getDialog()!!)
        }
        getHandler().post {
            initAsync()
        }
    }

    protected open fun initAsync() {
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

    protected abstract fun onCreateDialog(): AlertDialog?

    protected open fun onDialogShow(dialog: AlertDialog) {

    }

    protected open fun onDialogCancel(dialog: AlertDialog) {

    }
}