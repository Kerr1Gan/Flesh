package com.ecjtu.flesh.userinterface.dialog

import android.content.Context
import android.content.DialogInterface
import android.support.v7.app.AlertDialog
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.ecjtu.flesh.BuildConfig
import com.ecjtu.flesh.R
import com.ecjtu.flesh.model.models.UpdateBean
import com.ecjtu.netcore.Constants
import com.google.gson.Gson
import okhttp3.*
import java.io.IOException

class CheckUpdateDialogHelper(context: Context) : BaseDialogHelper(context) {

    private lateinit var okHttpClient: OkHttpClient
    private var call: Call? = null
    private var content: View? = null
    override fun onCreateDialog(): AlertDialog? {
        content = LayoutInflater.from(getContext()).inflate(R.layout.layout_progress, null)
        getBuilder()?.setTitle(R.string.check_update)
                ?.setMessage("正在获取更新信息...")
                ?.setView(content)
                ?.setNegativeButton(R.string.cancel, null)
                ?.setPositiveButton(R.string.ok, object : DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface?, which: Int) {

                    }
                })

        return getBuilder()?.create()
    }

    override fun onDialogShow(dialog: AlertDialog) {
        super.onDialogShow(dialog)
        getDialog()?.getButton(DialogInterface.BUTTON_POSITIVE)?.isEnabled = false
        okHttpClient = OkHttpClient()
        val request = Request.Builder()
                .url(Constants.CHECK_UPDATE_URL)
                .get()
                .build()
        call = okHttpClient.newCall(request)
        call?.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                getHandler().post {
                    val proBar = dialog.findViewById<View>(R.id.progress_bar)
                    proBar?.visibility = View.GONE
                    val parent = getContentLayout()
                    val params = parent?.layoutParams
                    params?.height = 0
                    parent?.layoutParams = params
                    parent?.requestLayout()
                    Toast.makeText(getContext(), "check update failed", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                getHandler().post {
                    val proBar = dialog.findViewById<View>(R.id.progress_bar)
                    proBar?.visibility = View.GONE
                    val parent = getContentLayout()
                    val params = parent?.layoutParams
                    params?.height = 0
                    parent?.layoutParams = params
                    parent?.requestLayout()
                }
                try {
                    val body = response.body()?.string()
                    if (!TextUtils.isEmpty(body)) {
                        val updateBean = Gson().fromJson(body, UpdateBean::class.java)
                        if (updateBean != null) {
                            if (updateBean.versionCode > BuildConfig.VERSION_CODE) {
                                needUpdate(updateBean.versionCode, updateBean.url)
                            }
                        }
                    }
                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
            }
        })

    }

    override fun onDialogCancel(dialog: AlertDialog) {
        super.onDialogCancel(dialog)
        call?.cancel()
    }

    private fun needUpdate(version: Int, url: String) {
        getHandler().post {
            getDialog()?.getButton(DialogInterface.BUTTON_POSITIVE)?.isEnabled = true
        }
    }

    private fun getContentLayout(): ViewGroup? {
        return content?.parent?.parent as ViewGroup?
    }

}