package com.example.birthdaycountdown

import java.time.Duration
import java.time.LocalDate
import java.time.Year
import java.time.ZonedDateTime

object BirthdayCalculator {

    data class Countdown(
        val days: Long,
        val hours: Long,
        val minutes: Long,
        val nextBirthday: LocalDate
    )

    fun nextBirthday(birthDate: LocalDate, reference: LocalDate = LocalDate.now()): LocalDate {
        var candidate = occurrenceInYear(birthDate, reference.year)
        if (candidate.isBefore(reference)) {
            candidate = occurrenceInYear(birthDate, reference.year + 1)
        }
        return candidate
    }

    private fun occurrenceInYear(birthDate: LocalDate, year: Int): LocalDate {
        val day = if (birthDate.monthValue == 2 && birthDate.dayOfMonth == 29 && !Year.isLeap(year.toLong())) {
            28
        } else {
            birthDate.dayOfMonth
        }
        return LocalDate.of(year, birthDate.monthValue, day)
    }

    fun countdown(birthDate: LocalDate, now: ZonedDateTime = ZonedDateTime.now()): Countdown {
        val next = nextBirthday(birthDate, now.toLocalDate())
        if (next == now.toLocalDate()) {
            return Countdown(days = 0, hours = 0, minutes = 0, nextBirthday = next)
        }
        val target = next.atStartOfDay(now.zone)
        val total = Duration.between(now, target)
        return Countdown(
            days = total.toDays(),
            hours = total.toHoursPart().toLong(),
            minutes = total.toMinutesPart().toLong(),
            nextBirthday = next
        )
    }
}
