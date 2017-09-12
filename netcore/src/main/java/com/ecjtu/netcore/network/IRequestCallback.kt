package com.ecjtu.netcore.network

import java.lang.Exception
import java.net.HttpURLConnection

/**
 * Created by Ethan_Xiang on 2017/7/14.
 */
interface IRequestCallback {
    fun onSuccess(httpURLConnection: HttpURLConnection?, response: String)
}

interface IRequestCallbackV2 : IRequestCallback {
    fun onError(httpURLConnection: HttpURLConnection?, exception: Exception)
}