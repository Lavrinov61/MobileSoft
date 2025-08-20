package com.fmagnus.photostudio.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.fmagnus.photostudio.data.model.PhotoService
import com.fmagnus.photostudio.databinding.ItemServiceBinding

class ServiceAdapter(private val onClick: (PhotoService) -> Unit) :
    ListAdapter<PhotoService, ServiceAdapter.ServiceViewHolder>(ServiceDiffCallback) {

    class ServiceViewHolder(private val binding: ItemServiceBinding, val onClick: (PhotoService) -> Unit) :
        RecyclerView.ViewHolder(binding.root) {
        private var currentService: PhotoService? = null

        init {
            itemView.setOnClickListener {
                currentService?.let {
                    onClick(it)
                }
            }
        }

        fun bind(service: PhotoService) {
            currentService = service
            binding.serviceName.text = service.name
            binding.serviceDescription.text = service.description
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServiceViewHolder {
        val binding = ItemServiceBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ServiceViewHolder(binding, onClick)
    }

    override fun onBindViewHolder(holder: ServiceViewHolder, position: Int) {
        val service = getItem(position)
        holder.bind(service)
    }
}

object ServiceDiffCallback : DiffUtil.ItemCallback<PhotoService>() {
    override fun areItemsTheSame(oldItem: PhotoService, newItem: PhotoService): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: PhotoService, newItem: PhotoService): Boolean {
        return oldItem == newItem
    }
}
