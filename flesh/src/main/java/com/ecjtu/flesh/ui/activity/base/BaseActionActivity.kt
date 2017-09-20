package com.ecjtu.flesh.ui.activity.base

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Point
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.v4.content.LocalBroadcastManager
import android.support.v7.app.AppCompatActivity
import android.view.KeyCharacterMap
import android.view.KeyEvent
import android.view.ViewConfiguration


/**
 * Created by KeriGan on 2017/6/25.
 */
abstract class BaseActionActivity : AppCompatActivity(), MemoryUnLeakHandler.IHandleMessage {

    private var mLocalBroadcastManger: LocalBroadcastManager? = null

    private var mIntentFilter: IntentFilter? = null

    private var mBroadcastReceiver: SimpleReceiver? = null

    private var mSimpleHandler: SimpleHandler? = null

    companion object {
        const val NAVIGATION_BAR_HEIGHT = "navigation_bar_height"
        const val STATUS_BAR_HEIGHT = "status_bar_height"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mLocalBroadcastManger = LocalBroadcastManager.getInstance(this)
        mIntentFilter = IntentFilter()
        mBroadcastReceiver = SimpleReceiver()
        registerActions(mIntentFilter)

        mSimpleHandler = SimpleHandler(this)
    }

    override fun onResume() {
        super.onResume()
        mLocalBroadcastManger?.registerReceiver(mBroadcastReceiver, mIntentFilter)
    }

    override fun onStop() {
        super.onStop()
        mLocalBroadcastManger?.unregisterReceiver(mBroadcastReceiver)
    }

    inner class SimpleReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context?, intent: Intent?) {
            this@BaseActionActivity.handleActions(intent?.action, intent)
        }
    }

    open fun registerActions(intentFilter: IntentFilter?) {
        //to register action by override
    }

    open fun handleActions(action: String?, intent: Intent?) {
        //override
    }

    open fun unregisterActions() {
        mLocalBroadcastManger?.unregisterReceiver(mBroadcastReceiver)
    }

    open fun getIntentFilter(): IntentFilter? {
        return mIntentFilter
    }

    open fun registerActions(array: Array<String>, intentFilter: IntentFilter) {
        for (action in array) {
            intentFilter.addAction(action)
        }
        mLocalBroadcastManger?.registerReceiver(mBroadcastReceiver, intentFilter)
        mIntentFilter = intentFilter
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    }

    fun getHandler(): Handler? {
        return mSimpleHandler
    }

    override fun handleMessage(msg: Message) {
        //do nothing
    }

    class SimpleHandler(host: BaseActionActivity) :
            MemoryUnLeakHandler<BaseActionActivity>(host)

    fun isNavigationBarShow(activity: Activity): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            val display = windowManager.defaultDisplay
            val size = Point()
            val realSize = Point()
            display.getSize(size)
            display.getRealSize(realSize)
            return realSize.y !== size.y
        } else {
            val menu = ViewConfiguration.get(activity).hasPermanentMenuKey()
            val back = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK)
            return !(menu || back)
        }
    }

    fun getNavigationBarHeight(activity: Activity): Int {
        if (!isNavigationBarShow(activity)) {
            return 0
        }
        val resources = activity.resources
        val resourceId = resources.getIdentifier(NAVIGATION_BAR_HEIGHT, "dimen", "android")
        //获取NavigationBar的高度
        return resources.getDimensionPixelSize(resourceId)
    }


    fun getScreenHeight(activity: Activity): Int {
        return activity.windowManager.defaultDisplay.height + getNavigationBarHeight(activity)
    }

    fun getStatusBarHeight(): Int {
        val resources = getResources()
        val resourceId = resources.getIdentifier(STATUS_BAR_HEIGHT, "dimen", "android")
        return resources.getDimensionPixelSize(resourceId)
    }
}
