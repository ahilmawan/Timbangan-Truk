package id.ahilmawan.weightbridge.ui.tickets

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import id.ahilmawan.weightbridge.databinding.ActivityMainBinding
import id.ahilmawan.weightbridge.models.Resource
import id.ahilmawan.weightbridge.repositories.FirebaseTicketRepository
import id.ahilmawan.weightbridge.ui.form.FormActivity
import id.ahilmawan.weightbridge.ui.form.FormActivity.Companion.EXTRA_TICKET

class MainActivity : AppCompatActivity(), TicketItemListener {

    private lateinit var viewBinding: ActivityMainBinding

    private lateinit var viewModel: TicketViewModel

    private lateinit var adapter: TicketAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        viewBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        ViewCompat.setOnApplyWindowInsetsListener(viewBinding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupRecyclerView()
        setupEventListener()
        setupViewModel()
    }

    override fun onResume() {
        super.onResume()
        loadTicket()
    }

    private fun loadTicket() {
        viewModel.getTickets()
    }

    private fun setupViewModel() {
        viewModel = TicketViewModel(FirebaseTicketRepository())
        viewModel.ticketState.observe(this) { result ->
            Log.d("MainActivity", "setupViewModel: ${result}")

            when (result) {
                is Resource.Loading -> {}
                is Resource.Success -> {
                    Log.d("MainActivity", "setupViewModel: ${result.data}")
                    adapter.setItems(result.data)
                }

                is Resource.Failure -> {}
            }
        }
    }

    private fun setupRecyclerView() {
        adapter = TicketAdapter(this, mutableListOf())
        viewBinding.rvContent.adapter = adapter
        viewBinding.rvContent.setHasFixedSize(true)
    }

    private fun setupEventListener() {
        viewBinding.fabAddTicket.setOnClickListener {
            openTicketForm()
        }
    }

    override fun onItemClicked(ticketId: String) {
        openTicketForm(ticketId)
    }

    override fun onItemEditClicked(ticketId: String) {
        openTicketForm(ticketId)
    }

    private fun openTicketForm(id: String = "") {
        startActivity(Intent(this, FormActivity::class.java).apply {
            putExtra(EXTRA_TICKET, id)
        })
    }


}