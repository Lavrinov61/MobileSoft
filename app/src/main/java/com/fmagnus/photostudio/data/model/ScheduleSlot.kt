package com.fmagnus.photostudio.data.model

import java.util.Date

data class ScheduleSlot(
    val id: String = "",
    val serviceId: String = "",
    val locationId: String = "",
    val startTime: Date? = null,
    val endTime: Date? = null,
    val isBooked: Boolean = false
)
