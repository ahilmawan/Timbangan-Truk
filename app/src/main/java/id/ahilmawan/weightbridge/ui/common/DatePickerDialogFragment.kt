package id.ahilmawan.weightbridge.ui.common

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import java.time.LocalDateTime

class DatePickerDialogFragment : DialogFragment(), DatePickerDialog.OnDateSetListener {

    companion object {
        const val TAG = "DatePickerDialog"

        private const val CURRENT_ARG = "current"

        fun newInstance(current: LocalDateTime = LocalDateTime.now()): DatePickerDialogFragment {

            return DatePickerDialogFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(CURRENT_ARG, current)
                }
            }
        }
    }

    private var listener: DateTimeListener? = null

    private lateinit var currentDateTime: LocalDateTime

    override fun onAttach(context: Context) {
        super.onAttach(context)

        listener = context as? DateTimeListener
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        currentDateTime =
            arguments?.getSerializable(CURRENT_ARG) as? LocalDateTime ?: LocalDateTime.now()

        val dateDialog = DatePickerDialog(
            requireContext(),
            this,
            currentDateTime.year,
            currentDateTime.monthValue - 1,
            currentDateTime.dayOfMonth
        )
        dateDialog.datePicker.maxDate = System.currentTimeMillis()

        return dateDialog
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        listener?.onDateSelected(
            LocalDateTime.of(
                year,
                month,
                dayOfMonth,
                currentDateTime.hour,
                currentDateTime.minute
            )
        )

        dismiss()
    }
}
