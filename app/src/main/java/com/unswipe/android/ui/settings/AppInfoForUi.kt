package com.unswipe.android.ui.settings

import android.content.pm.ApplicationInfo

data class AppInfoForUi(
    val name: String,
    val packageName: String,
    val icon: ApplicationInfo,
    val isBlocked: Boolean
) 