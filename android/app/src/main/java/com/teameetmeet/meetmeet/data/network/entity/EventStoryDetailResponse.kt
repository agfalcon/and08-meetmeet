package com.teameetmeet.meetmeet.data.network.entity

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.teameetmeet.meetmeet.data.model.EventDetail

@JsonClass(generateAdapter = true)
data class EventStoryDetailResponse(
    @Json(name = "result")
    val result: EventDetail
)
