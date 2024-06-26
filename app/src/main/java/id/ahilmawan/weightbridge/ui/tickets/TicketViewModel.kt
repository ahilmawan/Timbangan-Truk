package id.ahilmawan.weightbridge.ui.tickets

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import id.ahilmawan.weightbridge.models.Resource
import id.ahilmawan.weightbridge.models.Ticket
import id.ahilmawan.weightbridge.repositories.TicketRepository
import id.ahilmawan.weightbridge.ui.common.SortFilter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TicketViewModel(
    private val repository: TicketRepository
) : ViewModel() {

    private val ticketResult = MutableLiveData<Resource<List<Ticket>>>()
    val ticketState: LiveData<Resource<List<Ticket>>>
        get() = ticketResult

    fun getTickets(sortFilter: SortFilter? = null) {
        ticketResult.value = Resource.Loading()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val tickets = repository.getTickets(sortFilter)

                ticketResult.postValue(Resource.Success(tickets))
            } catch (e: Exception) {
                Log.d("TicketViewModel", "Error: ${e.message}", e)
                ticketResult.postValue(Resource.Failure(e))
            }
        }
    }
}
