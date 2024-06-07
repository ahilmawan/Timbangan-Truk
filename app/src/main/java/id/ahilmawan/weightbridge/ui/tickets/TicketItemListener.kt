package id.ahilmawan.weightbridge.ui.tickets

import id.ahilmawan.weightbridge.models.Ticket

interface TicketItemListener {
    fun onItemClicked(ticket: Ticket)

    fun onItemEditClicked(ticket: Ticket)
}
