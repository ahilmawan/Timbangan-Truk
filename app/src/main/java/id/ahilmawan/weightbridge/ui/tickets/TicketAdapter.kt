package id.ahilmawan.weightbridge.ui.tickets

import android.annotation.SuppressLint
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import id.ahilmawan.weightbridge.models.Ticket

class TicketAdapter(
    private val listener: TicketItemListener,
    private val contents: MutableList<Ticket>
) : Adapter<TicketItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TicketItemViewHolder {
        return TicketItemViewHolder.from(parent, listener)
    }

    override fun getItemCount(): Int = contents.size

    override fun onBindViewHolder(holder: TicketItemViewHolder, position: Int) {
        holder.onBind(contents[position])
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setItems(items: List<Ticket>) {
        contents.clear()
        contents.addAll(items)
        notifyDataSetChanged()
    }

    fun addItem(item: Ticket) {
        contents.add(item)
        notifyItemChanged(contents.size - 1)
    }
}