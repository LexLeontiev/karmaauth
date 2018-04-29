package com.lexleontiev.karmaauth

import android.app.Application
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger


class KarmaAuthApplication: Application() {

    override fun onCreate() {
        super.onCreate()

        //init logger
        if (BuildConfig.DEBUG) {
            Logger.addLogAdapter(AndroidLogAdapter())
        }
    }
}