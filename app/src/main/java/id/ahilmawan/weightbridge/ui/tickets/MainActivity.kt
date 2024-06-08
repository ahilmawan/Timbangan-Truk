package id.ahilmawan.weightbridge.ui.tickets

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.snackbar.Snackbar
import id.ahilmawan.weightbridge.R
import id.ahilmawan.weightbridge.databinding.ActivityMainBinding
import id.ahilmawan.weightbridge.models.Resource
import id.ahilmawan.weightbridge.models.Ticket
import id.ahilmawan.weightbridge.repositories.FirebaseTicketRepository
import id.ahilmawan.weightbridge.ui.common.SortFilter
import id.ahilmawan.weightbridge.ui.detail.DetailTicketActivity
import id.ahilmawan.weightbridge.ui.form.FormActivity
import id.ahilmawan.weightbridge.ui.form.FormActivity.Companion.EXTRA_TICKET

class MainActivity : AppCompatActivity(), TicketItemListener, FilterSortDialog.FilterSortListener {

    private lateinit var viewBinding: ActivityMainBinding

    private lateinit var viewModel: TicketViewModel

    private lateinit var adapter: TicketAdapter

    private var sortFilter: SortFilter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        viewBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        setSupportActionBar(viewBinding.toolbar)

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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_filter_sort, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_filter_sort -> {
                openSortFilter()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun loadTicket() {
        viewModel.getTickets(sortFilter)
    }

    private fun setupViewModel() {
        viewModel = TicketViewModel(FirebaseTicketRepository())
        viewModel.ticketState.observe(this) { result ->
            when (result) {
                is Resource.Loading -> {
                    viewBinding.progressBar.show()
                }

                is Resource.Success -> {
                    adapter.setItems(result.data)
                    viewBinding.progressBar.hide()
                }

                is Resource.Failure -> {
                    val errorMsg = result.error.message ?: "Unknown Error"
                    Log.e(
                        "MainActivity",
                        "Failed to Load Ticket: $errorMsg",
                        result.error
                    )
                    viewBinding.progressBar.hide()

                    Snackbar.make(viewBinding.root, errorMsg, Snackbar.LENGTH_LONG).show()
                }
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

    override fun onItemClicked(ticket: Ticket) {
        openTicketDetail(ticket)
    }

    override fun onItemEditClicked(ticket: Ticket) {
        openTicketForm(ticket)
    }

    private fun openTicketDetail(ticket: Ticket) {
        startActivity(Intent(this, DetailTicketActivity::class.java).apply {
            putExtra(EXTRA_TICKET, ticket)
        })
    }

    private fun openTicketForm(ticket: Ticket? = null) {
        startActivity(Intent(this, FormActivity::class.java).apply {
            putExtra(EXTRA_TICKET, ticket)
        })
    }

    private fun openSortFilter() {
        FilterSortDialog.newInstance(sortFilter).apply {
            show(supportFragmentManager, FilterSortDialog.TAG)
        }
    }

    override fun applyFilterSort(sortFilter: SortFilter) {
        this.sortFilter = sortFilter
        loadTicket()
    }

    override fun clearFilterSort() {
        this.sortFilter = SortFilter()
        loadTicket()
    }

}
