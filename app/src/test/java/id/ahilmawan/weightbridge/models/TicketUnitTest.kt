package id.ahilmawan.weightbridge.models

import junit.framework.TestCase.assertEquals
import org.junit.Test
import java.time.LocalDateTime
import java.time.ZoneOffset

class TicketUnitTest {
    @Test
    fun testCheckinDateTime() {
        val targetEpochSecond = 1754773200L

        val ticket = Ticket(checkinTime = targetEpochSecond)

        val targetDateTime = LocalDateTime.ofEpochSecond(targetEpochSecond, 0, ZoneOffset.UTC)

        assertEquals(targetDateTime, ticket.checkinDateTime)
    }
}