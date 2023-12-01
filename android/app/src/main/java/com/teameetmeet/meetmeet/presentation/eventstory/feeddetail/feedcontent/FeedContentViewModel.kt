package com.teameetmeet.meetmeet.presentation.eventstory.feeddetail.feedcontent

import androidx.lifecycle.ViewModel
import com.teameetmeet.meetmeet.data.model.Content
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class FeedContentViewModel : ViewModel() {

    private val _contents = MutableStateFlow<List<Content>>(emptyList())
    val contents: StateFlow<List<Content>> = _contents

    private val _isTouched = MutableStateFlow<Boolean>(false)
    val isTouched: StateFlow<Boolean> = _isTouched

    private val _currentIndex = MutableStateFlow<Int>(0)
    val currentIndex: StateFlow<Int> = _currentIndex


    fun fetchContents(content: Array<Content>, index: Int) {
        _contents.update {
            content.toList()
        }
        _currentIndex.update {
            index
        }
    }
}