package com.ks.langapp.ui.utils

import android.content.ContentResolver
import android.content.Context

class ResourceProvider(private val context: Context) {

    fun getString(resId: Int): String {
        return context.getString(resId)
    }

    fun getString(resId: Int, value: String?): String {
        return context.getString(resId, value)
    }

    fun getContentResolver(): ContentResolver {
        return context.contentResolver
    }
}