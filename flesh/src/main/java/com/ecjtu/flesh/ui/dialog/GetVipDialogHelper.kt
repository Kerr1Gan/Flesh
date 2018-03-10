package com.ecjtu.flesh.ui.dialog

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Handler
import android.view.View
import android.widget.TextView
import com.ecjtu.flesh.R
import com.ecjtu.flesh.ui.activity.PayPalActivity
import kotlin.concurrent.thread

/**
 * Created by xiang on 2018/3/9.
 */
class GetVipDialogHelper(context: Context) : BaseDialogHelper(context) {

    val API_URI = "/api/getUserById?&userId=%s"

    private val mHandler = Handler()

    override fun init() {
        super.init()
        getBuilder()?.setTitle("获取Vip")
                ?.setMessage("正在获取Vip信息...")
                ?.setView(R.layout.layout_progress)
                ?.setNegativeButton("不了", null)
                ?.setPositiveButton("去支付", object : DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface?, which: Int) {
                        val intent = Intent(getContext(), PayPalActivity::class.java)
                        getContext().startActivity(intent)
                    }
                })

        setDialog(getBuilder()?.create())
        getDialog()?.setOnShowListener {
            getDialog()?.getButton(DialogInterface.BUTTON_NEGATIVE)?.visibility = View.GONE
            getDialog()?.getButton(DialogInterface.BUTTON_POSITIVE)?.visibility = View.GONE
        }
        getDialog()?.setOnCancelListener {
            mHandler.removeMessages(0, null)
        }
        thread {
            Thread.sleep(2000)
            mHandler.post {
                val messageView = getDialog()?.findViewById(android.R.id.message) as TextView?
                messageView?.setText("通过PayPal支付5刀即可获得1月Vip。")
                messageView?.visibility = View.VISIBLE

                getDialog()?.findViewById(R.id.progress_bar)?.visibility = View.GONE
                getDialog()?.getButton(DialogInterface.BUTTON_NEGATIVE)?.visibility = View.VISIBLE
                getDialog()?.getButton(DialogInterface.BUTTON_POSITIVE)?.visibility = View.VISIBLE
            }
        }
    }
}