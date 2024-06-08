package id.ahilmawan.weightbridge.models

import android.os.Parcelable
import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties
import kotlinx.parcelize.Parcelize
import java.time.LocalDateTime
import java.time.ZoneOffset

@IgnoreExtraProperties
@Parcelize
data class Ticket(
    val driverName: String = "",
    val licensePlate: String = "",
    val inboundWeight: Int = 0,
    val outboundWeight: Int = 0,
    val netWeight: Int = 0,
    var checkinTime: Long = 0L,
    @get:Exclude var id: String = ""
) : Parcelable {

    @get:Exclude
    val checkinDateTime: LocalDateTime
        get() = LocalDateTime.ofEpochSecond(checkinTime, 0, ZoneOffset.UTC)
}
