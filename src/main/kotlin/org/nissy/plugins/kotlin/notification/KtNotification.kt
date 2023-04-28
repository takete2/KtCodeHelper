package org.nissy.plugins.kotlin.notification

import com.intellij.notification.Notification
import com.intellij.notification.NotificationType
import com.intellij.notification.Notifications
import com.intellij.openapi.project.Project

class KtNotification {
    companion object {
        private const val FLUTTER_NOTIFICATION_GROUP_ID = "kotlin code Messages"
        private var project: Project? = null

        fun init(p: Project) {
            project = p
        }

        fun showErrorNotification(title: String?, message: String?) {
            Notifications.Bus.notify(
                Notification(
                    FLUTTER_NOTIFICATION_GROUP_ID,
                    title!!,
                    message!!,
                    NotificationType.ERROR
                ), project
            )
        }

        fun showInfoNotification(title: String?, message: String?) {
            Notifications.Bus.notify(
                Notification(
                    FLUTTER_NOTIFICATION_GROUP_ID,
                    title!!,
                    message!!,
                    NotificationType.INFORMATION
                ), project
            )
        }
    }


}