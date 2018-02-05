package com.ecjtu.flesh.notification

import android.app.PendingIntent
import android.content.Context
import com.ecjtu.componentes.activity.ActionBarFragmentActivity
import com.ecjtu.flesh.ui.activity.PageDetailActivity
import com.ecjtu.flesh.ui.fragment.WebViewFragment

/**
 * Created by Ethan_Xiang on 2017/9/22.
 */
class SimpleNotificationBuilder(context: Context) : SimpleNotification(context) {

    companion object {
        const val ID = 0x100
    }

    fun build(title: String, content: String, ticker: String, actionDetailUrl: String) {
        val builder = super.buildNotification(ID, title, content, ticker)
        fullScreenIntent(getCurrentBuilder(), 0, null)
        val intent = PageDetailActivity.newInstance(context, actionDetailUrl, actionDetailUrl, "", "")
        builder.setContentIntent(PendingIntent.getActivity(context, 100, intent, PendingIntent.FLAG_UPDATE_CURRENT)) //requestCode 不能为0 否则MainActivity 将重建
    }

    fun buildH5(title: String, content: String, ticker: String, h5Url: String) {
        val builder = super.buildNotification(ID, title, content, ticker)
        fullScreenIntent(getCurrentBuilder(), 0, null)
        var intent = ActionBarFragmentActivity.newInstance(context, WebViewFragment::class.java, WebViewFragment.openUrl(h5Url))
        context.startActivity(intent)
        builder.setContentIntent(PendingIntent.getActivity(context, 100, intent, PendingIntent.FLAG_UPDATE_CURRENT)) //requestCode 不能为0 否则MainActivity 将重建
    }

    override fun send(tag: String?) {
        super.sendNotification(ID, getCurrentBuilder(), tag)
    }

    override fun cancel(tag: String?) {
        super.cancelNotification(ID, tag)
    }

}