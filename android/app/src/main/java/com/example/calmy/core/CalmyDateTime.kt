package com.example.calmy.core

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

object CalmyDateTime {
    private val RussianLocale = Locale("ru", "RU")
    private val Utc = TimeZone.getTimeZone("UTC")
    private val MonthNames = listOf(
        "январь",
        "февраль",
        "март",
        "апрель",
        "май",
        "июнь",
        "июль",
        "август",
        "сентябрь",
        "октябрь",
        "ноябрь",
        "декабрь"
    )

    fun nowIso(timeMillis: Long = System.currentTimeMillis()): String {
        return formatter("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US, Utc).format(Date(timeMillis))
    }

    fun parse(value: String): Date? {
        if (value.isBlank()) {
            return null
        }
        val normalizedValue = normalizeFractionalSeconds(value)

        return ParsePatterns.firstNotNullOfOrNull { pattern ->
            runCatching {
                val timeZone = if (pattern.endsWith("'Z'")) Utc else TimeZone.getDefault()
                formatter(pattern, Locale.US, timeZone).parse(normalizedValue)
            }.getOrNull()
        }
    }

    fun formatThoughtDate(value: String): String {
        val date = parse(value) ?: return value
        return "${formatter("HH:mm", RussianLocale).format(date)}, ${formatter("d MMMM yyyy 'года'", RussianLocale).format(date)}"
    }

    fun formatMonthFolder(date: Date): String {
        val calendar = calendar(date)
        val month = MonthNames[calendar.get(Calendar.MONTH)]
        val year = calendar.get(Calendar.YEAR)
        return "${month.replaceFirstChar { char -> char.uppercase(RussianLocale) }} $year"
    }

    fun monthKey(date: Date): String {
        return formatter("yyyy-MM", Locale.US).format(date)
    }

    fun weekStart(date: Date): Date {
        val calendar = calendar(date)
        calendar.firstDayOfWeek = Calendar.MONDAY
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        while (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
            calendar.add(Calendar.DAY_OF_MONTH, -1)
        }
        return calendar.time
    }

    fun weekKey(date: Date): String {
        return formatter("yyyy-MM-dd", Locale.US).format(weekStart(date))
    }

    fun formatWeekFolder(weekStart: Date): String {
        val start = calendar(weekStart)
        val end = calendar(weekStart).apply {
            add(Calendar.DAY_OF_MONTH, 6)
        }
        return formatDayRange(start, end)
    }

    fun dateKey(date: Date): String {
        return formatter("yyyy-MM-dd", Locale.US).format(date)
    }

    fun hourOfDay(date: Date): Int {
        return calendar(date).get(Calendar.HOUR_OF_DAY)
    }

    fun daysAgoStart(days: Int, now: Date = Date()): Date {
        return calendar(now).apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            add(Calendar.DAY_OF_MONTH, -(days - 1))
        }.time
    }

    private fun formatDayRange(start: Calendar, end: Calendar): String {
        val startDay = start.get(Calendar.DAY_OF_MONTH)
        val endDay = end.get(Calendar.DAY_OF_MONTH)
        val startMonth = MonthNames[start.get(Calendar.MONTH)]
        val endMonth = MonthNames[end.get(Calendar.MONTH)]
        val startYear = start.get(Calendar.YEAR)
        val endYear = end.get(Calendar.YEAR)

        return when {
            startYear != endYear -> "$startDay $startMonth $startYear - $endDay $endMonth $endYear"
            start.get(Calendar.MONTH) != end.get(Calendar.MONTH) -> "$startDay $startMonth - $endDay $endMonth $endYear"
            else -> "$startDay-$endDay $endMonth $endYear"
        }
    }

    private fun calendar(date: Date): Calendar {
        return Calendar.getInstance().apply {
            firstDayOfWeek = Calendar.MONDAY
            time = date
        }
    }

    private fun formatter(
        pattern: String,
        locale: Locale,
        timeZone: TimeZone = TimeZone.getDefault()
    ): SimpleDateFormat {
        return SimpleDateFormat(pattern, locale).apply {
            this.timeZone = timeZone
            isLenient = false
        }
    }

    private fun normalizeFractionalSeconds(value: String): String {
        return FractionRegex.replace(value) { match ->
            val fraction = match.groupValues[1].padEnd(3, '0').take(3)
            ".$fraction${match.groupValues[2]}"
        }
    }

    private val ParsePatterns = listOf(
        "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
        "yyyy-MM-dd'T'HH:mm:ss'Z'",
        "yyyy-MM-dd'T'HH:mm:ss.SSSXXX",
        "yyyy-MM-dd'T'HH:mm:ssXXX",
        "yyyy-MM-dd"
    )
    private val FractionRegex = Regex("\\.(\\d+)(Z|[+-]\\d{2}:\\d{2})$")
}
