package com.example.birthdaycountdown

import android.content.Context
import java.time.LocalDate

object BirthdayPrefs {

    private const val FILE_NAME = "birthday_prefs"
    private const val KEY_IS_SET = "is_set"
    private const val KEY_DAY = "day"
    private const val KEY_MONTH = "month"
    private const val KEY_YEAR = "year"

    fun save(context: Context, date: LocalDate) {
        context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(KEY_IS_SET, true)
            .putInt(KEY_DAY, date.dayOfMonth)
            .putInt(KEY_MONTH, date.monthValue)
            .putInt(KEY_YEAR, date.year)
            .apply()
    }

    fun load(context: Context): LocalDate? {
        val prefs = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)
        if (!prefs.getBoolean(KEY_IS_SET, false)) return null
        return try {
            LocalDate.of(
                prefs.getInt(KEY_YEAR, 2000),
                prefs.getInt(KEY_MONTH, 1),
                prefs.getInt(KEY_DAY, 1)
            )
        } catch (e: Exception) {
            null
        }
    }
}
