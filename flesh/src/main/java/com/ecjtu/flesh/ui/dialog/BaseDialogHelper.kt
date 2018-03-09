package com.ecjtu.flesh.ui.dialog

import android.app.Dialog
import android.content.Context
import android.support.v7.app.AlertDialog

/**
 * Created by xiang on 2018/3/9.
 */
abstract class BaseDialogHelper {
    private val mContext: Context

    private var mAlertDialogBuilder: AlertDialog.Builder? = null

    private var mDialog: Dialog? = null

    constructor(context: Context) {
        mContext = context
        init()
    }

    open protected fun init() {
        mAlertDialogBuilder = AlertDialog.Builder(mContext)
    }

    fun getDialog(): Dialog? = mDialog

    fun getContext(): Context = mContext

    fun getBuilder(): AlertDialog.Builder? = mAlertDialogBuilder

    fun setDialog(dialog: Dialog?) {
        mDialog = dialog
    }
}