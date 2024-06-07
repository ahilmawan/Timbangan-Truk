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
import id.ahilmawan.weightbridge.ui.common.SortFilter.Order.DESC
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.tasks.await
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
        if (sortFilter?.sortOrder == DESC) {
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

        var dbQuery = sortFilter?.filterTerm?.let {
            database.child(TICKET_DB_CHILD).equalTo(it) // Need more tinkering to filter case insensitive
        } ?: database.child(TICKET_DB_CHILD)

        dbQuery = sortFilter?.sortField?.let {
            when (it) {
                CHECKIN_DATE_TIME -> {
                    dbQuery.orderByChild(TICKET_DATE_FIELD)
                }

                DRIVER_NAME -> {
                    dbQuery.orderByChild(TICKET_DRIVER_FIELD)
                }

                LICENSE_NUMBER -> {
                    dbQuery.orderByChild(TICKET_PLATE_FIELD)
                }
            }
        } ?: dbQuery

        dbQuery.addValueEventListener(dataListener)

        awaitClose {
            dbQuery.removeEventListener(dataListener)
        }
    }

}
