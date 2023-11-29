package com.teameetmeet.meetmeet.data.network.api

import com.teameetmeet.meetmeet.data.model.EventStory
import com.teameetmeet.meetmeet.data.network.entity.AddEventRequest
import com.teameetmeet.meetmeet.data.network.entity.EventStoryDetailResponse
import com.teameetmeet.meetmeet.data.network.entity.KakaoLoginRequest
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface EventStoryApi {

    @GET("/event/{event_id}/feeds")
    suspend fun getStory(@Path("event_id") id: String): EventStory

    @GET("event/{eventId}")
    suspend fun getStoryDetail(@Path("eventId") id: String): EventStoryDetailResponse

    @POST()
    suspend fun editNotification(@Body singleStringRequest: KakaoLoginRequest)

    @DELETE("event/{eventId}")
    suspend fun deleteEventStory(@Path("eventId") id: String, @Query("isAll") isAll: Boolean = false)

    @PATCH("event/{eventId}")
    suspend fun editEventStory(@Path("eventId") id: String, @Query("isAll") isAll: Boolean = false, @Body editEventRequest: AddEventRequest)
}