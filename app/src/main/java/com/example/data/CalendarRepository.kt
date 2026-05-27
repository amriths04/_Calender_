package com.example.data

import kotlinx.coroutines.flow.Flow

class CalendarRepository(private val dao: CalendarEventDao) {
    fun getEventsForDate(date: String): Flow<List<CalendarEvent>> = dao.getEventsForDate(date)
    
    val allEvents: Flow<List<CalendarEvent>> = dao.getAllEvents()

    suspend fun insert(event: CalendarEvent) = dao.insertEvent(event)

    suspend fun delete(event: CalendarEvent) = dao.deleteEvent(event)

    suspend fun deleteForDate(date: String) = dao.deleteEventsForDate(date)
}
