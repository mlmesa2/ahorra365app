package com.mlmesa.savingdays.util

import com.mlmesa.savingdays.R

/**
 * Collection of motivational messages to display to users
 */
object MotivationalMessages {
    
    private val messages = listOf(
        R.string.motivational_message_1,
        R.string.motivational_message_2,
        R.string.motivational_message_3,
        R.string.motivational_message_4,
        R.string.motivational_message_5,
        R.string.motivational_message_6,
        R.string.motivational_message_7,
        R.string.motivational_message_8,
        R.string.motivational_message_9,
        R.string.motivational_message_10,
        R.string.motivational_message_11,
        R.string.motivational_message_12,
        R.string.motivational_message_13,
        R.string.motivational_message_14,
        R.string.motivational_message_15,
        R.string.motivational_message_16,
        R.string.motivational_message_17,
        R.string.motivational_message_18,
        R.string.motivational_message_19,
        R.string.motivational_message_20,
        R.string.motivational_message_21,
        R.string.motivational_message_22,
        R.string.motivational_message_23,
        R.string.motivational_message_24,
        R.string.motivational_message_25,
        R.string.motivational_message_26,
        R.string.motivational_message_27,
        R.string.motivational_message_28,
        R.string.motivational_message_29,
        R.string.motivational_message_30
    )
    
    /**
     * Get a random motivational message resource ID
     */
    fun getRandom(): Int {
        return messages.random()
    }
    
    /**
     * Get a specific message resource ID by index
     */
    fun get(index: Int): Int {
        return messages[index % messages.size]
    }
    
    /**
     * Get all message resource IDs
     */
    fun getAll(): List<Int> = messages
}
