package com.ecjtu.componentes.activity

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.OrientationEventListener

/**
 * Created by Ethan_Xiang on 2017/10/12.
 */
open class RotateByOrientationActivity : BaseFragmentActivity() {


    private var mOrientationListener: OrientationEventListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initOrientationListener()
    }

    private fun initOrientationListener() {
        mOrientationListener = object : OrientationEventListener(this) {
            override fun onOrientationChanged(rotation: Int) {
                // 设置竖屏
                if (rotation >= 0 && rotation <= 45 || rotation >= 315 || rotation >= 135 && rotation <= 225) {
                    this@RotateByOrientationActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
                } else if (rotation > 45 && rotation < 135 || rotation > 225 && rotation < 315) {
                    // 设置横屏
                    if (rotation > 225 && rotation < 315) {
                        this@RotateByOrientationActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
                    } else {
                        this@RotateByOrientationActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE)
                    }
                }
            }
        }
        mOrientationListener?.enable()
    }
}