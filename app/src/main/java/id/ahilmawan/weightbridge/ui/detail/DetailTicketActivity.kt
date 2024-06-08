package id.ahilmawan.weightbridge.ui.detail

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import id.ahilmawan.weightbridge.R
import id.ahilmawan.weightbridge.databinding.ActivityDetailBinding
import id.ahilmawan.weightbridge.models.Ticket
import id.ahilmawan.weightbridge.ui.form.FormActivity
import id.ahilmawan.weightbridge.ui.form.FormActivity.Companion.EXTRA_TICKET
import java.time.format.DateTimeFormatter

class DetailTicketActivity : AppCompatActivity() {

    private var ticket: Ticket? = null

    private lateinit var viewBinding: ActivityDetailBinding

    private val editTicketLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                ticket = result.data?.getParcelableExtra(EXTRA_TICKET)
                ticket?.let { setupView(it) }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewBinding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        setupToolbar()

        intent.getParcelableExtra<Ticket>(EXTRA_TICKET)?.let { ticket ->
            this.ticket = ticket
            setupView(ticket)
        } ?: throw Exception("Ticket not found")
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_edit, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }

            R.id.action_edit -> {
                openEditTicket()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setupToolbar() {
        setSupportActionBar(viewBinding.toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
        }
    }

    private fun setupView(ticket: Ticket) {
        val fieldDateFormatter = DateTimeFormatter.ofPattern(FormActivity.DATE_FIELD_FORMAT)
        val checkInDateTimeLabel = fieldDateFormatter.format(ticket.checkinDateTime)

        with(viewBinding) {
            tvLicenseNumber.text = ticket.licensePlate
            tvDriverName.text = ticket.driverName
            tvInboundWeight.text = getString(R.string.inbound_format, ticket.inboundWeight)
            tvOutboundWeight.text = getString(R.string.outbound_format, ticket.outboundWeight)
            tvNetWeight.text = getString(R.string.net_format, ticket.netWeight)
            tvCheckInTime.text = checkInDateTimeLabel
        }
    }

    private fun openEditTicket() {
        ticket?.let {
            editTicketLauncher.launch(Intent(this, FormActivity::class.java).apply {
                putExtra(EXTRA_TICKET, it)
            })
        }
    }
}