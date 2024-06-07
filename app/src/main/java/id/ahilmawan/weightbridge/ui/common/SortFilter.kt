package id.ahilmawan.weightbridge.ui.common

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SortFilter(
    val filterField: Field? = null,
    val filterTerm: String? = null,
    val sortField: Field? = null,
    val sortOrder: Order? = null
) : Parcelable {

    enum class Field {
        CHECKIN_DATE_TIME, LICENSE_NUMBER, DRIVER_NAME
    }

    enum class Order {
        ASC, DESC
    }

}
