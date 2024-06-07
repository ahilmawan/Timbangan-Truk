package id.ahilmawan.weightbridge.models

import android.os.Parcelable
import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@IgnoreExtraProperties
@Parcelize
data class Ticket(
    val driverName: String = "",
    val licensePlate: String = "",
    val inboundWeight: Int = 0,
    val outboundWeight: Int = 0,
    val netWeight: Int = 0,
    var checkinTime: String = "",
    @get:Exclude var id: String = ""
) : Parcelable {

    companion object {
        private const val ISO_8601_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'"
    }

    @get:Exclude
    @IgnoredOnParcel
    var checkInDateTime: LocalDateTime? = null
        set(value) {
            field = value
            if (value != null) {
                checkinTime = DateTimeFormatter.ofPattern(ISO_8601_FORMAT).format(value)
            }
        }
        get() {
            if (field == null && checkinTime.isNotBlank()) {
                field =
                    LocalDateTime.parse(checkinTime, DateTimeFormatter.ofPattern(ISO_8601_FORMAT))
            }

            return field
        }

}
