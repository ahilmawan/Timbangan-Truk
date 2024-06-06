package id.ahilmawan.weightbridge.ui.form

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import id.ahilmawan.weightbridge.models.Resource
import id.ahilmawan.weightbridge.models.Ticket
import id.ahilmawan.weightbridge.repositories.TicketRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FormViewModel(
    private val repository: TicketRepository
) : ViewModel() {

    private val ticketResult = MutableLiveData<Resource<Ticket>>()
    val ticketState: LiveData<Resource<Ticket>>
        get() = ticketResult

    fun saveTicket(ticket: Ticket) {
        ticketResult.value = Resource.Loading()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                Log.d("FormViewModel", "is Ticket ID available: ${ticket.id.isNotBlank()}")
                if (ticket.id.isBlank()) {
                    repository.createTicket(ticket)
                    ticketResult.postValue(Resource.Success(ticket))
                } else {
                    repository.editTicket(ticket)
                    ticketResult.postValue(Resource.Success(ticket))
                }
            } catch (e: Exception) {
                ticketResult.postValue(Resource.Failure(e))
            }
        }
    }
}
