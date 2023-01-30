package com.eddy.debuglibrary.presentation.view.ui.permission

sealed interface PermissionEvent {
    object Allow: PermissionEvent
    object Deny: PermissionEvent
    object NeverDeny: PermissionEvent
}