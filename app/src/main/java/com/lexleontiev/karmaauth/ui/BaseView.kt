package com.lexleontiev.karmaauth.ui


interface BaseView<T: BasePresenter> {

    fun setPresener(presenter: T)
}