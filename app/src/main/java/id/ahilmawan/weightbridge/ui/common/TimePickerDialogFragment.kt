package id.ahilmawan.weightbridge.ui.common

import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.widget.TimePicker
import androidx.fragment.app.DialogFragment
import java.time.LocalDateTime

class TimePickerDialogFragment : DialogFragment(), TimePickerDialog.OnTimeSetListener {

    companion object {
        const val TAG = "TimePickerDialog"

        private const val CURRENT_ARG = "current"

        fun newInstance(current: LocalDateTime = LocalDateTime.now()): TimePickerDialogFragment {

            return TimePickerDialogFragment().apply {
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

        return TimePickerDialog(
            requireContext(),
            this,
            currentDateTime.hour,
            currentDateTime.minute,
            true
        )
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        listener?.onTimeSelected(
            LocalDateTime.of(
                currentDateTime.year,
                currentDateTime.month,
                currentDateTime.dayOfMonth,
                hourOfDay,
                minute
            )
        )

        dismiss()
    }
}
