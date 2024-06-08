package id.ahilmawan.weightbridge.repositories

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.google.firebase.database.getValue
import id.ahilmawan.weightbridge.models.Ticket
import id.ahilmawan.weightbridge.ui.common.SortFilter
import id.ahilmawan.weightbridge.ui.common.SortFilter.Field.CHECKIN_DATE_TIME
import id.ahilmawan.weightbridge.ui.common.SortFilter.Field.DRIVER_NAME
import id.ahilmawan.weightbridge.ui.common.SortFilter.Field.LICENSE_NUMBER
import id.ahilmawan.weightbridge.ui.common.SortFilter.Order.ASC
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class FirebaseTicketRepository @Inject constructor() : TicketRepository {

    companion object {
        private const val TICKET_DB_CHILD = "ticket"
        private const val TICKET_DATE_FIELD = "checkinTime"
        private const val TICKET_DRIVER_FIELD = "driverName"
        private const val TICKET_PLATE_FIELD = "licensePlate"
    }

    private val database = Firebase.database.reference

    override suspend fun getTickets(sortFilter: SortFilter?): List<Ticket> {
        var result: List<Ticket> = ticketsFlow(sortFilter).first()

        // Default sorting is DESC
        if (sortFilter?.sortOrder != ASC) {
            result = result.reversed()
        }

        return result
    }

    override suspend fun getTicket(id: String): Ticket? {
        val snapshot = database.child(TICKET_DB_CHILD).child(id).get().await()
        return snapshot.getValue<Ticket>()
    }

    override suspend fun createTicket(ticket: Ticket): Ticket {
        val key = database.child(TICKET_DB_CHILD).push().key
            ?: throw IllegalStateException("Failed to create ticket, Unable to create data key")

        database.child(TICKET_DB_CHILD).child(key).setValue(ticket).await()
        ticket.id = key
        return ticket
    }

    override suspend fun editTicket(ticket: Ticket): Ticket {
        database.child(TICKET_DB_CHILD).child(ticket.id).setValue(ticket).await()

        return ticket
    }

    private fun ticketsFlow(sortFilter: SortFilter?) = callbackFlow {
        val dataListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val tickets = mutableListOf<Ticket>()
                for (data in snapshot.children) {
                    val ticket = data.getValue<Ticket>()
                    if (ticket != null) {
                        ticket.id = data.key ?: ""
                        tickets.add(ticket)
                    }
                }
                Log.d("FirebaseTicketRepository", "Flow Tickets: $tickets")

                trySend(tickets)
            }

            override fun onCancelled(error: DatabaseError) {

            }
        }

        val dbReference = database.child(TICKET_DB_CHILD)

        val orderField = when (sortFilter?.sortField) {
            CHECKIN_DATE_TIME -> TICKET_DATE_FIELD
            DRIVER_NAME -> TICKET_DRIVER_FIELD
            LICENSE_NUMBER -> TICKET_PLATE_FIELD
            else -> TICKET_DATE_FIELD // -> default order is by checkin time
        }

        var dbQuery = dbReference.orderByChild(orderField)

        sortFilter?.filterTerm?.let {
            if (orderField == TICKET_DATE_FIELD) {
                val originalFormatter = DateTimeFormatter.ofPattern(SortFilter.DATE_FORMAT)
                val originalDate = LocalDate.parse(it, originalFormatter)
                val startDateTime = originalDate.atStartOfDay()
                val endDateTime = originalDate.atTime(LocalTime.MAX)

                dbQuery = dbQuery
                    .startAfter(startDateTime.toEpochSecond(ZoneOffset.UTC).toDouble())
                    .endAt(endDateTime.toEpochSecond(ZoneOffset.UTC).toDouble())

            } else if (it.isNotBlank()) {
                dbQuery = dbQuery.equalTo(it)
            }
        }

        Log.d("FirebaseRepo", "Query $dbQuery")

        dbQuery.addValueEventListener(dataListener)

        awaitClose {
            dbQuery.removeEventListener(dataListener)
        }
    }

}
