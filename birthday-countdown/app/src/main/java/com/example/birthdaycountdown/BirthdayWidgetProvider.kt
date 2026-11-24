package com.example.birthdaycountdown

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.RemoteViews
import java.time.format.DateTimeFormatter
import java.util.Locale

class BirthdayWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        appWidgetIds.forEach { id -> updateWidget(context, appWidgetManager, id) }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        when (intent.action) {
            Intent.ACTION_TIME_TICK,
            Intent.ACTION_TIMEZONE_CHANGED,
            Intent.ACTION_DATE_CHANGED,
            Intent.ACTION_LOCALE_CHANGED,
            Intent.ACTION_BOOT_COMPLETED,
            ACTION_REFRESH -> refreshAll(context)
        }
    }

    private fun updateWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
        val views = RemoteViews(context.packageName, R.layout.widget_birthday)
        val birthDate = BirthdayPrefs.load(context)

        if (birthDate == null) {
            views.setTextViewText(R.id.widget_countdown, context.getString(R.string.widget_not_configured))
            views.setTextViewText(R.id.widget_date, "")
        } else {
            val countdown = BirthdayCalculator.countdown(birthDate)
            if (countdown.days == 0L) {
                views.setTextViewText(R.id.widget_countdown, context.getString(R.string.widget_today))
            } else {
                val text = context.resources.getQuantityString(
                    R.plurals.widget_countdown,
                    countdown.days.toInt(),
                    countdown.days,
                    countdown.hours,
                    countdown.minutes
                )
                views.setTextViewText(R.id.widget_countdown, text)
            }
            val dateText = context.getString(
                R.string.widget_next_birthday,
                countdown.nextBirthday.format(DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.getDefault()))
            )
            views.setTextViewText(R.id.widget_date, dateText)
        }

        val openIntent = Intent(context, MainActivity::class.java)
        val flags = PendingIntent.FLAG_UPDATE_CURRENT or
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) PendingIntent.FLAG_IMMUTABLE else 0
        views.setOnClickPendingIntent(
            R.id.widget_root,
            PendingIntent.getActivity(context, 0, openIntent, flags)
        )

        appWidgetManager.updateAppWidget(appWidgetId, views)
    }

    companion object {
        const val ACTION_REFRESH = "com.example.birthdaycountdown.ACTION_REFRESH"

        fun refreshAll(context: Context) {
            val manager = AppWidgetManager.getInstance(context)
            val ids = manager.getAppWidgetIds(ComponentName(context, BirthdayWidgetProvider::class.java))
            if (ids.isNotEmpty()) {
                val intent = Intent(context, BirthdayWidgetProvider::class.java).apply {
                    action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
                    putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
                }
                context.sendBroadcast(intent)
            }
        }
    }
}
