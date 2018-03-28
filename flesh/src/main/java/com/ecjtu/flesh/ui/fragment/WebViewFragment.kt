package com.ecjtu.flesh.ui.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import com.ecjtu.flesh.R
import com.ecjtu.flesh.util.activity.ActivityUtil
import com.ecjtu.flesh.util.file.FileUtil
import java.io.File

/**
 * Created by Ethan_Xiang on 2018/2/5.
 */
class WebViewFragment : Fragment() {

    companion object {
        const val TAG = "WebViewFragment"
        const val EXTRA_URL = "extra_url"
        const val EXTRA_TYPE = "extra_type"
        const val WEB_ROOT_PATH = "web"
        const val TYPE_INNER_WEB = 0x10
        const val TYPE_DEFAULT = TYPE_INNER_WEB shl 1
        const val TYPE_MIME = TYPE_DEFAULT shl 1
        const val INTERFACE_NAME = "android"

        @JvmStatic
        fun openUrl(url: String): Bundle {
            return Bundle().apply { putString(WebViewFragment.EXTRA_URL, url) }
        }

        @JvmStatic
        fun openInnerUrl(url: String): Bundle {
            return Bundle().apply {
                putString(WebViewFragment.EXTRA_URL, url)
                putInt(WebViewFragment.EXTRA_TYPE, WebViewFragment.TYPE_INNER_WEB)
            }
        }

        @JvmStatic
        fun openWithMIME(url: String): Bundle {
            return Bundle().apply {
                putString(WebViewFragment.EXTRA_URL, url)
                putInt(WebViewFragment.EXTRA_TYPE, WebViewFragment.TYPE_MIME)
            }
        }
    }

    private var mWebView: WebView? = null
    private var mJsInterface: JavaScriptInterface? = null
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_web_view, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initWebView()

        if (arguments != null) {
            var url = arguments.get(EXTRA_URL) as String?
            var type = arguments.get(EXTRA_TYPE)
            if (type == null) {
                type = TYPE_DEFAULT
            }

            if (type == TYPE_DEFAULT) {
                if (url != null) {
                    mWebView?.loadUrl(url)
                }
            } else if (type == TYPE_INNER_WEB) {
                var file = context.getExternalFilesDir(WEB_ROOT_PATH)
                if (file != null) {
                    file = File(file, url)
                    if (file.exists() && !file.isDirectory) {
                        mWebView?.loadUrl("file://${file.absolutePath}")
                        Log.e(TAG, "load by dynamic")
                        return
                    }
                }
                mWebView?.loadUrl("file:///android_asset/${WEB_ROOT_PATH}/${url}")
                Log.e(TAG, "load by static")
            } else if (type == TYPE_MIME) {
                val extension = MimeTypeMap.getFileExtensionFromUrl(url)
                val mime = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
                        ?: "text/html"
                toDoWithMIME(mime, url)
            }
        }
    }

    @SuppressLint("JavascriptInterface")
    private fun initWebView() {
        mWebView = view?.findViewById(R.id.web_view) as WebView?
        mWebView?.setWebViewClient(SimpleWebViewClient())
        mWebView?.setWebChromeClient(SimpleWebChromeClient())

        val settings = mWebView?.getSettings()
        settings?.javaScriptEnabled = true

        mJsInterface = JavaScriptInterface(context)
        mWebView?.addJavascriptInterface(mJsInterface, INTERFACE_NAME)
    }

    private fun toDoWithMIME(mime: String?, url: String?) {
        if (mime?.startsWith("text") == true) {
            var arr = FileUtil.readFileContent(File(url))
            if (arr != null) {
                mWebView?.loadDataWithBaseURL(null, String(arr), mime, "utf-8", null)
            }
        } else if (mime?.startsWith("image") == true || mime?.startsWith("video") == true) {
            mWebView?.loadUrl("file://${url}")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mWebView != null) {
            mWebView?.removeJavascriptInterface(INTERFACE_NAME)
            mWebView?.setWebViewClient(null)
            mWebView?.setWebChromeClient(null)
            mWebView?.loadDataWithBaseURL(null, "", "text/html", "utf-8", null)
            mWebView?.clearHistory()
            (mWebView?.getParent() as ViewGroup).removeView(mWebView)
            mWebView?.destroy()
            mWebView = null
        }
    }

    class JavaScriptInterface(val context: Context) {

        @JavascriptInterface
        fun gotoAppDetailSettings() {
            context.startActivity(ActivityUtil.getAppDetailSettingIntent(context))
        }
    }

    class SimpleWebViewClient : WebViewClient() {
        init {
        }

        override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
            view?.loadUrl(url)
            return true
        }
    }

    class SimpleWebChromeClient : WebChromeClient() {
    }
}