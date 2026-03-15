package com.example.tododone.data.local.database

import androidx.room.TypeConverter
import com.example.tododone.domain.model.RecurrenceRule
import com.example.tododone.domain.model.Reminder
import kotlinx.datetime.Instant
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.time.LocalTime

class Converters {
    private val json = Json { ignoreUnknownKeys = true }

    @TypeConverter
    fun fromTimestamp(value: Long?): Instant? {
        return value?.let { Instant.fromEpochMilliseconds(it) }
    }

    @TypeConverter
    fun toTimestamp(instant: Instant?): Long? {
        return instant?.toEpochMilliseconds()
    }

    @TypeConverter
    fun fromLocalTime(value: String?): LocalTime? {
        return value?.let { LocalTime.parse(it) }
    }

    @TypeConverter
    fun toLocalTime(time: LocalTime?): String? {
        return time?.toString()
    }

    @TypeConverter
    fun fromStringList(value: String): List<String> {
        return if (value.isBlank()) emptyList()
        else json.decodeFromString(value)
    }

    @TypeConverter
    fun toStringList(list: List<String>): String {
        return json.encodeToString(list)
    }

    @TypeConverter
    fun fromReminder(value: String?): Reminder? {
        return value?.let { json.decodeFromString(it) }
    }

    @TypeConverter
    fun toReminder(reminder: Reminder?): String? {
        return reminder?.let { json.encodeToString(it) }
    }

    @TypeConverter
    fun fromRecurrenceRule(value: String?): RecurrenceRule? {
        return value?.let { json.decodeFromString(it) }
    }

    @TypeConverter
    fun toRecurrenceRule(rule: RecurrenceRule?): String? {
        return rule?.let { json.encodeToString(it) }
    }
}