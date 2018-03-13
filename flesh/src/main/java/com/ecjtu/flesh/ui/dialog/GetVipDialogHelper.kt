package com.ecjtu.flesh.ui.dialog

import android.content.*
import android.preference.PreferenceManager
import android.support.v7.app.AlertDialog
import android.telephony.TelephonyManager
import android.text.TextUtils
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
        const val API_URI = "/api/getUserByDeviceId?deviceId="
    }

    override fun onCreateDialog(): AlertDialog? {
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

        val deviceId = (getContext().getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager?)?.deviceId
        if (TextUtils.isEmpty(deviceId)) {
            val builder = AlertDialog.Builder(getContext())
            builder.setTitle("警告")
            builder.setMessage("由于您使用的是虚拟机，所以购买后请牢记Vip信息.")
            builder.setPositiveButton("确定", null)
        }
        return getBuilder()?.create()
    }

    override fun onDialogShow(dialog: AlertDialog) {
        val deviceId = (getContext().getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager?)?.deviceId
        doRequest(deviceId)
        getDialog()?.getButton(DialogInterface.BUTTON_NEGATIVE)?.visibility = View.GONE
        getDialog()?.getButton(DialogInterface.BUTTON_POSITIVE)?.visibility = View.GONE
    }

    override fun onDialogCancel(dialog: AlertDialog) {
        super.onDialogCancel(dialog)
        getHandler().removeMessages(0, null)
    }

    private fun doRequest(deviceId: String?) {
        var local = deviceId
        if (TextUtils.isEmpty(local)) {
            local = PreferenceManager.getDefaultSharedPreferences(getContext()).getString("paymentId", "")
        }
        AsyncNetwork().request(Constants.SERVER_URL + API_URI + local)
                .setRequestCallback(object : IRequestCallbackV2 {
                    override fun onSuccess(httpURLConnection: HttpURLConnection?, response: String) {
                        try {
                            val jObj = JSONObject(response)
                            val code = (jObj.opt("code") as String).toInt()
                            if (code < 0) {
                                getHandler().post {
                                    val messageView = getDialog()?.findViewById(android.R.id.message) as TextView?
                                    messageView?.setText("通过PayPal支付5刀即可获得1月Vip。")
                                    messageView?.visibility = View.VISIBLE

                                    getDialog()?.findViewById(R.id.progress_bar)?.visibility = View.GONE
                                    getDialog()?.getButton(DialogInterface.BUTTON_NEGATIVE)?.visibility = View.VISIBLE
                                    getDialog()?.getButton(DialogInterface.BUTTON_POSITIVE)?.visibility = View.VISIBLE
                                }
                            } else {
                                var json = JSONObject(response)
                                json = json.optJSONObject("data")
                                val key = json.optString("paymentId")
                                getHandler().post {
                                    val messageView = getDialog()?.findViewById(android.R.id.message) as TextView?
                                    messageView?.setText("您的Vip信息：\n" + "key:" + key)
                                    messageView?.visibility = View.VISIBLE
                                    getDialog()?.getButton(DialogInterface.BUTTON_POSITIVE)?.setText("复制key")
                                    getDialog()?.getButton(DialogInterface.BUTTON_POSITIVE)?.setOnClickListener {
                                        //获取剪贴板管理器：
                                        val cm = getContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager?
                                        // 创建普通字符型ClipData
                                        val mClipData = ClipData.newPlainText("Vip key", key ?: "")
                                        // 将ClipData内容放到系统剪贴板里。
                                        cm?.setPrimaryClip(mClipData)
                                        Toast.makeText(getContext(), "已复制到剪贴板", Toast.LENGTH_SHORT).show()
                                        getDialog()?.cancel()
                                    }

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
                        getHandler().post {
                            getDialog()?.cancel()
                            Toast.makeText(getContext(), "获取失败", Toast.LENGTH_SHORT).show()
                        }
                    }
                })
    }

}