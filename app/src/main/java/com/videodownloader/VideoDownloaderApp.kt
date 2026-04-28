package com.videodownloader

import android.app.Application
import android.util.Log

class VideoDownloaderApp : Application() {
    
    override fun onCreate() {
        super.onCreate()
        instance = this
        Log.d("VideoDownloader", "Application initialized")
    }
    
    companion object {
        lateinit var instance: VideoDownloaderApp
            private set
    }
}
