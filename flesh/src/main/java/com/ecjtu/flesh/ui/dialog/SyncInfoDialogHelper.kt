package com.ecjtu.flesh.ui.dialog

import android.content.Context
import android.content.DialogInterface
import android.preference.PreferenceManager
import android.support.v7.app.AlertDialog
import android.telephony.TelephonyManager
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import com.amazonaws.ClientConfiguration
import com.amazonaws.Protocol
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.regions.Region
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.ObjectMetadata
import com.amazonaws.services.s3.model.PutObjectRequest
import com.ecjtu.flesh.Constants
import com.ecjtu.flesh.R
import com.ecjtu.flesh.db.DatabaseManager.Companion.DB_NAME
import com.ecjtu.flesh.util.encrypt.SecretKeyUtils
import java.text.SimpleDateFormat
import java.util.*
import kotlin.concurrent.thread

/**
 * Created by Ethan_Xiang on 2018/3/12.
 */
class SyncInfoDialogHelper(context: Context) : BaseDialogHelper(context) {

    private var mS3: AmazonS3Client? = null

    private val mDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())

    override fun onCreateDialog(): AlertDialog? {
        val telephonyManager = getContext().getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager?
        var deviceId = telephonyManager?.getDeviceId()
        if (TextUtils.isEmpty(deviceId)) {
            deviceId = PreferenceManager.getDefaultSharedPreferences(getContext()).getString(Constants.PREF_VIP_INFO, "")
        }
        val dialog = getBuilder()?.setTitle(R.string.sync_data)
                ?.setMessage("")
                ?.setPositiveButton(R.string.upload, { dialog: DialogInterface, which: Int ->
                    thread {
                        try {
                            if (mS3 == null) {
                                val secretKey = SecretKeyUtils.getKeyFromServer()
                                val content = SecretKeyUtils.getS3InfoFromServer(secretKey!!.key)
                                val params = content.split(",")
                                val provider = BasicAWSCredentials(params[0], params[1])
                                val config = ClientConfiguration()
                                config.protocol = Protocol.HTTP
                                mS3 = AmazonS3Client(provider, config)
                                val region = Region.getRegion(Regions.CN_NORTH_1)
                                mS3?.setRegion(region)
                                mS3?.setEndpoint(Constants.S3_URL)
                            }
                            val dbPath = getContext().getDatabasePath(DB_NAME)
                            val request = PutObjectRequest("firststorage0001", "databases/$deviceId", dbPath)
                            request.metadata = ObjectMetadata()
                            val time = System.currentTimeMillis()
                            request.metadata.getUserMetadata().put("update_time", time.toString())
                            mS3?.putObject(request)
                            getHandler().post {
                                Toast.makeText(getContext(), R.string.upload_success, Toast.LENGTH_SHORT).show()
                            }
                        } catch (ex: Exception) {
                            ex.printStackTrace()
                            getHandler().post {
                                Toast.makeText(getContext(), R.string.upload_failure, Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                })
                ?.setNeutralButton(R.string.sync, { _, _ ->
                    if (!TextUtils.isEmpty(deviceId)) {
                        SyncInfoProgressDialog(getContext(), deviceId!!).getDialog()?.show()
                    }
                })
                ?.setNegativeButton(R.string.no, null)
                ?.setView(R.layout.layout_progress)
                ?.create()
        return dialog
    }

    override fun onDialogShow(dialog: AlertDialog) {
        super.onDialogShow(dialog)
        dialog.getButton(DialogInterface.BUTTON_POSITIVE).visibility = View.GONE
        dialog.getButton(DialogInterface.BUTTON_NEGATIVE).visibility = View.GONE
        dialog.getButton(DialogInterface.BUTTON_NEUTRAL).visibility = View.GONE
        thread {
            var index = 0
            do {
                try {
                    val telephonyManager = getContext().getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager?
                    var deviceId = telephonyManager?.getDeviceId()
                    if (TextUtils.isEmpty(deviceId)) {
                        deviceId = PreferenceManager.getDefaultSharedPreferences(getContext()).getString("deviceId", "")
                    }
                    if (TextUtils.isEmpty(deviceId)) {
                        deviceId = PreferenceManager.getDefaultSharedPreferences(getContext()).getString(Constants.PREF_VIP_INFO, "")
                    }
                    var longDeviceId = 0L
                    try {
                        longDeviceId = deviceId?.toLong() ?: 0
                    } catch (ex: Exception) {
                        ex.printStackTrace()
                        longDeviceId = 1
                    }
                    if (TextUtils.isEmpty(deviceId) || longDeviceId == 0L) {
                        getHandler().post {
                            System.out.println("SyncInfoDialogHelper deviceId null cancel")
                            getDialog()?.cancel()
                            val builder = AlertDialog.Builder(getContext())
                            builder.setTitle(R.string.warn)
                                    .setMessage(R.string.sync_info_dialog_warning)
                                    .setPositiveButton(R.string.ok, null)
                                    .create().show()
                        }
                        return@thread
                    }
                    val secretKey = SecretKeyUtils.getKeyFromServer()
                    val content = SecretKeyUtils.getS3InfoFromServer(secretKey!!.key)
                    val params = content.split(",")
                    val provider = BasicAWSCredentials(params[0], params[1])
                    val config = ClientConfiguration()
                    if (index == 0) {
                        config.protocol = Protocol.HTTP
                    } else {
                        config.protocol = Protocol.HTTPS
                    }
                    mS3 = AmazonS3Client(provider, config)
                    val region = Region.getRegion(Regions.CN_NORTH_1)
                    mS3?.setRegion(region)
                    mS3?.setEndpoint(Constants.S3_URL)
                    val s3ObjectMetaData = mS3?.getObjectMetadata("firststorage0001", "databases/$deviceId")
                    if (s3ObjectMetaData != null) {
                        val time = s3ObjectMetaData.getUserMetaDataOf("update_time")
                        val timeLong = time?.toLong() ?: -1L
                        val timeString = if (timeLong == -1L) "" else mDateFormat.format(timeLong)
                        getHandler().post {
                            dialog.getButton(DialogInterface.BUTTON_POSITIVE).visibility = View.VISIBLE
                            dialog.getButton(DialogInterface.BUTTON_NEGATIVE).visibility = View.VISIBLE
                            dialog.getButton(DialogInterface.BUTTON_NEUTRAL).visibility = View.VISIBLE
                            dialog.findViewById<View>(R.id.progress_bar)?.visibility = View.GONE
                            getDialog()?.setMessage(getContext().getString(R.string.sync_info_explain) + "$timeString.")
                        }
                        break
                    }
                    if (index >= 1) break
                } catch (ex: Exception) {
                    ex.printStackTrace()
                    if (index >= 1) {
                        if (ex.toString().contains("404")) {
                            getHandler().post {
                                dialog.getButton(DialogInterface.BUTTON_POSITIVE).visibility = View.VISIBLE
                                dialog.getButton(DialogInterface.BUTTON_NEGATIVE).visibility = View.VISIBLE
                                dialog.getButton(DialogInterface.BUTTON_NEUTRAL).visibility = View.VISIBLE
                                dialog.findViewById<View>(R.id.progress_bar)?.visibility = View.GONE
                            }
                        } else {
                            getDialog()?.cancel()
                            System.out.println("SyncInfoDialogHelper amazon can not connect " + ex.toString())
                        }
                        break
                    }
                    index++
                }
            } while (!Thread.interrupted())
        }
    }

    override fun onDialogCancel(dialog: AlertDialog) {
        super.onDialogCancel(dialog)
//        getHandler().removeMessages(0, null)
    }
}