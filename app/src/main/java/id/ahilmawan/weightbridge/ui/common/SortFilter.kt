package id.ahilmawan.weightbridge.ui.common

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SortFilter(
    val filterTerm: String? = null,
    val sortField: Field? = null,
    val sortOrder: Order? = null
) : Parcelable {

    companion object {
        const val DATE_FORMAT = "d MMMM yyyy"
    }

    enum class Field {
        CHECKIN_DATE_TIME, LICENSE_NUMBER, DRIVER_NAME
    }

    enum class Order {
        ASC, DESC
    }

}
