package com.example.trip_to_hyeonchungsa.tthLib.context

import android.app.Application
import android.content.Context

/**
 * Application 클래스
 * 전역 Context를 제공합니다.
 */
class App : Application() {
    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        private lateinit var instance: App

        /**
         * Application Context를 가져옵니다.
         */
        fun getContext(): Context = instance.applicationContext
    }
}