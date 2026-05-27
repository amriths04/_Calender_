package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface CalendarEventDao {
    @Query("SELECT * FROM calendar_events WHERE date = :date ORDER BY id DESC")
    fun getEventsForDate(date: String): Flow<List<CalendarEvent>>

    @Query("SELECT * FROM calendar_events ORDER BY timestamp DESC")
    fun getAllEvents(): Flow<List<CalendarEvent>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvent(event: CalendarEvent)

    @Delete
    suspend fun deleteEvent(event: CalendarEvent)

    @Query("DELETE FROM calendar_events WHERE date = :date")
    suspend fun deleteEventsForDate(date: String)
}
