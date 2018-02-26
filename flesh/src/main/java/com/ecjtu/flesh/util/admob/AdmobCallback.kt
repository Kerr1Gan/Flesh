package com.ecjtu.flesh.util.admob

import com.google.android.gms.ads.reward.RewardItem

/**
 * Created by Ethan_Xiang on 2017/9/1.
 */
interface AdmobCallback {
    fun onLoaded()
    fun onError()
    fun onOpened()
    fun onClosed()
}

interface AdmobCallbackV2 : AdmobCallback {
    fun onReward(item: RewardItem?)
}