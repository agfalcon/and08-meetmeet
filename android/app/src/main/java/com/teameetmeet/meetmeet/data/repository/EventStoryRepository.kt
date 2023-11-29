package com.teameetmeet.meetmeet.data.repository

import com.teameetmeet.meetmeet.data.model.EventDetail
import com.teameetmeet.meetmeet.data.model.EventStory
import com.teameetmeet.meetmeet.data.network.api.EventStoryApi
import com.teameetmeet.meetmeet.data.network.entity.AddEventRequest
import com.teameetmeet.meetmeet.data.network.entity.KakaoLoginRequest
import com.teameetmeet.meetmeet.data.toException
import com.teameetmeet.meetmeet.presentation.model.EventColor
import com.teameetmeet.meetmeet.presentation.model.EventNotification
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class EventStoryRepository @Inject constructor(
    private val eventStoryApi: EventStoryApi
) {

    fun getEventStory(id: Int): Flow<EventStory> {
        return flowOf(true)
            .map {
                eventStoryApi.getStory(id.toString())
            }.catch {
                throw it.toException()
            }
    }

    fun getEventStoryDetail(id: Int): Flow<EventDetail> {
        return flowOf(Unit)
            .map {
                eventStoryApi.getStoryDetail(id.toString()).result
            }.catch {
                throw it.toException()
            }
        //TODO("이벤트 세부 정보 가져오고 로컬에 이벤트가 있으면 색과 알림 가져오기 아니면 DEFAULT 색 일정으로 파싱해서 내리기")
    }

    fun deleteEventStory(id: Int, isAll: Boolean = false): Flow<Unit> {
        return flowOf(true)
            .map {
                eventStoryApi.deleteEventStory(id.toString(), isAll)
            }.catch {
                throw it.toException()
            }
    }

    fun editEventStory(
        eventId: Int,
        isAll: Boolean = false,
        title: String,
        startDate: String,
        endDate: String,
        isJoinable: Boolean,
        isVisible: Boolean,
        memo: String,
        repeatTerm: String?,
        repeatFrequency: Int,
        repeatEndDate: String,
        color: EventColor,
        alarm: EventNotification
    ): Flow<Unit> {
        return flowOf(true)
            .map {
                eventStoryApi.editEventStory(
                    id = eventId.toString(),
                    isAll = isAll,
                    AddEventRequest(
                        title,
                        startDate,
                        endDate,
                        isJoinable,
                        isVisible,
                        memo.ifEmpty { null },
                        repeatTerm,
                        repeatFrequency,
                        repeatEndDate
                    )
                )
            }.catch {
                throw it.toException()
            }
    }

    fun editNotification(message: String): Flow<Unit> {
        return flowOf(true)
            .map {
                eventStoryApi.editNotification(KakaoLoginRequest(message))
            }.catch {
                throw it
            }
    }

}