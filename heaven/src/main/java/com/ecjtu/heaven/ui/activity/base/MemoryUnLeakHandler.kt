package com.ecjtu.heaven.ui.activity.base

import android.os.Handler
import android.os.Message
import java.lang.ref.WeakReference

/**
 * Created by Ethan_Xiang on 2017/7/3.
 */
open class MemoryUnLeakHandler<T : MemoryUnLeakHandler.IHandleMessage>(host: T): Handler(){

    private var mWeakRef: WeakReference<T>? =null
    init {
        mWeakRef= WeakReference(host)
    }

    override fun handleMessage(msg: Message?) {
        super.handleMessage(msg)
        if(mWeakRef?.get()==null) return
        mWeakRef?.get()?.handleMessage(msg!!)
    }

    interface IHandleMessage{
        fun handleMessage(msg: Message)
    }
}