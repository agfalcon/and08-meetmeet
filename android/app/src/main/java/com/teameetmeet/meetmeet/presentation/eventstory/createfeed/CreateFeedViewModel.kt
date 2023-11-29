package com.teameetmeet.meetmeet.presentation.eventstory.createfeed

import androidx.lifecycle.ViewModel
import com.teameetmeet.meetmeet.R
import com.teameetmeet.meetmeet.data.repository.EventStoryRepository
import com.teameetmeet.meetmeet.presentation.model.MediaItem
import com.teameetmeet.meetmeet.util.toAbsolutePath
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import java.io.File
import javax.inject.Inject

@HiltViewModel
class CreateFeedViewModel @Inject constructor(
    private val eventStoryRepository: EventStoryRepository
) : ViewModel(), MediaItemCancelClickListener {
    private val _feedText = MutableStateFlow<String>("")
    val feedText: StateFlow<String> = _feedText

    private val _mediaList = MutableStateFlow<List<MediaItem>>(listOf())
    val mediaList: StateFlow<List<MediaItem>> = _mediaList

    private val _createFeedUiEvent = MutableSharedFlow<CreateFeedUiEvent>()
    val createFeedUiEvent: SharedFlow<CreateFeedUiEvent> = _createFeedUiEvent

    fun setFeedText(text: CharSequence) {
        _feedText.update { text.toString() }
    }

    fun selectMedia(uris: List<MediaItem>) {
        _mediaList.update { (it + uris).distinct() }
    }

    override fun onItemClick(mediaItem: MediaItem) {
        _mediaList.update { it.filter { item -> item != mediaItem } }
    }

    fun onSave() {
        val mediaSizeConstraint = 1024 * 1024 * 50
        if (mediaList.value.size > 10) {
            _createFeedUiEvent.tryEmit(
                CreateFeedUiEvent.ShowMessage(R.string.create_feed_constraint_amount)
            )
        } else if (mediaList.value.sumOf { it.size } > mediaSizeConstraint) {
            _createFeedUiEvent.tryEmit(
                CreateFeedUiEvent.ShowMessage(R.string.create_feed_media_constraint_size)
            )
        } else {
            mediaList.value
                .mapNotNull { it.uri.toAbsolutePath() }
                .map { File(it) }
                .let { eventStoryRepository.createFeed(0, feedText.value, it) }
                .catch {
                    //todo: 예외처리
                }
        }
    }
}