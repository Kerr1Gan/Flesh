package com.ecjtu.flesh.mvp.presenter

class MainContract {
    interface Presenter {
        fun onStop()
        fun onResume()
        fun onDestroy()
    }

    interface View
}