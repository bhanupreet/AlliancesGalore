package com.alliancesgalore.alliancesgalore.Services

//class DayViewContainer(view: View) : ViewContainer(view) {
//    private var selectedDate: LocalDate? = null
//    lateinit var day: CalendarDay // Will be set when this container is bound.
//    val textView = view.exFiveDayText
//    val layout = view.exFiveDayLayout
//    val flightTopView = view.exFiveDayFlightTop
//    val flightBottomView = view.exFiveDayFlightBottom
//
//    init {
//        view.setOnClickListener {
//            if (day.owner == DayOwner.THIS_MONTH) {
//                if (selectedDate != day.date) {
//                    val oldDate = selectedDate
//                    selectedDate = day.date
//                    exFiveCalendar.notifyDateChanged(day.date)
//                    oldDate?.let { exFiveCalendar.notifyDateChanged(it) }
//                    updateAdapterForDate(day.date)
//                }
//            }
//        }
//    }
//}