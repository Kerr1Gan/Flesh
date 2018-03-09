package com.ecjtu.flesh.ui.dialog

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import com.ecjtu.flesh.ui.activity.PayPalActivity

/**
 * Created by xiang on 2018/3/9.
 */
class GetVipDialogHelper(context: Context) : BaseDialogHelper(context) {

    override fun init() {
        super.init()
        getBuilder()?.setTitle("获取Vip")
                ?.setMessage("通过PayPal支付5刀即可获得1月Vip。")
                ?.setNegativeButton("不了", null)
                ?.setPositiveButton("去支付", object : DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface?, which: Int) {
                        val intent = Intent(getContext(), PayPalActivity::class.java)
                        getContext().startActivity(intent)
                    }
                })

        setDialog(getBuilder()?.create())
    }
}