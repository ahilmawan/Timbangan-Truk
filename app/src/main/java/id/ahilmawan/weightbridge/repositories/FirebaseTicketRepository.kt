package id.ahilmawan.weightbridge.repositories

import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import id.ahilmawan.weightbridge.models.Ticket
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.tasks.await

class FirebaseTicketRepository : TicketRepository {

    companion object {
        private const val TICKET_DB_CHILD = "ticket"
    }

    private val database = Firebase.database.reference

    override suspend fun getTickets(): List<Ticket> {
        return ticketsFlow().toList().flatten() // -> hack?
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

    private fun ticketsFlow() = callbackFlow {
        val dataListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val tickets = snapshot.children.mapNotNull { it.getValue(Ticket::class.java) }
                trySend(tickets)
            }

            override fun onCancelled(error: DatabaseError) {

            }
        }

        database.addValueEventListener(dataListener)
        awaitClose {
            database.removeEventListener(dataListener)
        }
    }

}