package com.ecjtu.flesh.ui.dialog

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Handler
import android.telephony.TelephonyManager
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.ecjtu.flesh.Constants
import com.ecjtu.flesh.R
import com.ecjtu.flesh.ui.activity.PayPalActivity
import com.ecjtu.netcore.network.AsyncNetwork
import com.ecjtu.netcore.network.IRequestCallbackV2
import org.json.JSONObject
import java.net.HttpURLConnection

/**
 * Created by xiang on 2018/3/9.
 */
class GetVipDialogHelper(context: Context) : BaseDialogHelper(context) {

    companion object {
        const val API_URI = "/api/getUserById?&userId="
    }

    private var mHandler: Handler? = null

    override fun init() {
        super.init()
        mHandler = Handler()

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
            mHandler?.removeMessages(0, null)
        }
        val deviceId = (getContext().getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager?)?.deviceId
        AsyncNetwork().request(Constants.SERVER_URL + API_URI + deviceId)
                .setRequestCallback(object : IRequestCallbackV2 {
                    override fun onSuccess(httpURLConnection: HttpURLConnection?, response: String) {
                        try {
                            val jObj = JSONObject(response)
                            val code = (jObj.opt("code") as String).toInt()
                            if (code < 0) {
                                mHandler?.post {
                                    val messageView = getDialog()?.findViewById(android.R.id.message) as TextView?
                                    messageView?.setText("通过PayPal支付5刀即可获得1月Vip。")
                                    messageView?.visibility = View.VISIBLE

                                    getDialog()?.findViewById(R.id.progress_bar)?.visibility = View.GONE
                                    getDialog()?.getButton(DialogInterface.BUTTON_NEGATIVE)?.visibility = View.VISIBLE
                                    getDialog()?.getButton(DialogInterface.BUTTON_POSITIVE)?.visibility = View.VISIBLE
                                }
                            } else {
                                mHandler?.post {
                                    val messageView = getDialog()?.findViewById(android.R.id.message) as TextView?
                                    messageView?.setText("您的Vip信息：\n" + "key:123456789")
                                    messageView?.visibility = View.VISIBLE
                                    getDialog()?.getButton(DialogInterface.BUTTON_POSITIVE)?.setText("复制key")

                                    getDialog()?.findViewById(R.id.progress_bar)?.visibility = View.GONE
                                    getDialog()?.getButton(DialogInterface.BUTTON_NEGATIVE)?.visibility = View.VISIBLE
                                    getDialog()?.getButton(DialogInterface.BUTTON_POSITIVE)?.visibility = View.VISIBLE
                                }
                            }
                        } catch (ex: Exception) {
                            ex.printStackTrace()
                            getDialog()?.cancel()
                        }
                    }

                    override fun onError(httpURLConnection: HttpURLConnection?, exception: Exception) {
                        mHandler?.post {
                            getDialog()?.cancel()
                            Toast.makeText(getContext(), "获取失败", Toast.LENGTH_SHORT).show()
                        }
                    }
                })
    }
}