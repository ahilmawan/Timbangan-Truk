package id.ahilmawan.weightbridge.ui.tickets

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import id.ahilmawan.weightbridge.R
import id.ahilmawan.weightbridge.databinding.ItemTicketBinding
import id.ahilmawan.weightbridge.models.Ticket
import id.ahilmawan.weightbridge.ui.form.FormActivity
import java.time.format.DateTimeFormatter

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

    private var ticket: Ticket? = null

    init {
        binding.root.setOnClickListener { ticket?.let { listener.onItemClicked(it) } }
        binding.ivEditTicket.setOnClickListener { ticket?.let { listener.onItemEditClicked(it) } }
    }

    fun onBind(ticket: Ticket) {
        this.ticket = ticket

        val unit = binding.root.context.getString(R.string.suffix_weight_unit)
        val weightUnit = "${ticket.netWeight} $unit"

        with(binding) {
            tvLicenseNumber.text = ticket.licensePlate
            tvDriverName.text = ticket.driverName
            tvNetWeight.text = weightUnit

            tvDate.text = DateTimeFormatter.ofPattern(FormActivity.DATE_FIELD_FORMAT)
                .format(ticket.checkinDateTime)
        }
    }
}
