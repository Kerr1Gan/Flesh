package com.ecjtu.flesh.userinterface.dialog

import android.app.Dialog
import android.content.Context
import androidx.appcompat.app.AlertDialog

/**
 * Created by xiang on 2018/3/9.
 */
class VipDialogHelper {

    private val mContext: Context

    private var mAlertDialogBuilder: AlertDialog.Builder? = null

    private var mDialog: Dialog? = null

    constructor(context: Context) {
        mContext = context
        init()
    }

    private fun init() {
        mAlertDialogBuilder = AlertDialog.Builder(mContext)
        mAlertDialogBuilder?.setTitle("Vip")

    }

    fun getDialog(): Dialog? = mDialog
}