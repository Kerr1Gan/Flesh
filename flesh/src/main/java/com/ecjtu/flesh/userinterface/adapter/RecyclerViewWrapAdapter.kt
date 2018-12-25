package com.ecjtu.flesh.userinterface.adapter

import android.support.v7.widget.RecyclerView

/**
 * Created by Ethan_Xiang on 2017/9/26.
 */
abstract class RecyclerViewWrapAdapter<T : RecyclerView.ViewHolder> : RecyclerView.Adapter<T>() {
    private val mListHeight = ArrayList<Int>()

    open protected fun getHeight(position: Int): Int {
        if (position >= mListHeight.size || position < 0) {
            return 0
        }
        return mListHeight[position]
    }

    open protected fun setHeight(position: Int, height: Int) {
        if (position >= mListHeight.size) {
            val diff = position - mListHeight.size + 1
            mListHeight.addAll(Array<Int>(diff, { height }))
        }
        mListHeight.set(position, height)
    }
}