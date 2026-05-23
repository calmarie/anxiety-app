package com.example.calmy.domain.model

enum class AnxietyType(
    val apiValue: String,
    val displayName: String
) {
    Health("health", "здоровье"),
    Work("work", "работа"),
    Money("money", "деньги"),
    Partner("partner", "партнер"),
    Family("family", "семья"),
    Environment("environment", "окружение"),
    Education("education", "образование"),
    Other("other", "прочее");

    companion object {
        val selectableValues = entries

        fun fromApiValue(value: String): AnxietyType {
            return entries.firstOrNull { type -> type.apiValue == value } ?: Other
        }
    }
}
