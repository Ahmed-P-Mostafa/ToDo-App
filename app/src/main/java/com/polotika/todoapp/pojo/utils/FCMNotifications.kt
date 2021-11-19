package com.polotika.todoapp.pojo.utils

import com.google.firebase.messaging.FirebaseMessagingService

class FCMNotifications :FirebaseMessagingService() {

    override fun onNewToken(p0: String) {
        super.onNewToken(p0)

    }
}