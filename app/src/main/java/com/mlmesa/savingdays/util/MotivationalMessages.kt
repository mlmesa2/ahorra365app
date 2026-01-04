package com.mlmesa.savingdays.util

/**
 * Collection of motivational messages to display to users
 */
object MotivationalMessages {
    
    private val messages = listOf(
        "¡Cada peso cuenta! 💰",
        "¡Vas por buen camino! 🌟",
        "El ahorro es el camino a la libertad financiera 🚀",
        "¡Hoy es un gran día para ahorrar! ☀️",
        "Pequeños pasos, grandes logros 👣",
        "¡Tu yo del futuro te lo agradecerá! 🙏",
        "La constancia es la clave del éxito 🔑",
        "¡Sigue así, campeón! 🏆",
        "Cada día es una nueva oportunidad 🌈",
        "¡Tu esfuerzo vale la pena! 💪",
        "El ahorro de hoy es la seguridad de mañana 🛡️",
        "¡Estás construyendo tu futuro! 🏗️",
        "La disciplina te llevará lejos 🎯",
        "¡No te rindas, ya casi llegas! 🎉",
        "Tus metas están más cerca de lo que crees 🎁",
        "¡Eres más fuerte de lo que piensas! 💎",
        "El éxito es la suma de pequeños esfuerzos 📈",
        "¡Hoy es tu día de brillar! ✨",
        "La perseverancia siempre gana 🥇",
        "¡Confía en el proceso! 🌱",
        "Cada ahorro te acerca a tus sueños 💭",
        "¡Tú puedes con esto y más! 🦸",
        "El cambio comienza con una decisión 🔄",
        "¡Sigue adelante, no mires atrás! 👀",
        "Tu determinación es admirable 🌟",
        "¡Hoy es el día perfecto para avanzar! 📅",
        "La consistencia es tu superpoder 🦸‍♀️",
        "¡Estás más cerca de tu meta! 🎯",
        "Cada día cuenta, cada peso importa 💵",
        "¡Eres un ejemplo de disciplina! 👏"
    )
    
    /**
     * Get a random motivational message
     */
    fun getRandom(): String {
        return messages.random()
    }
    
    /**
     * Get a specific message by index
     */
    fun get(index: Int): String {
        return messages[index % messages.size]
    }
    
    /**
     * Get all messages
     */
    fun getAll(): List<String> = messages
}
