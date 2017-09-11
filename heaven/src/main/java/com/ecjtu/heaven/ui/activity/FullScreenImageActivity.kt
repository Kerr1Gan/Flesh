package com.ecjtu.heaven.ui.activity

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.BitmapImageViewTarget
import com.bumptech.glide.request.target.Target
import com.ecjtu.heaven.R

/**
 * Created by KerriGan on 2017/9/11.
 */
class FullScreenImageActivity : AppCompatActivity(),RequestListener<Bitmap>{

    companion object {
        private const val EXTRA_URI = "full_screen_extra_uri"

        fun newInstance(context: Context,uri:String):Intent{
            val intent = Intent(context,FullScreenImageActivity::class.java)
            intent.putExtra(EXTRA_URI,uri)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_full_screen_image)
        if(intent.extras!=null){
            val uri = intent.extras.getString(EXTRA_URI,"")
            if(!TextUtils.isEmpty(uri)){
                val builder = LazyHeaders.Builder().addHeader("User-Agent", "Mozilla/5.0 (Linux; Android 5.1.1; Nexus 6 Build/LYZ28E) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.90 Mobile Safari/537.36")
                        .addHeader("Accept", "image/webp,image/apng,image/*,*/*;q=0.8")
                        .addHeader("Accept-Encoding", "gzip, deflate")
                        .addHeader("Accept-Language", "zh-CN,zh;q=0.8")
                        .addHeader("Host", "i.meizitu.net")
                        .addHeader("Proxy-Connection", "keep-alive")
                        .addHeader("Referer", "http://m.mzitu.com/")
                val glideUrl = GlideUrl(uri, builder.build())
                Glide.with(this).asBitmap().load(glideUrl).listener(this).into(findViewById(R.id.image) as ImageView)
                return
            }
        }
        finish()
    }

    override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Bitmap>?, isFirstResource: Boolean): Boolean {
        return false
    }

    override fun onResourceReady(resource: Bitmap?, model: Any?, target: Target<Bitmap>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
        (target as BitmapImageViewTarget).view.setImageBitmap(resource)
        return true
    }

}