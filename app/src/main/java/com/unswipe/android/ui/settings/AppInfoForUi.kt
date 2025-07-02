package com.unswipe.android.ui.settings

import android.graphics.drawable.Drawable

data class AppInfoForUi(
    val name: String,
    val packageName: String,
    val icon: Drawable,
    val isBlocked: Boolean
) 