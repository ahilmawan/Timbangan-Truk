package id.ahilmawan.weightbridge.ui.tickets

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import id.ahilmawan.weightbridge.databinding.ItemTicketBinding
import id.ahilmawan.weightbridge.models.Ticket

class TicketItemViewHolder(
    private val binding: ItemTicketBinding,
    private val listener: TicketItemListener
) : ViewHolder(binding.root) {

    companion object {
        fun from(parent: ViewGroup, listener: TicketItemListener): TicketItemViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ItemTicketBinding.inflate(layoutInflater, parent, false)
            return TicketItemViewHolder(binding, listener)
        }
    }

    private var ticketId: String? = null

    init {
        binding.root.setOnClickListener { ticketId?.let { listener.onItemClicked(it) } }
        binding.ivEditTicket.setOnClickListener { ticketId?.let { listener.onItemEditClicked(it) } }
    }

    fun onBind(ticket: Ticket) {
        ticketId = ticket.id

        with(binding) {
            tvLicenseNumber.text = ticket.licensePlate
            tvDriverName.text = ticket.driverName
            tvNetWeight.text = ticket.netWeight.toString()
        }
    }
}