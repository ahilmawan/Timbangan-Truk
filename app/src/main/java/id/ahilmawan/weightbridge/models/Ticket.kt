package id.ahilmawan.weightbridge.models

import android.os.Parcelable
import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties
import kotlinx.parcelize.Parcelize

@IgnoreExtraProperties
@Parcelize
data class Ticket(
    val driverName: String = "",
    val licensePlate: String = "",
    val inboundWeight: Int = 0,
    val outboundWeight: Int = 0,
    val netWeight: Int = 0,
    val checkinTime: String = "",
    @get:Exclude var id: String = ""
) : Parcelable
