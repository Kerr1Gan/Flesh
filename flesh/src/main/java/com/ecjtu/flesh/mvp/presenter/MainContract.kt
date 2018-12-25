package com.ecjtu.flesh.mvp.presenter

import com.ecjtu.flesh.mvp.IPresenter

class MainContract {
    interface Presenter : IPresenter<View> {
        fun onStop()
        fun onResume()
        fun onDestroy()
    }

    interface View : IPresenter<Presenter> {

    }
}