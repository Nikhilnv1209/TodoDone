package com.example.tododone.domain.model

import com.example.tododone.data.local.serializer.InstantSerializer
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

enum class ReminderType {
    RELATIVE,
    ABSOLUTE
}

@Serializable
data class Reminder(
    val type: ReminderType,
    val offsetMinutes: Int?,
    @Serializable(with = InstantSerializer::class)
    val absoluteTime: Instant?
)