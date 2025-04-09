package com.unswipe.android.data.model

// This might not need to be a separate model if only storing package names in a Set
// But could be useful if you add more info later (e.g., custom limits per app)
data class AppBlockInfo(
    val packageName: String,
    val isBlocked: Boolean = true // default to blocked when added
) 