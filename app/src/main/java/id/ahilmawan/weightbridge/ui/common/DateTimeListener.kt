package id.ahilmawan.weightbridge.ui.common

import java.time.LocalDateTime

interface DateTimeListener {
    fun onDateSelected(dateTime: LocalDateTime)
    fun onTimeSelected(dateTime: LocalDateTime)
}
