package com.encore.music.presentation.utils

import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavController

fun <T> NavController.setNavigationResult(
    key: String,
    result: T,
) = previousBackStackEntry?.savedStateHandle?.set(key, result)

fun <T> NavController.getNavigationResult(
    owner: LifecycleOwner,
    key: String,
    onResult: (result: T) -> Unit,
) {
    val savedStateHandle = (currentBackStackEntry ?: return).savedStateHandle
    savedStateHandle.getLiveData<T>(key).observe(owner) { value ->
        value?.also {
            onResult(it)
            savedStateHandle.remove<T>(key)
        }
    }
}
