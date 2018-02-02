package com.ecjtu.flesh.ui.fragment

import android.app.Activity
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.graphics.Point
import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.TextUtils
import android.view.*
import com.ecjtu.componentes.activity.BaseActionActivity
import com.ecjtu.flesh.R
import tv.danmaku.ijk.media.exo.video.AndroidMediaController
import tv.danmaku.ijk.media.exo.video.IjkVideoView
import tv.danmaku.ijk.media.exo.video.SimpleMediaController
import tv.danmaku.ijk.media.player.IMediaPlayer

/**
 * Created by Ethan_Xiang on 2018/2/2.
 */
class IjkVideoFragment : Fragment(), GestureDetector.OnGestureListener, View.OnTouchListener {

    companion object {
        @JvmField
        val EXTRA_URI_PATH = "extra_uri_path"
    }

    private var mMediaController: AndroidMediaController? = null

    private var mVideoView: IjkVideoView? = null

    private var mIsPlaying: Boolean = false

    private var mGestureDetector: GestureDetector? = null

    private var mOrientationListener: OrientationEventListener? = null

    private var mIgnoreOrientation: Boolean = false

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_ijk_video_player, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init(view)
    }

    private fun init(view: View?) {
        if (mMediaController == null)
            mMediaController = AndroidMediaController(context)
        mMediaController?.setMediaPlayerCallback(mCallback)

        mVideoView = view?.findViewById(R.id.video_view) as IjkVideoView
        mVideoView?.setMediaController(mMediaController)
        mVideoView?.setOnInfoListener { mp, what, extra ->
            if (what == IMediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
//                mVideoView.setVisibility(View.VISIBLE)
//                mLoading.setVisibility(View.GONE)
            }
            false
        }
        var arg = arguments
        if (arg != null) {
            val uri = arg.getString(EXTRA_URI_PATH, "")
            if (!TextUtils.isEmpty(uri)) {
                mVideoView?.setVideoPath(uri)
                mVideoView?.start()
            } else {
                activity.finish()
            }
        } else {
            activity.finish()
        }

        mGestureDetector = GestureDetector(activity, this)
        initOrientationListener()

        if (isNavigationBarShow(activity)) {
            val root = view.findViewById(R.id.root)
            root.setPadding(root.paddingLeft, root.paddingTop, view.paddingRight, view.paddingBottom + getNavigationBarHeight(activity))
        }
    }

    private val mCallback = SimpleMediaController.MediaPlayerCallback {
        mIgnoreOrientation = !mIgnoreOrientation
        if (activity.getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE) {
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT)
        } else {
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE)
        }
    }

    override fun onResume() {
        super.onResume()
        if (mIsPlaying == true) {
            mVideoView?.resume()
        }
        mOrientationListener?.enable()
    }

    override fun onStop() {
        super.onStop()
        mIsPlaying = mVideoView?.isPlaying ?: mIsPlaying
        if (mIsPlaying) {
            mVideoView?.pause()
        }
        mOrientationListener?.disable()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        setOrientationConfig(newConfig.orientation)
    }

    private fun setOrientationConfig(orientation: Int) {
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN)
            activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE)
            //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
        } else if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN)
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
            val flags: Int
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                flags = View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                        View.SYSTEM_UI_FLAG_FULLSCREEN
            } else {
                flags = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            }
            activity.getWindow().getDecorView().setSystemUiVisibility(flags)
        }
    }

    private fun initOrientationListener() {
        mOrientationListener = object : OrientationEventListener(context) {
            override fun onOrientationChanged(rotation: Int) {
                if (mIgnoreOrientation) {
                    return
                }
                // 设置竖屏
                if (rotation >= 0 && rotation <= 45 || rotation >= 315 || rotation >= 135 && rotation <= 225) {
                    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
                } else if (rotation > 45 && rotation < 135 || rotation > 225 && rotation < 315) {
                    // 设置横屏
                    if (rotation > 225 && rotation < 315) {
                        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
                    } else {
                        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE)
                    }
                }
            }
        }
        mOrientationListener?.enable()
    }

    override fun onDown(e: MotionEvent): Boolean {
        return true
    }

    override fun onShowPress(e: MotionEvent) {

    }

    override fun onSingleTapUp(e: MotionEvent): Boolean {
        return false
    }

    override fun onScroll(e1: MotionEvent, e2: MotionEvent, distanceX: Float, distanceY: Float): Boolean {
        return false
    }

    override fun onLongPress(e: MotionEvent) {

    }

    override fun onFling(e1: MotionEvent, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
        if (e2.rawX - e1.rawX > 200 && Math.abs(e2.rawY - e1.rawY) < 200 && velocityX > 500) {
            activity.finish()
        }
        return false
    }

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        return mGestureDetector?.onTouchEvent(event) ?: false
    }

    fun isNavigationBarShow(activity: Activity): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            val display = activity.windowManager.defaultDisplay
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
        val resourceId = resources.getIdentifier(BaseActionActivity.NAVIGATION_BAR_HEIGHT, "dimen", "android")
        //获取NavigationBar的高度
        return resources.getDimensionPixelSize(resourceId)
    }
}