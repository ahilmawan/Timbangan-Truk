package id.ahilmawan.weightbridge.models

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Ticket(
    val licensePlate: String,
    val driverName: String,
    val inboundWeight: Int,
    val outboundWeight: Int,
    val netWeight: Int,
    @get:Exclude var id: String = ""
)
