package id.ahilmawan.weightbridge.repositories

import id.ahilmawan.weightbridge.models.Ticket

interface TicketRepository {

    suspend fun getTickets(): List<Ticket>

    suspend fun createTicket(ticket: Ticket): Ticket

    suspend fun editTicket(ticket: Ticket): Ticket
}
