package com.fmagnus.photostudio.data.model

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class Booking(
    val id: String = "",
    val userId: String = "",
    val serviceId: String = "",
    val serviceName: String = "", // Denormalized for easy display
    val locationId: String = "",
    val locationName: String = "", // Denormalized
    @ServerTimestamp
    val bookingTime: Date? = null,
    val appointmentTimestamp: Date? = null,
    val userName: String = "",
    val userPhone: String = ""
)
