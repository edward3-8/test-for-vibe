package com.example.birthdaycountdown

import android.app.Activity
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

class MainActivity : Activity() {

    private lateinit var selectedDateText: TextView
    private lateinit var previewText: TextView
    private var selectedDate: LocalDate? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        selectedDateText = findViewById(R.id.selected_date)
        previewText = findViewById(R.id.preview)
        val chooseButton: Button = findViewById(R.id.button_choose_date)
        val saveButton: Button = findViewById(R.id.button_save)
        val pinButton: Button = findViewById(R.id.button_add_widget)

        selectedDate = BirthdayPrefs.load(this)
        renderSelectedDate()
        renderPreview()

        chooseButton.setOnClickListener { showDatePicker() }
        saveButton.setOnClickListener { saveAndRefresh() }
        pinButton.setOnClickListener { pinWidget() }
    }

    private fun showDatePicker() {
        val today = LocalDate.now()
        val initial = selectedDate ?: LocalDate.of(today.year - 25, today.monthValue, today.dayOfMonth)
        DatePickerDialog(
            this,
            { _, year, month, day ->
                selectedDate = LocalDate.of(year, month + 1, day)
                renderSelectedDate()
                renderPreview()
            },
            initial.year,
            initial.monthValue - 1,
            initial.dayOfMonth
        ).apply {
            datePicker.maxDate = System.currentTimeMillis()
            show()
        }
    }

    private fun saveAndRefresh() {
        val date = selectedDate
        if (date == null) {
            Toast.makeText(this, R.string.error_no_date, Toast.LENGTH_SHORT).show()
            return
        }
        BirthdayPrefs.save(this, date)
        BirthdayWidgetProvider.refreshAll(this)
        renderPreview()
        Toast.makeText(this, R.string.saved, Toast.LENGTH_SHORT).show()
    }

    private fun pinWidget() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            Toast.makeText(this, R.string.pin_not_supported, Toast.LENGTH_SHORT).show()
            return
        }
        val manager = AppWidgetManager.getInstance(this)
        if (!manager.isRequestPinAppWidgetSupported) {
            Toast.makeText(this, R.string.pin_not_supported, Toast.LENGTH_SHORT).show()
            return
        }
        val flags = PendingIntent.FLAG_UPDATE_CURRENT or
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) PendingIntent.FLAG_IMMUTABLE else 0
        val callback = PendingIntent.getBroadcast(
            this,
            0,
            Intent(this, BirthdayWidgetProvider::class.java).setAction(BirthdayWidgetProvider.ACTION_REFRESH),
            flags
        )
        manager.requestPinAppWidget(ComponentName(this, BirthdayWidgetProvider::class.java), null, callback)
    }

    private fun renderSelectedDate() {
        val date = selectedDate
        selectedDateText.text = if (date != null) {
            getString(R.string.selected_date, formatDate(date))
        } else {
            getString(R.string.no_date_selected)
        }
    }

    private fun renderPreview() {
        val date = selectedDate ?: run {
            previewText.text = getString(R.string.preview_empty)
            return
        }
        val countdown = BirthdayCalculator.countdown(date)
        previewText.text = if (countdown.days == 0L) {
            getString(R.string.preview_today, formatDate(countdown.nextBirthday))
        } else {
            getString(
                R.string.preview_countdown,
                countdown.days,
                countdown.hours,
                countdown.minutes,
                formatDate(countdown.nextBirthday)
            )
        }
    }

    private fun formatDate(date: LocalDate): String =
        date.format(DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.getDefault()))
}
