package id.ahilmawan.weightbridge.ui.tickets

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import id.ahilmawan.weightbridge.R
import id.ahilmawan.weightbridge.databinding.FilterSortDialogBinding
import id.ahilmawan.weightbridge.ui.common.SortFilter
import id.ahilmawan.weightbridge.ui.common.SortFilter.Field.CHECKIN_DATE_TIME
import id.ahilmawan.weightbridge.ui.common.SortFilter.Field.DRIVER_NAME
import id.ahilmawan.weightbridge.ui.common.SortFilter.Field.LICENSE_NUMBER
import id.ahilmawan.weightbridge.ui.common.SortFilter.Order.ASC
import id.ahilmawan.weightbridge.ui.common.SortFilter.Order.DESC

class FilterSortDialog : DialogFragment() {

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
            sortFilter.filterTerm?.let { tietSearch.setText(it) }

            when (sortFilter.sortField) {
                CHECKIN_DATE_TIME -> {
                    val text = actvField.adapter.getItem(0).toString()
                    actvField.setText(text, false)
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

            when (sortFilter.sortOrder) {
                ASC -> rgSortOptions.check(R.id.rbAscending)
                DESC -> rgSortOptions.check(R.id.rbDescending)
                else -> rgSortOptions.clearCheck()
            }
        }
    }

    private fun setupEventListener() {
        with(viewBinding) {
            tietSearch.addTextChangedListener {
                sortFilter = sortFilter.copy(filterTerm = it.toString().trim())
            }

            actvField.setOnItemClickListener { _, _, position, _ ->
                when (position) {
                    0 -> sortFilter = sortFilter.copy(sortField = CHECKIN_DATE_TIME)
                    1 -> sortFilter = sortFilter.copy(sortField = DRIVER_NAME)
                    2 -> sortFilter = sortFilter.copy(sortField = LICENSE_NUMBER)
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
}
