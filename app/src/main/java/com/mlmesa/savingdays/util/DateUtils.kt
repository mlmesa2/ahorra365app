package com.mlmesa.savingdays.util

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale

/**
 * Utility functions for date formatting and calculations
 */

object DateUtils {
    

    private val shortFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    private val mediumFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
    private val longFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG)
    
    /**
     * Format date as dd/MM/yyyy
     */
    fun formatShort(date: LocalDate): String {
        return date.format(shortFormatter)
    }
    
    /**
     * Format date in medium style
     */
    fun formatMedium(date: LocalDate): String {
        return date.format(mediumFormatter)
    }
    
    /**
     * Format date in long style
     */
    fun formatLong(date: LocalDate): String {
        return date.format(longFormatter)
    }
    
    /**
     * Get the day of year (1-365/366)
     */
    fun getDayOfYear(date: LocalDate): Int {
        return date.dayOfYear
    }
    
    /**
     * Check if a date is today
     */
    fun isToday(date: LocalDate): Boolean {
        return date == LocalDate.now()
    }
    
    /**
     * Check if a date is in the past
     */
    fun isPast(date: LocalDate): Boolean {
        return date.isBefore(LocalDate.now())
    }
    
    /**
     * Check if a date is in the future
     */
    fun isFuture(date: LocalDate): Boolean {
        return date.isAfter(LocalDate.now())
    }
    
    /**
     * Get the first day of the current month
     */
    fun getFirstDayOfMonth(year: Int, month: Int): LocalDate {
        return LocalDate.of(year, month, 1)
    }
    
    /**
     * Get the last day of the current month
     */
    fun getLastDayOfMonth(year: Int, month: Int): LocalDate {
        return getFirstDayOfMonth(year, month).plusMonths(1).minusDays(1)
    }
    
    /**
     * Get month name in Spanish
     */
    fun getMonthNameSpanish(month: Int): String {
        return when (month) {
            1 -> "Enero"
            2 -> "Febrero"
            3 -> "Marzo"
            4 -> "Abril"
            5 -> "Mayo"
            6 -> "Junio"
            7 -> "Julio"
            8 -> "Agosto"
            9 -> "Septiembre"
            10 -> "Octubre"
            11 -> "Noviembre"
            12 -> "Diciembre"
            else -> ""
        }
    }
}
