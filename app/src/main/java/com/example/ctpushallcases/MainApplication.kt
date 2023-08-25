package com.example.ctpushallcases

import android.app.Application
import com.clevertap.android.pushtemplates.PushTemplateNotificationHandler
import com.clevertap.android.sdk.ActivityLifecycleCallback
import com.clevertap.android.sdk.CleverTapAPI
import com.clevertap.android.sdk.interfaces.NotificationHandler

//import com.clevertap.android.sdk.interfaces.NotificationHandler

class MainApplication(): Application() {
    override fun onCreate() {
        ActivityLifecycleCallback.register(this)
        super.onCreate()
        CleverTapAPI.setDebugLevel(CleverTapAPI.LogLevel.VERBOSE)
        CleverTapAPI.setNotificationHandler(PushTemplateNotificationHandler() as NotificationHandler)

    }
}