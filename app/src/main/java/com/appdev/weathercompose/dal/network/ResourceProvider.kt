package com.appdev.weathercompose.dal.network

import android.content.Context
import androidx.annotation.StringRes
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ResourceProvider @Inject constructor(@ApplicationContext private val appContext: Context) {
    fun getString(@StringRes id: Int): String {
        return appContext.getString(id)
    }
}
