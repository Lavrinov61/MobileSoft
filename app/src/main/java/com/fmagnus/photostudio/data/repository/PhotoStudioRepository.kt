package com.fmagnus.photostudio.data.repository

import com.fmagnus.photostudio.data.model.PhotoService
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class PhotoStudioRepository {

    private val db = FirebaseFirestore.getInstance()

    suspend fun getPhotoServices(): List<PhotoService> {
        return try {
            db.collection("services")
                .get()
                .await()
                .toObjects(PhotoService::class.java)
        } catch (e: Exception) {
            // In a real app, handle this error more gracefully
            emptyList()
        }
    }

    suspend fun getScheduleSlots(serviceId: String, date: java.util.Date): List<com.fmagnus.photostudio.data.model.ScheduleSlot> {
        val startOfDay = java.util.Calendar.getInstance().apply { time = date; set(java.util.Calendar.HOUR_OF_DAY, 0); set(java.util.Calendar.MINUTE, 0); set(java.util.Calendar.SECOND, 0) }.time
        val endOfDay = java.util.Calendar.getInstance().apply { time = date; set(java.util.Calendar.HOUR_OF_DAY, 23); set(java.util.Calendar.MINUTE, 59); set(java.util.Calendar.SECOND, 59) }.time

        return try {
            db.collection("schedule_slots")
                .whereEqualTo("serviceId", serviceId)
                .whereGreaterThanOrEqualTo("startTime", startOfDay)
                .whereLessThanOrEqualTo("startTime", endOfDay)
                .orderBy("startTime")
                .get()
                .await()
                .toObjects(com.fmagnus.photostudio.data.model.ScheduleSlot::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun createBooking(
        userId: String,
        serviceId: String,
        slotId: String,
        userName: String,
        userPhone: String
    ): Boolean {
        val slotRef = db.collection("schedule_slots").document(slotId)
        val bookingRef = db.collection("bookings").document()

        return try {
            db.runTransaction { transaction ->
                val slotSnapshot = transaction.get(slotRef)
                if (slotSnapshot.getBoolean("isBooked") == true) {
                    throw Exception("Slot is already booked.")
                }

                // TODO: We should probably fetch service/location details to denormalize them here.
                // For now, we'll just use IDs.

                val newBooking = com.fmagnus.photostudio.data.model.Booking(
                    id = bookingRef.id,
                    userId = userId,
                    serviceId = serviceId,
                    appointmentTimestamp = slotSnapshot.getDate("startTime"),
                    userName = userName,
                    userPhone = userPhone
                )

                transaction.set(bookingRef, newBooking)
                transaction.update(slotRef, "isBooked", true)

                null
            }.await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun getUserBookings(userId: String): List<com.fmagnus.photostudio.data.model.Booking> {
        return try {
            db.collection("bookings")
                .whereEqualTo("userId", userId)
                .orderBy("appointmentTimestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get()
                .await()
                .toObjects(com.fmagnus.photostudio.data.model.Booking::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}
