package id.ahilmawan.weightbridge.ui.form

import android.util.Log
import androidx.annotation.VisibleForTesting
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
import javax.inject.Inject

@HiltViewModel
class FormViewModel @Inject constructor(
    private val repository: TicketRepository
) : ViewModel() {

    private val ticketResult = MutableLiveData<Resource<Ticket>>()
    val ticketState: LiveData<Resource<Ticket>> get() = ticketResult

    private val checkinTime = MutableStateFlow(0L)
    private val licensePlate = MutableStateFlow("")
    private val driverName = MutableStateFlow("")
    private val inboundWeight = MutableStateFlow(0)
    private val outboundWeight = MutableStateFlow(0)
    private val netWeight = MutableStateFlow(0)

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    val weightValidations: Flow<Boolean> =
        combine(inboundWeight, outboundWeight) { inbound, outbound ->
            return@combine inbound in 1..<outbound
        }

    val inputValidations: Flow<Boolean> = combine(
        checkinTime, licensePlate, driverName, weightValidations, netWeight
    ) { checkin, license, driver, weightValid, netWeight ->

        val isLicenseValid = license.isNotBlank()
        val isDriverValid = driver.isNotBlank()
        val isCheckinValid = checkin > 0
        val isNetWeightValid = netWeight > 0

        return@combine isLicenseValid && isDriverValid && isCheckinValid && weightValid && isNetWeightValid
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

    fun setCheckInTime(epochTime: Long) {
        checkinTime.value = epochTime
    }
}
