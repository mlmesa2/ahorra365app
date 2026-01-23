package com.mlmesa.savingdays.data.model

import com.mlmesa.savingdays.R
import java.util.Locale
import kotlin.math.roundToInt

/**
 * Enum representing currency scales for different countries.
 * The base amount (1-365) is multiplied by the factor to get the display amount.
 * Formula: max(1, round(base × factor))
 */
enum class CurrencyScale(
    val countryCode: String,
    val symbol: String,
    val displayName: Int,
    val factor: Double
) {
    MEXICO("MX", "MXN $", R.string.country_name_mexico, 1.0),
    USA("US", "$", R.string.country_name_united_sates, 0.1),
    EUROPE("EU", "€", R.string.country_name_europe, 0.1),
    COLOMBIA("CO", "COP $", R.string.country_name_colombia,200.0),
    ARGENTINA("AR", "ARS $", R.string.country_name_argentina, 50.0),
    CHILE("CL", "CLP $", R.string.country_name_chile, 50.0),
    BRAZIL("BR", "R$", R.string.country_name_brazil, 1.0),
    PERU("PE", "S/", R.string.country_name_peru, 1.0),
    UK("GB", "£", R.string.country_name_great_britain, 0.08),
    GENERIC("GENERIC", "$", R.string.country_name_generic, 1.0);


    /**
     * Calculate the display amount based on the base amount and factor.
     * Always returns at least 1 (no amounts below 1).
     * Always returns an integer (no decimals).
     */
    fun calculateAmount(baseAmount: Int): Int {
        return maxOf(1, (baseAmount * factor).roundToInt())
    }

    /**
     * Get the formatted amount string with currency symbol.
     */
    fun formatAmount(baseAmount: Int): String {
        val displayAmount = calculateAmount(baseAmount)
        return "$symbol$displayAmount"
    }

    companion object {
        /**
         * Get CurrencyScale from country code.
         * Returns MEXICO as default if not found.
         */
        fun fromCountryCode(code: String): CurrencyScale {
            return entries.find { it.countryCode == code } ?: GENERIC
        }

        /**
         * Get CurrencyScale from device locale.
         * Returns MEXICO as default if country is not supported.
         */
        fun fromLocale(locale: Locale = Locale.getDefault()): CurrencyScale {
            return when (locale.country) {
                "MX" -> MEXICO
                "US" -> USA
                "CO" -> COLOMBIA
                "AR" -> ARGENTINA
                "CL" -> CHILE
                "BR" -> BRAZIL
                "PE" -> PERU
                "GB" -> UK
                "ES", "DE", "FR", "IT", "PT", "NL", "BE", "AT", "IE" -> EUROPE
                else -> GENERIC // Default
            }
        }

        /**
         * Get all available scales for UI selection.
         */
        fun getAllScales(): List<CurrencyScale> = entries.toList()
    }
}
