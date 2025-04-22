// Create a new file, e.g., TestViewModel.kt
package com.unswipe.android.ui.dashboard // Or another UI package

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TestViewModel @Inject constructor() : ViewModel() {
    // Empty for now
}