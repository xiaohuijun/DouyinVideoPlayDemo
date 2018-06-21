package com.demo.douyinvideoplay

import android.app.Application

class MApplication : Application() {
    override fun onCreate() {
        super.onCreate()
    }

    companion object {
        var isDouyinVideoFragmentVisible: Boolean = false
    }
}