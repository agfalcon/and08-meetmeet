package com.teameetmeet.meetmeet.presentation.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teameetmeet.meetmeet.data.model.UserProfile
import com.teameetmeet.meetmeet.data.repository.UserRepository
import com.teameetmeet.meetmeet.presentation.model.CalendarViewMode
import com.teameetmeet.meetmeet.service.notification.NotificationHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val notificationHelper: NotificationHelper
) : ViewModel() {

    private val _userProfileImage = MutableStateFlow<String>("")
    val userProfileImage: StateFlow<String> = _userProfileImage

    private val _userNickName = MutableStateFlow<String>("")
    val userNickName: StateFlow<String> = _userNickName

    private val _calendarViewMode = MutableStateFlow<CalendarViewMode>(CalendarViewMode.MONTH)
    val calendarViewMode: StateFlow<CalendarViewMode> = _calendarViewMode

    private val _activeNotificationCount = MutableStateFlow<Int>(0)
    val activeNotificationCount: StateFlow<Int> = _activeNotificationCount

    fun setActiveNotificationCountFlow() {
        viewModelScope.launch {
            notificationHelper.activeNotificationCount
                .collectLatest { count ->
                    _activeNotificationCount.update { count }
                }
        }
    }

    fun fetchActiveNotificationCount() {
        viewModelScope.launch {
            notificationHelper.emitActiveNotificationCount()
        }
    }

    fun fetchUserProfile() {
        viewModelScope.launch {
            userRepository.getUserProfile().catch {
                updateUserProfile(UserProfile(null, "", ""))
            }.collect { userProfile ->
                updateUserProfile(userProfile)
            }
        }
    }

    private fun updateUserProfile(userProfile: UserProfile) {
        _userProfileImage.update { userProfile.profileImage.orEmpty() }
        _userNickName.update { userProfile.nickname }
    }

    fun changeViewMode(position: Int) {
        _calendarViewMode.update {
            when (position) {
                0 -> CalendarViewMode.MONTH
                1 -> CalendarViewMode.WEEK
                else -> CalendarViewMode.MONTH
            }
        }
    }
}