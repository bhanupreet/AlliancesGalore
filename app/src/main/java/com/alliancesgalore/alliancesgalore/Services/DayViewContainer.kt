package com.alliancesgalore.alliancesgalore.Services

import android.view.View
import android.widget.TextView
import com.kizitonwose.calendarview.ui.ViewContainer
import kotlinx.android.synthetic.main.calendar_day_layout.view.*
import org.threeten.bp.LocalDate

class DayViewContainer(view: View) : ViewContainer(view) {
    private val selectedDates = mutableSetOf<LocalDate>()

    val textView: TextView = view.calendarDayText

}
