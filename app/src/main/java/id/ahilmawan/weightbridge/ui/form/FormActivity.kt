package id.ahilmawan.weightbridge.ui.form

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import id.ahilmawan.weightbridge.R
import id.ahilmawan.weightbridge.databinding.ActivityFormBinding
import id.ahilmawan.weightbridge.models.Resource
import id.ahilmawan.weightbridge.models.Ticket
import id.ahilmawan.weightbridge.ui.common.DatePickerDialogFragment
import id.ahilmawan.weightbridge.ui.common.DateTimeListener
import id.ahilmawan.weightbridge.ui.common.ProgressDialog
import id.ahilmawan.weightbridge.ui.common.TimePickerDialogFragment
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@AndroidEntryPoint
class FormActivity : AppCompatActivity(), DateTimeListener {

    companion object {
        const val TAG = "FormActivity"
        const val EXTRA_TICKET = "EXTRA_TICKET"
        const val DATE_FIELD_FORMAT = "EEEE, d MMMM yyyy HH:mm"
    }

    private val viewModel: FormViewModel by viewModels()

    private lateinit var viewBinding: ActivityFormBinding

    private var dateFieldFormatter = DateTimeFormatter.ofPattern(DATE_FIELD_FORMAT)

    private var isInputValid = true

    private var isFirstTimeValidation = true

    private var ticket = Ticket()

    private var checkinTime: LocalDateTime? = null

