package id.ahilmawan.weightbridge.repositories

import id.ahilmawan.weightbridge.models.Ticket
import id.ahilmawan.weightbridge.ui.common.SortFilter

interface TicketRepository {

    suspend fun getTickets(sortFilter: SortFilter?): List<Ticket>

    suspend fun getTicket(id: String): Ticket?

    suspend fun createTicket(ticket: Ticket): Ticket

    suspend fun editTicket(ticket: Ticket): Ticket
}
