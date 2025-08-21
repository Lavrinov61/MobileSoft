package com.fmagnus.photostudio.ui.booking

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.fmagnus.photostudio.R
import com.fmagnus.photostudio.databinding.CalendarDayLayoutBinding
import com.fmagnus.photostudio.databinding.FragmentScheduleBinding
import com.fmagnus.photostudio.ui.viewmodel.ScheduleViewModel
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.CalendarMonth
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.firstDayOfWeekFromLocale
import com.kizitonwose.calendar.view.MonthDayBinder
import com.kizitonwose.calendar.view.MonthHeaderFooterBinder
import com.kizitonwose.calendar.view.ViewContainer
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

class ScheduleFragment : Fragment() {

    private var _binding: FragmentScheduleBinding? = null
    private val binding get() = _binding!!

    private val args: ScheduleFragmentArgs by navArgs()
    private val viewModel: ScheduleViewModel by viewModels()
    private lateinit var timeSlotAdapter: TimeSlotAdapter

    private var selectedDate: LocalDate? = null
    private val dateFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentScheduleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupCalendar()
        setupRecyclerView()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        timeSlotAdapter = TimeSlotAdapter { slot ->
            if (!slot.isBooked) {
                val action = ScheduleFragmentDirections.actionScheduleFragmentToPaymentFragment(slot.id, args.serviceId)
                findNavController().navigate(action)
            } else {
                Toast.makeText(context, "This slot is already booked", Toast.LENGTH_SHORT).show()
            }
        }
        binding.timeSlotsRecyclerView.adapter = timeSlotAdapter
    }

    private fun observeViewModel() {
        viewModel.slots.observe(viewLifecycleOwner) { slots ->
            timeSlotAdapter.submitList(slots)
        }
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.isVisible = isLoading
        }
        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun setupCalendar() {
        class DayViewContainer(view: View) : ViewContainer(view) {
            lateinit var day: CalendarDay
            val textView = CalendarDayLayoutBinding.bind(view).calendarDayText
            init {
                view.setOnClickListener {
                    if (day.position == DayPosition.MonthDate) {
                        selectDate(day.date)
                    }
                }
            }
        }

        binding.calendarView.dayBinder = object : MonthDayBinder<DayViewContainer> {
            override fun create(view: View) = DayViewContainer(view)
            override fun bind(container: DayViewContainer, data: CalendarDay) {
                container.day = data
                container.textView.text = data.date.dayOfMonth.toString()
                if (data.position == DayPosition.MonthDate) {
                    container.textView.setTextColor(resources.getColor(android.R.color.black))
                    if (data.date == selectedDate) {
                        container.textView.setBackgroundResource(R.drawable.calendar_selected_day_bg)
                    } else {
                        container.textView.background = null
                    }
                } else {
                    container.textView.setTextColor(resources.getColor(android.R.color.darker_gray))
                    container.textView.background = null
                }
            }
        }

        class MonthHeaderContainer(view: View) : ViewContainer(view) {
            val textView = view.findViewById<TextView>(R.id.month_text)
        }

        binding.calendarView.monthHeaderBinder = object : MonthHeaderFooterBinder<MonthHeaderContainer> {
            override fun create(view: View) = MonthHeaderContainer(view)
            override fun bind(container: MonthHeaderContainer, data: CalendarMonth) {
                container.textView.text = "${data.yearMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault())} ${data.yearMonth.year}"
            }
        }

        val currentMonth = YearMonth.now()
        val startMonth = currentMonth.minusMonths(10)
        val endMonth = currentMonth.plusMonths(10)
        val firstDayOfWeek = firstDayOfWeekFromLocale()
        binding.calendarView.setup(startMonth, endMonth, firstDayOfWeek)
        binding.calendarView.scrollToMonth(currentMonth)
    }

    private fun selectDate(date: LocalDate) {
        if (selectedDate != date) {
            val oldDate = selectedDate
            selectedDate = date
            oldDate?.let { binding.calendarView.notifyDateChanged(it) }
            binding.calendarView.notifyDateChanged(date)
            updateAdapterForDate(date)
        }
    }

    private fun updateAdapterForDate(date: LocalDate) {
        binding.selectedDateText.text = "Available slots for ${dateFormatter.format(date)}"
        viewModel.fetchSlots(args.serviceId, date)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
