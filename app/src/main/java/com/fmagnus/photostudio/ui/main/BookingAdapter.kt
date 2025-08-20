package com.fmagnus.photostudio.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.fmagnus.photostudio.data.model.Booking
import com.fmagnus.photostudio.databinding.ItemBookingBinding
import java.text.SimpleDateFormat
import java.util.Locale

class BookingAdapter : ListAdapter<Booking, BookingAdapter.BookingViewHolder>(BookingDiffCallback) {

    private val dateFormat = SimpleDateFormat("d MMMM yyyy", Locale.getDefault())
    private val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

    class BookingViewHolder(private val binding: ItemBookingBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(booking: Booking, dateFormat: SimpleDateFormat, timeFormat: SimpleDateFormat) {
            binding.serviceNameText.text = booking.serviceName.ifEmpty { "Booking" }
            binding.appointmentDateText.text = "Date: ${booking.appointmentTimestamp?.let { dateFormat.format(it) } ?: "N/A"}"
            binding.appointmentTimeText.text = "Time: ${booking.appointmentTimestamp?.let { timeFormat.format(it) } ?: "N/A"}"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookingViewHolder {
        val binding = ItemBookingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BookingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BookingViewHolder, position: Int) {
        holder.bind(getItem(position), dateFormat, timeFormat)
    }
}

object BookingDiffCallback : DiffUtil.ItemCallback<Booking>() {
    override fun areItemsTheSame(oldItem: Booking, newItem: Booking): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Booking, newItem: Booking): Boolean {
        return oldItem == newItem
    }
}
