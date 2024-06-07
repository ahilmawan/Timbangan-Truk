package id.ahilmawan.weightbridge.ui.form

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel

import id.ahilmawan.weightbridge.models.Resource
import id.ahilmawan.weightbridge.models.Ticket
import id.ahilmawan.weightbridge.repositories.TicketRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class FormViewModel @Inject constructor(
    private val repository: TicketRepository
) : ViewModel() {

    private val ticketResult = MutableLiveData<Resource<Ticket>>()
    val ticketState: LiveData<Resource<Ticket>> get() = ticketResult

    private val chekcinTime = MutableStateFlow(Date())
    private val licensePlate = MutableStateFlow("")
    private val driverName = MutableStateFlow("")
    private val inboundWeight = MutableStateFlow(0)
    private val outboundWeight = MutableStateFlow(0)
    private val netWeight = MutableStateFlow(0)

    val inputValidations: Flow<Boolean> =
        combine(
            licensePlate,
            driverName,
            inboundWeight,
            outboundWeight,
            netWeight
        ) { license, driver, inbound, outbound, net ->

            val isLicenseValid = license.isNotBlank()
            val isDriverValid = driver.isNotBlank()
            val isInboundValid = inbound > 0
            val isOutboundValid = outbound > inbound
            val isNetValid = net > 0

            return@combine isLicenseValid && isDriverValid
                    && isInboundValid && isOutboundValid && isNetValid
        }

    val calculatedNetWeight: Flow<Int> =
        combine(inboundWeight, outboundWeight) { inbound, outbound ->
            return@combine if (inbound in 1..<outbound) {
                outbound - inbound
            } else {
                0
            }
        }

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

    fun setLicensePlate(license: String) {
        licensePlate.value = license
    }

    fun setDriverName(driver: String) {
        driverName.value = driver
    }

    fun setInboundWeight(weight: Int) {
        inboundWeight.value = weight
    }

    fun setOutboundWeight(weight: Int) {
        outboundWeight.value = weight
    }

    fun setNetWeight(weight: Int) {
        netWeight.value = weight
    }

    fun setCheckInTime(time: Date) {
        chekcinTime.value = time
    }
}
