package com.mrpark1.meparkpartner.util

import android.content.Context
import androidx.preference.PreferenceManager
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class SharedPrefUtil @Inject constructor(@ApplicationContext private val appContext: Context) {
    //SharedPreference 관련 유틸

    companion object {
        const val KEY_FIRST_LAUNCH = "first_launch"
        const val KEY_ACCESS_TOKEN = "access_token"
        const val KEY_USER_NAME = "user_name"
        const val KEY_SELECTED_PARKING_LOT = "selected_parking_lot"
    }

    private fun getSharedPreference() = PreferenceManager.getDefaultSharedPreferences(appContext)

    fun put(key: String, value: Any) {
        val editor = getSharedPreference().edit()
        when (value) {
            is Boolean -> editor.putBoolean(key, value)
            is String -> editor.putString(key, value)
            else -> throw TypeCastException("Type unavailable.")
        }
        editor.apply()
    }

    fun getString(key: String, default: String?): String? {
        return getSharedPreference().getString(key, default)
    }

    fun getBoolean(key: String, default: Boolean): Boolean {
        return getSharedPreference().getBoolean(key, default)
    }

}