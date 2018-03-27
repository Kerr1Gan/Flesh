package com.ecjtu.flesh.ui.dialog

import android.content.*
import android.os.Environment
import android.preference.PreferenceManager
import android.support.v7.app.AlertDialog
import android.telephony.TelephonyManager
import android.text.TextUtils
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.ecjtu.flesh.Constants
import com.ecjtu.flesh.R
import com.ecjtu.flesh.ui.activity.PayPalActivity
import com.ecjtu.flesh.util.CloseableUtil
import com.ecjtu.flesh.util.encrypt.MD5Utils
import com.ecjtu.netcore.network.AsyncNetwork
import com.ecjtu.netcore.network.IRequestCallbackV2
import org.json.JSONObject
import java.io.*
import java.net.HttpURLConnection


/**
 * Created by xiang on 2018/3/9.
 */
class GetVipDialogHelper(context: Context) : BaseDialogHelper(context) {

    companion object {
        const val API_URI = "/api/getUserByDeviceId?deviceId="
    }

    private var mDeviceId: String? = null

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
                ?.setNeutralButton("输入Vip key", { _, _ ->
                    doVipKey()
                })
        val deviceId = (getContext().getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager?)?.deviceId
        var longLocal = 0L
        longLocal = try {
            deviceId?.toLong() ?: 0
        } catch (ex: Exception) {
            ex.printStackTrace()
            0L
        }
        if (TextUtils.isEmpty(deviceId) || longLocal == 0L) {
            getHandler().post {
                val builder = AlertDialog.Builder(getContext())
                builder.setTitle("警告")
                builder.setMessage("由于您使用的是虚拟机，所以购买后请牢记Vip信息.")
                builder.setPositiveButton("确定", null)
                        .create()
                        .show()
            }
        }
        return getBuilder()?.create().apply { this?.setCancelable(false) }
    }

    override fun onDialogShow(dialog: AlertDialog) {
        val deviceId = (getContext().getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager?)?.deviceId
        doRequest(deviceId)
        getDialog()?.getButton(DialogInterface.BUTTON_NEGATIVE)?.visibility = View.GONE
        getDialog()?.getButton(DialogInterface.BUTTON_POSITIVE)?.visibility = View.GONE
        getDialog()?.getButton(DialogInterface.BUTTON_NEUTRAL)?.visibility = View.GONE
    }

    override fun onDialogCancel(dialog: AlertDialog) {
        super.onDialogCancel(dialog)
        getHandler().removeMessages(0, null)
    }

    private fun doRequest(deviceId: String?) {
        var local = deviceId
        var longLocal = 0L
        try {
            longLocal = deviceId?.toLong() ?: 0
            if (longLocal == 0L) {
                mDeviceId = ""
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        if (TextUtils.isEmpty(local) || longLocal == 0L) {
            local = PreferenceManager.getDefaultSharedPreferences(getContext()).getString("paymentId", "")
            if (TextUtils.isEmpty(local)) {
                if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
                    val sdUrl = Environment.getExternalStorageDirectory().absolutePath
                    val vipFile = File(sdUrl, Constants.LOCAL_VIP_PATH)
                    var reader: BufferedReader? = null
                    try {
                        reader = BufferedReader(FileReader(vipFile))
                        local = reader.readLine()
                        PreferenceManager.getDefaultSharedPreferences(getContext()).edit().putString("paymentId", local).apply()
                    } catch (ex: Exception) {
                        ex.printStackTrace()
                    } finally {
                        CloseableUtil.closeQuitely(reader)
                    }
                }
            } else {
                if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
                    val sdUrl = Environment.getExternalStorageDirectory().absolutePath
                    val vipFile = File(sdUrl, Constants.LOCAL_VIP_PATH)
                    vipFile.parentFile.mkdirs()
                    if (vipFile.exists() || vipFile.isDirectory) {
                        vipFile.delete()
                    }
                    var writer: BufferedWriter? = null
                    try {
                        writer = BufferedWriter(FileWriter(vipFile))
                        writer.write(local)
                    } catch (ex: Exception) {
                        ex.printStackTrace()
                    } finally {
                        CloseableUtil.closeQuitely(writer)
                    }
                }
            }
        }
        mDeviceId = local
        val serverUrl = PreferenceManager.getDefaultSharedPreferences(getContext()).getString(Constants.PREF_SERVER_URL, Constants.SERVER_URL)
        AsyncNetwork().request(serverUrl + API_URI + if (!TextUtils.isEmpty(local)) local else "-1")
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
                                    getDialog()?.getButton(DialogInterface.BUTTON_NEUTRAL)?.visibility = View.VISIBLE
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

    private fun verifyVip(vipKey: String?, deviceId: String?) {
        val serverUrl = PreferenceManager.getDefaultSharedPreferences(getContext()).getString(Constants.PREF_SERVER_URL, Constants.SERVER_URL)
        AsyncNetwork()
                .request(serverUrl + "/api/isPaySuccess?deviceId=$deviceId&paymentId=$vipKey")
                .setRequestCallback(object : IRequestCallbackV2 {
                    override fun onSuccess(httpURLConnection: HttpURLConnection?, response: String) {
                        try {
                            val jsonObj = JSONObject(response)
                            val code = jsonObj.optInt("code")
                            if (code == 0) {
                                getHandler().post {
                                    PreferenceManager.getDefaultSharedPreferences(getContext())
                                            .edit()
                                            .putString("deviceId", if (TextUtils.isEmpty(mDeviceId)) MD5Utils.MD5(vipKey!!) else mDeviceId)
                                            .putString("paymentId", vipKey)
                                            .apply()
                                    getDialog()?.cancel()
                                    Toast.makeText(getContext(), "验证成功", Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                getHandler().post {
                                    getDialog()?.cancel()
                                    Toast.makeText(getContext(), "验证失败", Toast.LENGTH_SHORT).show()
                                }
                            }
                        } catch (ex: Exception) {
                            ex.printStackTrace()
                        }
                    }

                    override fun onError(httpURLConnection: HttpURLConnection?, exception: java.lang.Exception) {
                        getHandler().post {
                            getDialog()?.cancel()
                            Toast.makeText(getContext(), "验证失败", Toast.LENGTH_SHORT).show()
                        }
                    }
                })
    }

    private fun doVipKey() {
        val builder = AlertDialog.Builder(getContext())
        builder.setTitle("Vip key")
        builder.setMessage("输入之前的Vip key即可以找回丢失的Vip哦！")
        builder.setView(R.layout.layout_edit_text)
        builder.setNegativeButton("不了", null)
        builder.setPositiveButton("确定", { dialog: DialogInterface, _ ->
            if (dialog is AlertDialog) {
                val vipKey = (dialog.findViewById(R.id.edit_text) as EditText?)?.text?.toString()
                verifyVip(vipKey, mDeviceId)
            }
        }).create().show()
    }


}