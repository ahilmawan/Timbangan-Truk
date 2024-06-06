package id.ahilmawan.weightbridge.ui.tickets

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import id.ahilmawan.weightbridge.models.Resource
import id.ahilmawan.weightbridge.models.Ticket
import id.ahilmawan.weightbridge.repositories.TicketRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TicketViewModel(
    private val repository: TicketRepository
) : ViewModel() {

    private val ticketResult = MutableLiveData<Resource<List<Ticket>>>()
    val ticketState: LiveData<Resource<List<Ticket>>>
        get() = ticketResult

    fun getTickets() {
        ticketResult.value = Resource.Loading()
        viewModelScope.launch(Dispatchers.IO) {
            ticketResult.postValue(Resource.Success(repository.getTickets()))
        }
    }
}
