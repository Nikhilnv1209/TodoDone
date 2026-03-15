package com.example.tododone.domain.model

import com.example.tododone.data.local.serializer.InstantSerializer
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import java.time.DayOfWeek

enum class Frequency {
    DAILY,
    WEEKDAYS,
    WEEKLY,
    BIWEEKLY,
    MONTHLY,
    CUSTOM
}

@Serializable
data class RecurrenceRule(
    val frequency: Frequency,
    val interval: Int,
    val daysOfWeek: List<DayOfWeek>?,
    @Serializable(with = InstantSerializer::class)
    val endDate: Instant?,
    val occurrenceCount: Int?
)