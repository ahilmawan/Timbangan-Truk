package id.ahilmawan.weightbridge.ui.common

import junit.framework.TestCase.assertEquals
import org.junit.Test
import java.text.SimpleDateFormat
import java.util.Date

class SortFilterUnitTest {

    @Test
    fun testDateFormat() {
        val dateFormat = "d MMMM yyyy"

        val date = Date()

        val targetFormattedText = SimpleDateFormat(dateFormat).format(date)
        val actualFormattedText = SimpleDateFormat(SortFilter.DATE_FORMAT).format(date)

        assertEquals(targetFormattedText, actualFormattedText)
    }
}