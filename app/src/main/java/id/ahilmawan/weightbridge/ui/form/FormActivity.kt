package id.ahilmawan.weightbridge.ui.form

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import id.ahilmawan.weightbridge.databinding.ActivityFormBinding
import id.ahilmawan.weightbridge.models.Resource
import id.ahilmawan.weightbridge.models.Ticket
import id.ahilmawan.weightbridge.repositories.FirebaseTicketRepository

class FormActivity : AppCompatActivity() {

    companion object {
        const val TAG = "FormActivity"
    }

    private lateinit var viewBinding: ActivityFormBinding

    private var ticket = Ticket(
        "B 1234 ABC",
        "Haris",
        1000,
        1500,
        500
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewBinding = ActivityFormBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        viewBinding.btnUpdate.isEnabled = false

        val viewModel = FormViewModel(FirebaseTicketRepository())
        viewModel.ticketState.observe(this) { result ->
            when (result) {
                is Resource.Success -> {
                    this.ticket = result.data
                    viewBinding.btnUpdate.isEnabled = true
                }

                is Resource.Loading -> {

                }

                is Resource.Failure -> {
                    Log.e(TAG, "Ticket error", result.error)
                }
            }
        }

        viewBinding.btnCreate.setOnClickListener {
            viewModel.saveTicket(ticket)
        }

        viewBinding.btnUpdate.setOnClickListener {
            val updatedTicket = ticket.copy(driverName = "Rahman")
            viewModel.saveTicket(updatedTicket)
        }

    }
}