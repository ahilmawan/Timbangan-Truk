package id.ahilmawan.weightbridge.ui.tickets

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import id.ahilmawan.weightbridge.R
import id.ahilmawan.weightbridge.databinding.FilterSortDialogBinding
import id.ahilmawan.weightbridge.ui.common.DatePickerDialogFragment
import id.ahilmawan.weightbridge.ui.common.DateTimeListener
import id.ahilmawan.weightbridge.ui.common.SortFilter
import id.ahilmawan.weightbridge.ui.common.SortFilter.Field.CHECKIN_DATE_TIME
import id.ahilmawan.weightbridge.ui.common.SortFilter.Field.DRIVER_NAME
import id.ahilmawan.weightbridge.ui.common.SortFilter.Field.LICENSE_NUMBER
import id.ahilmawan.weightbridge.ui.common.SortFilter.Order.ASC
import id.ahilmawan.weightbridge.ui.common.SortFilter.Order.DESC
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class FilterSortDialog : DialogFragment(), DateTimeListener {

    interface FilterSortListener {
        fun applyFilterSort(sortFilter: SortFilter)
        fun clearFilterSort()
    }

    companion object {
        const val TAG = "FilterSortDialog"

        private const val EXTRA_SORT_FILTER = "EXTRA_SORT_FILTER"

        fun newInstance(sortFilter: SortFilter? = SortFilter()): FilterSortDialog {
            return FilterSortDialog().apply {
                arguments = Bundle().apply {
                    putParcelable(EXTRA_SORT_FILTER, sortFilter)
                }
            }
        }
    }

    private lateinit var viewBinding: FilterSortDialogBinding

    private lateinit var sortFilter: SortFilter

    private var listener: FilterSortListener? = null

    private var filterDateTime: LocalDateTime? = null

    private val dateFormatter = DateTimeFormatter.ofPattern(SortFilter.DATE_FORMAT)

    override fun onAttach(context: Context) {
        super.onAttach(context)

        listener = context as? FilterSortListener
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sortFilter = arguments?.getParcelable(EXTRA_SORT_FILTER) ?: SortFilter()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        viewBinding = FilterSortDialogBinding.inflate(layoutInflater)

        setupEventListener()
        setupInitialView()

        val dialog = AlertDialog.Builder(requireContext())
            .setTitle(R.string.sort_filter)
            .setView(viewBinding.root)
            .setPositiveButton(R.string.apply) { _, _ ->
                listener?.applyFilterSort(sortFilter)
                dismiss()
            }
            .setNegativeButton(R.string.reset) { _, _ ->
                listener?.clearFilterSort()
                dismiss()
            }
            .create()

        return dialog
    }

    private fun setupInitialView() {
        with(viewBinding) {
            tietDate.setOnClickListener { openDatePicker() }

            when (sortFilter.sortField) {
                CHECKIN_DATE_TIME -> {
                    val text = actvField.adapter.getItem(0).toString()
                    actvField.setText(text, false)
                    showPicker(true)
                }

                DRIVER_NAME -> {
                    val text = actvField.adapter.getItem(1).toString()
                    actvField.setText(text, false)
                }

                LICENSE_NUMBER -> {
                    val text = actvField.adapter.getItem(2).toString()
                    actvField.setText(text, false)
                }

                else -> actvField.clearListSelection()
            }

            sortFilter.filterTerm?.let {
                if (sortFilter.sortField == CHECKIN_DATE_TIME) tietDate.setText(it)
                else tietSearch.setText(it)
            }

            when (sortFilter.sortOrder) {
                ASC -> rgSortOptions.check(R.id.rbAscending)
                DESC -> rgSortOptions.check(R.id.rbDescending)
                else -> rgSortOptions.clearCheck()
            }
        }
    }

    private fun setupEventListener() {
        with(viewBinding) {
            actvField.setOnItemClickListener { _, _, position, _ ->
                when (position) {
                    0 -> {
                        showPicker(true)
                        sortFilter = sortFilter.copy(sortField = CHECKIN_DATE_TIME)
                    }

                    1 -> {
                        showPicker(false)
                        sortFilter = sortFilter.copy(sortField = DRIVER_NAME)
                    }

                    2 -> {
                        showPicker(false)
                        sortFilter = sortFilter.copy(sortField = LICENSE_NUMBER)
                    }
                }
            }

            tietSearch.addTextChangedListener {
                if (it.isNullOrBlank().not()) {
                    sortFilter = sortFilter.copy(filterTerm = it.toString().trim())
                }
            }

            rgSortOptions.setOnCheckedChangeListener { _, checkedId ->
                when (checkedId) {
                    R.id.rbAscending -> {
                        sortFilter = sortFilter.copy(sortOrder = ASC)
                    }

                    R.id.rbDescending -> {
                        sortFilter = sortFilter.copy(sortOrder = DESC)
                    }
                }
            }
        }
    }

    private fun openDatePicker() {
        DatePickerDialogFragment.newInstance(filterDateTime ?: LocalDateTime.now())
            .show(childFragmentManager, DatePickerDialogFragment.TAG)
    }

    override fun onDateSelected(dateTime: LocalDateTime) {
        filterDateTime = dateTime
        val term = dateTime.format(dateFormatter)
        sortFilter = sortFilter.copy(filterTerm = term)
        viewBinding.tietDate.setText(term)
    }

    override fun onTimeSelected(dateTime: LocalDateTime) {
        // Do nothing
    }

    private fun showPicker(show: Boolean) {
        viewBinding.tilSearch.isInvisible = show
        viewBinding.tilDate.isVisible = show
    }
}
