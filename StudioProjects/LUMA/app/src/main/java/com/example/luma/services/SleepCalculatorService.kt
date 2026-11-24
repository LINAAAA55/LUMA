package com.example.luma.services

import java.util.Calendar

data class SleepSuggestion(
    val timeFormatted: String,
    val time24Formatted: String,
    val hour: Int,
    val minute: Int,
    val cycleCount: Int,
    val durationMinutes: Int,
    val durationFormatted: String,
    val isRecommended: Boolean,
    val description: String
)

class SleepCalculatorService {

    fun calculateWakeUpTimes(bedHour: Int, bedMinute: Int, sleepLatencyMinutes: Int): List<SleepSuggestion> {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, bedHour)
            set(Calendar.MINUTE, bedMinute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            add(Calendar.MINUTE, sleepLatencyMinutes)
        }

        val suggestions = mutableListOf<SleepSuggestion>()
        val cycleCounts = listOf(3, 4, 5, 6)

        for (cycles in cycleCounts) {
            val durationMins = cycles * 90
            val targetCalendar = calendar.clone() as Calendar
            targetCalendar.add(Calendar.MINUTE, durationMins)

            val hour24 = targetCalendar.get(Calendar.HOUR_OF_DAY)
            val minute = targetCalendar.get(Calendar.MINUTE)
            val amPm = if (hour24 < 12) "AM" else "PM"
            val hour12 = when {
                hour24 == 0 -> 12
                hour24 > 12 -> hour24 - 12
                else -> hour24
            }

            val timeFormatted = String.format("%02d:%02d %s", hour12, minute, amPm)
            val time24Formatted = String.format("%02d:%02d", hour24, minute)
            val hours = durationMins / 60
            val mins = durationMins % 60
            val durationFormatted = if (mins > 0) "${hours}h ${mins}m" else "${hours}h"

            val isRecommended = cycles == 5 || cycles == 6
            val desc = when (cycles) {
                3 -> "$cycles Sleep Cycles ($durationFormatted) - Short rest"
                4 -> "$cycles Sleep Cycles ($durationFormatted)"
                5 -> "$cycles Sleep Cycles ($durationFormatted) - Good rest"
                6 -> "$cycles Sleep Cycles ($durationFormatted) - Recommended (Optimal)"
                else -> "$cycles Sleep Cycles ($durationFormatted)"
            }

            suggestions.add(
                SleepSuggestion(
                    timeFormatted = timeFormatted,
                    time24Formatted = time24Formatted,
                    hour = hour24,
                    minute = minute,
                    cycleCount = cycles,
                    durationMinutes = durationMins,
                    durationFormatted = durationFormatted,
                    isRecommended = isRecommended,
                    description = desc
                )
            )
        }

        return suggestions
    }

    fun calculateBedtimes(wakeHour: Int, wakeMinute: Int, sleepLatencyMinutes: Int): List<SleepSuggestion> {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, wakeHour)
            set(Calendar.MINUTE, wakeMinute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        val suggestions = mutableListOf<SleepSuggestion>()
        val cycleCounts = listOf(6, 5, 4, 3)

        for (cycles in cycleCounts) {
            val durationMins = cycles * 90
            val targetCalendar = calendar.clone() as Calendar
            targetCalendar.add(Calendar.MINUTE, -(durationMins + sleepLatencyMinutes))

            val hour24 = targetCalendar.get(Calendar.HOUR_OF_DAY)
            val minute = targetCalendar.get(Calendar.MINUTE)
            val amPm = if (hour24 < 12) "AM" else "PM"
            val hour12 = when {
                hour24 == 0 -> 12
                hour24 > 12 -> hour24 - 12
                else -> hour24
            }

            val timeFormatted = String.format("%02d:%02d %s", hour12, minute, amPm)
            val time24Formatted = String.format("%02d:%02d", hour24, minute)
            val hours = durationMins / 60
            val mins = durationMins % 60
            val durationFormatted = if (mins > 0) "${hours}h ${mins}m" else "${hours}h"

            val isRecommended = cycles == 5 || cycles == 6
            val desc = when (cycles) {
                6 -> "$cycles Sleep Cycles ($durationFormatted) - Recommended (9 hours)"
                5 -> "$cycles Sleep Cycles ($durationFormatted) - Recommended (7.5 hours)"
                4 -> "$cycles Sleep Cycles ($durationFormatted) - 6 hours"
                else -> "$cycles Sleep Cycles ($durationFormatted)"
            }

            suggestions.add(
                SleepSuggestion(
                    timeFormatted = timeFormatted,
                    time24Formatted = time24Formatted,
                    hour = hour24,
                    minute = minute,
                    cycleCount = cycles,
                    durationMinutes = durationMins,
                    durationFormatted = durationFormatted,
                    isRecommended = isRecommended,
                    description = desc
                )
            )
        }

        return suggestions
    }
}