    private val progressDialog: ProgressDialog by lazy {
        ProgressDialog(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        viewBinding = ActivityFormBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        setupToolbar()

        ViewCompat.setOnApplyWindowInsetsListener(viewBinding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupViewModel()

        setupView()

        intent.getParcelableExtra<Ticket>(EXTRA_TICKET)?.let {
            ticket = it
            Log.d(TAG, "Ticket Initial: $ticket")
            setupTitle(getString(R.string.edit_ticket))
            setupForm(ticket)
        }

        setupFieldErrorAutoDetection()
    }

    private fun setupToolbar() {
        setSupportActionBar(viewBinding.toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
        }
    }

    private fun setupTitle(title: String) {
        supportActionBar?.title = title
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_form, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        menu?.findItem(R.id.action_done)?.let {
            it.icon =
                if (isInputValid) ContextCompat.getDrawable(this, R.drawable.baseline_check_24)
                else ContextCompat.getDrawable(this, R.drawable.baseline_check_grey_24)
            it.isEnabled = isInputValid
        }

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()

                true
            }

            R.id.action_done -> {
                if (isFirstTimeValidation) {
                    isFirstTimeValidation = false

                    validateInitialInput()
                } else {
                    saveTicket()
                }

                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setupViewModel() {
        viewModel.ticketState.observe(this) { result ->
            when (result) {
                is Resource.Loading -> {
                    progressDialog.show()
                }

                is Resource.Success -> {
                    progressDialog.dismiss()
                    setResult(RESULT_OK, Intent().apply {
                        putExtra(EXTRA_TICKET, result.data)
                    })
                    finish()
                }

                is Resource.Failure -> {
                    val message = result.error.message ?: ""
                    Log.e(TAG, "Unable to Save Ticket:$message", result.error)
                    progressDialog.dismiss()
                    Snackbar.make(viewBinding.root, message, Snackbar.LENGTH_LONG).show()
                }
            }
        }

        lifecycleScope.launch {
            viewModel.inputValidations.collect { isValid ->
                if (!isFirstTimeValidation) {
                    isInputValid = isValid
                    invalidateOptionsMenu()
                }
            }
        }
        lifecycleScope.launch {
            viewModel.calculatedNetWeight.collect { netWeight ->
                if (netWeight > 0) {
                    viewBinding.tietNetWeight.setText(netWeight.toString())
                } else {
                    viewBinding.tietNetWeight.setText("")
                }
            }
        }
    }

    private fun setupForm(ticket: Ticket) {
        checkinTime = ticket.checkInDateTime
        with(viewBinding) {
            checkinTime?.let { tietCheckinTime.setText(dateFieldFormatter.format(it)) }
            tietDriverName.setText(ticket.driverName)
            tietLicensePlate.setText(ticket.licensePlate)
            tietInboundWeight.setText(ticket.inboundWeight.toString())
            tietOutboundWeight.setText(ticket.outboundWeight.toString())
            tietNetWeight.setText(ticket.netWeight.toString())
        }
    }

    private fun setupView() {
        viewBinding.tietCheckinTime.setOnClickListener {
            openDatePicker()
        }
    }

    private fun setupFieldErrorAutoDetection() {
        with(viewBinding) {
            tietDriverName.addTextChangedListener {
                val input = it.toString().trim()
                viewModel.setDriverName(input)
                if (!isFirstTimeValidation) validateDriverField(input)
            }
            tietLicensePlate.addTextChangedListener {
                val input = it.toString().trim()
                viewModel.setLicensePlate(input)
                if (!isFirstTimeValidation) validateLicenseField(input)
            }
            tietInboundWeight.addTextChangedListener {
                val input = it.toString().trim().toIntOrNull() ?: 0
                viewModel.setInboundWeight(input)
                if (!isFirstTimeValidation) validateInboundWeightField(input)
            }
            tietOutboundWeight.addTextChangedListener {
                val input = it.toString().trim().toIntOrNull() ?: 0
                viewModel.setOutboundWeight(input)
                if (!isFirstTimeValidation) validateOutboundWeightField(input)
            }
            tietNetWeight.addTextChangedListener {
                val input = it.toString().trim().toIntOrNull() ?: 0
                viewModel.setNetWeight(input)
                if (!isFirstTimeValidation) validateNetWeightField(input)
            }
        }
    }

    private fun saveTicket() {
        ticket = ticket.copy(
            driverName = viewBinding.tietDriverName.text.toString().trim(),
            licensePlate = viewBinding.tietLicensePlate.text.toString().trim(),
            inboundWeight = viewBinding.tietInboundWeight.text.toString().trim().toInt(),
            outboundWeight = viewBinding.tietOutboundWeight.text.toString().trim().toInt(),
            netWeight = viewBinding.tietNetWeight.text.toString().trim().toInt()
        )
        checkinTime?.let { ticket.checkInDateTime = it }

        Log.d(TAG, "Ticket Save: $ticket")
        viewModel.saveTicket(ticket)
    }

    private fun validateInitialInput() {
        val driver = viewBinding.tietDriverName.text.toString().trim()
        val license = viewBinding.tietLicensePlate.text.toString().trim()
        val inbound = viewBinding.tietInboundWeight.text.toString().trim().toIntOrNull() ?: 0
        val outbound = viewBinding.tietOutboundWeight.text.toString().trim().toIntOrNull() ?: 0
        val net = viewBinding.tietNetWeight.text.toString().trim().toIntOrNull() ?: 0

        val isValid =
            validateDriverField(driver) && validateLicenseField(license)
                    && validateInboundWeightField(inbound) && validateOutboundWeightField(outbound)
                    && validateNetWeightField(net)

        if (isValid) {
            saveTicket()
        }
    }

    private fun validateDriverField(input: String): Boolean {
        val isValid = input.isNotBlank()
        viewBinding.tilDriverName.error =
            if (isValid) ""
            else getString(R.string.error_not_empty)

        return isValid
    }

    private fun validateLicenseField(input: String): Boolean {
        val isValid = input.isNotBlank()
        viewBinding.tilLicensePlate.error =
            if (isValid) ""
            else getString(R.string.error_not_empty)

        return isValid
    }

    private fun validateNetWeightField(value: Int): Boolean {
        val isValid = value > 0
        viewBinding.tilNetWeight.error =
            if (isValid) ""
            else getString(R.string.error_greater_than_number, 0)

        return isValid
    }

    private fun validateInboundWeightField(value: Int): Boolean {
        val outboundWeight = viewBinding.tietOutboundWeight.text.toString().trim().toInt()

        val isGreaterThanZero = value > 0
        val isLessThanOutbound = value < outboundWeight
        val isValid = isGreaterThanZero && isLessThanOutbound

        viewBinding.tilInboundWeight.error =
            when {
                !isGreaterThanZero -> getString(R.string.error_greater_than_number, 0)
                !isLessThanOutbound -> getString(
                    R.string.error_less_than,
                    getString(R.string.hint_outbound)
                )

                else -> ""
            }

        return isValid
    }

    private fun validateOutboundWeightField(value: Int): Boolean {
        val inBoundWeight = viewBinding.tietInboundWeight.text.toString().trim().toInt()

        val isValid = value > inBoundWeight
        viewBinding.tilOutboundWeight.error =
            if (isValid) ""
            else getString(
                R.string.error_greater_than_field,
                getString(R.string.hint_inbound)
            )

        if (isValid) {
            viewBinding.tilInboundWeight.error = ""
        }


        return isValid
    }

    private fun setDateField(current: LocalDateTime) {
        viewBinding.tietCheckinTime.setText(dateFieldFormatter.format(current))
    }

    private fun openDatePicker() {
        DatePickerDialogFragment.newInstance()
            .show(supportFragmentManager, DatePickerDialogFragment.TAG)
    }

    private fun openTimePicker(current: LocalDateTime) {
        TimePickerDialogFragment.newInstance(current)
            .show(supportFragmentManager, DatePickerDialogFragment.TAG)
    }

    override fun onDateSelected(dateTime: LocalDateTime) {
        checkinTime = dateTime
        openTimePicker(dateTime)
    }

    override fun onTimeSelected(dateTime: LocalDateTime) {
        checkinTime = dateTime
        setDateField(dateTime)
    }
}
