package com.fmagnus.photostudio.ui.booking

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.fmagnus.photostudio.data.model.ScheduleSlot
import com.fmagnus.photostudio.databinding.ItemTimeSlotBinding
import java.text.SimpleDateFormat
import java.util.Locale

class TimeSlotAdapter(private val onClick: (ScheduleSlot) -> Unit) :
    ListAdapter<ScheduleSlot, TimeSlotAdapter.TimeSlotViewHolder>(TimeSlotDiffCallback) {

    private val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

    class TimeSlotViewHolder(private val binding: ItemTimeSlotBinding, val onClick: (ScheduleSlot) -> Unit) :
        RecyclerView.ViewHolder(binding.root) {
        private var currentSlot: ScheduleSlot? = null

        init {
            itemView.setOnClickListener {
                currentSlot?.let {
                    onClick(it)
                }
            }
        }

        fun bind(slot: ScheduleSlot, timeFormat: SimpleDateFormat) {
            currentSlot = slot
            binding.timeSlotText.text = slot.startTime?.let { timeFormat.format(it) } ?: "N/A"
            itemView.isEnabled = !slot.isBooked
            binding.root.alpha = if (slot.isBooked) 0.5f else 1.0f
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimeSlotViewHolder {
        val binding = ItemTimeSlotBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TimeSlotViewHolder(binding, onClick)
    }

    override fun onBindViewHolder(holder: TimeSlotViewHolder, position: Int) {
        val slot = getItem(position)
        holder.bind(slot, timeFormat)
    }
}

object TimeSlotDiffCallback : DiffUtil.ItemCallback<ScheduleSlot>() {
    override fun areItemsTheSame(oldItem: ScheduleSlot, newItem: ScheduleSlot): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: ScheduleSlot, newItem: ScheduleSlot): Boolean {
        return oldItem == newItem
    }
}
