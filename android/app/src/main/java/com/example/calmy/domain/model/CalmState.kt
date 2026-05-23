package com.example.calmy.domain.model

enum class CalmState(
    val cloudLevel: Int
) {
    VeryCalm(1),
    Calm(2),
    Anxious(3),
    VeryAnxious(4),
    Overwhelmed(5);

    companion object {
        fun fromAverageAnxiety(value: Double): CalmState {
            return when {
                value <= 2.0 -> VeryCalm
                value <= 4.0 -> Calm
                value <= 6.0 -> Anxious
                value <= 8.0 -> VeryAnxious
                else -> Overwhelmed
            }
        }

        fun fromCloudLevel(level: Int): CalmState {
            return entries.firstOrNull { state -> state.cloudLevel == level.coerceIn(1, 5) } ?: Calm
        }
    }
}
