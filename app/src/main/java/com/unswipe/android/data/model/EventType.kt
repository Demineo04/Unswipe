package com.unswipe.android.data.model // Or your chosen package

enum class EventType {
    DEVICE_UNLOCKED,
    SCREEN_ON,
    SCREEN_OFF,
    APP_USAGE_START,
    APP_USAGE_STOP,
    ACCESSIBILITY_EVENT,

    // --- Add these two lines ---
    SCREEN_UNLOCK,
    SWIPE
    // --------------------------

    // Add any other event types your application needs here
}