package id.ahilmawan.weightbridge.ui.form

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import id.ahilmawan.weightbridge.R
import id.ahilmawan.weightbridge.databinding.ActivityFormBinding
import id.ahilmawan.weightbridge.models.Resource
import id.ahilmawan.weightbridge.models.Ticket
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FormActivity : AppCompatActivity() {

    companion object {
        const val TAG = "FormActivity"
        const val EXTRA_TICKET = "EXTRA_TICKET"
    }

    private val viewModel: FormViewModel by viewModels()

    private lateinit var viewBinding: ActivityFormBinding

    private var isInputValid = false

    private var isFirstTimeValidation = true

    private var ticket = Ticket()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        viewBinding = ActivityFormBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        setSupportActionBar(viewBinding.toolbar)

        ViewCompat.setOnApplyWindowInsetsListener(viewBinding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupViewModel()

        intent.getParcelableExtra<Ticket>(EXTRA_TICKET)?.let {
            ticket = it
            setupForm(ticket)
        }

        setupEventListener()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_form, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        menu?.findItem(R.id.action_done)?.isEnabled = isInputValid || isFirstTimeValidation

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_done -> {
                if (isFirstTimeValidation) {
                    isFirstTimeValidation = false

                    validateInitialInput()
                } else {
                    saveTicket()
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupViewModel() {
        viewModel.ticketState.observe(this) { result ->
            when (result) {
                is Resource.Loading -> {}
                is Resource.Success -> {}
                is Resource.Failure -> {}
            }
        }

        lifecycleScope.launch {
            viewModel.inputValidations.collect { isValid ->
                isInputValid = isValid
                if (!isFirstTimeValidation) {
                    invalidateOptionsMenu()
                }
            }
        }
        lifecycleScope.launch {
            viewModel.calculatedNetWeight.collect { netWeight ->
                Log.d("CALCULATED NET WEIGHT", "Weight $netWeight")
                if (netWeight > 0) {
                    viewBinding.tietNetWeight.setText(netWeight.toString())
                } else {
                    viewBinding.tietNetWeight.setText("")
                }
            }
        }
    }

    private fun setupForm(ticket: Ticket) {
        with(viewBinding) {
            tietDriverName.setText(ticket.driverName)
            tietLicensePlate.setText(ticket.licensePlate)
            tietInboundWeight.setText(ticket.inboundWeight.toString())
            tietOutboundWeight.setText(ticket.outboundWeight.toString())
            tietNetWeight.setText(ticket.netWeight.toString())
        }
    }

    private fun setupEventListener() {
        with(viewBinding) {
            tietDriverName.addTextChangedListener {
                val input = it.toString().trim()
                viewModel.setDriverName(input)
                validateDriverField(input)
            }
            tietLicensePlate.addTextChangedListener {
                val input = it.toString().trim()
                viewModel.setLicensePlate(input)
                validateLicenseField(input)
            }
            tietInboundWeight.addTextChangedListener {
                val input = it.toString().trim().toIntOrNull() ?: 0
                viewModel.setInboundWeight(input)
                validateInboundWeightField(input)
            }
            tietOutboundWeight.addTextChangedListener {
                val input = it.toString().trim().toIntOrNull() ?: 0
                viewModel.setOutboundWeight(input)
                validateOutboundWeightField(input)
            }
            tietNetWeight.addTextChangedListener {
                val input = it.toString().trim().toIntOrNull() ?: 0
                viewModel.setNetWeight(input)
                validateNetWeightField(input)
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
        val isValid = value > 0
        viewBinding.tilInboundWeight.error =
            if (isValid) ""
            else getString(R.string.error_greater_than_number, 0)

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

        return isValid
    }
}
