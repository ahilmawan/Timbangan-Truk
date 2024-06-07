package id.ahilmawan.weightbridge.ui.tickets

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
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
        setupView()
        setupEventListener()

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

    private fun setupView() {
        with(viewBinding) {
            when (sortFilter.sortField) {
                CHECKIN_DATE_TIME -> {
                    if (sortFilter.sortOrder == ASC) rgSortOptions.check(R.id.rbDateAsc)
                    else rgSortOptions.check(R.id.rbDateDesc)
                }

                DRIVER_NAME -> {
                    if (sortFilter.sortOrder == ASC) rgSortOptions.check(R.id.rbNameAsc)
                    else rgSortOptions.check(R.id.rbNameDesc)
                }

                LICENSE_NUMBER -> {
                    if (sortFilter.sortOrder == ASC) rgSortOptions.check(R.id.rbPlateAsc)
                    else rgSortOptions.check(R.id.rbPlateDesc)
                }

                else -> {
                    rgSortOptions.clearCheck()
                }
            }
        }
    }

    private fun setupEventListener() {
        with(viewBinding) {
            rgSortOptions.setOnCheckedChangeListener { _, checkedId ->
                when (checkedId) {
                    R.id.rbDateAsc -> {
                        sortFilter = sortFilter.copy(
                            sortField = CHECKIN_DATE_TIME,
                            sortOrder = ASC
                        )
                    }

                    R.id.rbDateDesc -> {
                        sortFilter = sortFilter.copy(
                            sortField = CHECKIN_DATE_TIME,
                            sortOrder = DESC
                        )
                    }

                    R.id.rbNameAsc -> {
                        sortFilter = sortFilter.copy(
                            sortField = DRIVER_NAME,
                            sortOrder = ASC
                        )
                    }

                    R.id.rbNameDesc -> {
                        sortFilter = sortFilter.copy(
                            sortField = DRIVER_NAME,
                            sortOrder = DESC
                        )
                    }

                    R.id.rbPlateAsc -> {
                        sortFilter = sortFilter.copy(
                            sortField = LICENSE_NUMBER,
                            sortOrder = ASC
                        )
                    }

                    R.id.rbPlateDesc -> {
                        sortFilter = sortFilter.copy(
                            sortField = LICENSE_NUMBER,
                            sortOrder = DESC
                        )
                    }
                }
            }
        }
    }
}
