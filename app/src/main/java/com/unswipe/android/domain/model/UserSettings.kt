package com.unswipe.android.domain.model

import java.util.concurrent.TimeUnit

data class UserSettings(
    val dailyUsageLimitMillis: Long,
    val currentStreak: Int,
    val isPremium: Boolean,
    val blockedApps: Set<String> // Set of package names
) {
    companion object {
        // Define a default state
        val Defaults = UserSettings(
            dailyUsageLimitMillis = TimeUnit.HOURS.toMillis(3), // Default 3 hours
            currentStreak = 0,
            isPremium = false,
            blockedApps = setOf( // Default common apps - user can customize
                 "com.zhiliaoapp.musically", // TikTok
                 "com.instagram.android",
                 "com.google.android.youtube",
                 "com.facebook.katana",
                 "com.snapchat.android",
                 "com.linkedin.android",
                 "com.twitter.android"
             )
        )
    }
} 