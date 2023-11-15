package com.teameetmeet.meetmeet.data.repository

import com.teameetmeet.meetmeet.data.database.entity.Event
import com.teameetmeet.meetmeet.data.datasource.LocalCalendarDataSource
import com.teameetmeet.meetmeet.data.datasource.RemoteCalendarDataSource
import com.teameetmeet.meetmeet.data.network.entity.EventResponse
import com.teameetmeet.meetmeet.data.toEventDto
import com.teameetmeet.meetmeet.data.toLocalEventEntity
import kotlinx.coroutines.flow.Flow

class CalendarRepository(
    private val localCalendarDataSource: LocalCalendarDataSource,
    private val remoteCalendarDataSource: RemoteCalendarDataSource
) {
    suspend fun getEvents(startDate: String, endDate: String): Flow<List<Event>> {
        try {
            syncEvents(startDate, endDate)
        } catch (e: Exception) {
            println(e.message)
        } finally {
            return localCalendarDataSource.getEvents(startDate, endDate)
        }
    }

    suspend fun syncEvents(startDate: String, endDate: String) {
        //todo
    }

    private suspend fun syncInserts(localEvents: List<Event>, remoteEvents: List<EventResponse>) {
        remoteEvents
            .filter { remoteEvent ->
                localEvents.none { localEvent -> localEvent.id == remoteEvent.id }
            }
            .mapNotNull { remoteEvent ->
                remoteEvent.toEventDto()?.toLocalEventEntity()
            }
            .forEach { localEvent -> localCalendarDataSource.insert(localEvent) }
    }

    private suspend fun syncDeletes(localEvents: List<Event>, remoteEvents: List<EventResponse>) {
        localEvents
            .filter { localEvent ->
                remoteEvents.none { remoteEvent -> remoteEvent.id == localEvent.id }
            }
            .forEach { localEvent -> localCalendarDataSource.delete(localEvent) }
    }

    private suspend fun syncUpdates(localEvents: List<Event>, remoteEvents: List<EventResponse>) {
        remoteEvents
            .filter { remoteEvent ->
                localEvents.any { localEvent -> localEvent.id == remoteEvent.id }
            }
            .forEach { remoteEvent ->
                localCalendarDataSource.updateEventAttr(
                    id = remoteEvent.id,
                    title = remoteEvent.title,
                    startDate = remoteEvent.startDate,
                    endDate = remoteEvent.endDate,
                )
            }
    }
}