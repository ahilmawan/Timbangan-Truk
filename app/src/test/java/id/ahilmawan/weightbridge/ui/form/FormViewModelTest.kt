package id.ahilmawan.weightbridge.ui.form

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import id.ahilmawan.weightbridge.repositories.TicketRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock

@ExperimentalCoroutinesApi
class FormViewModelTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: FormViewModel
    private lateinit var repository: TicketRepository

    @Before
    fun setUp() {
        repository = mock()
        viewModel = FormViewModel(repository)
    }

    @Test
    fun `inputValidations emits true when all inputs are valid`() = runTest {
        // Given
        viewModel.setCheckInTime(1754773200)
        viewModel.setLicensePlate("B 1234 ABC")
        viewModel.setDriverName("John Doe")
        viewModel.setInboundWeight(1000)
        viewModel.setOutboundWeight(2000)
        viewModel.setNetWeight(1000)

        // When
        val isValid = viewModel.inputValidations.first()

        // Then
        assertTrue(isValid)
    }

    @Test
    fun `inputValidations emits false when any input is invalid`() = runTest {
        // Given
        viewModel.setCheckInTime(0L)
        viewModel.setLicensePlate("")
        viewModel.setDriverName("John Doe")
        viewModel.setInboundWeight(1000)
        viewModel.setOutboundWeight(2000)
        viewModel.setNetWeight(1000)

        // When
        val isValid = viewModel.inputValidations.first()

        // Then
        assertFalse(isValid)
    }

    @Test
    fun `weightValidations emits true when inbound weight is less than outbound weight`() =
        runTest {
            // Given
            viewModel.setInboundWeight(1000)
            viewModel.setOutboundWeight(2000)

            // When
            val isValid = viewModel.weightValidations.first()

            // Then
            assertTrue(isValid)
        }

    @Test
    fun `weightValidations emits false when inbound weight is not less than outbound weight`() =
        runTest {
            // Given
            viewModel.setInboundWeight(2000)
            viewModel.setOutboundWeight(1000)

            // When
            val isValid = viewModel.weightValidations.first()

            // Then
            assertFalse(isValid)
        }

    @Test
    fun `calculatedNetWeight emits correct net weight`() = runTest {
        // Given
        viewModel.setInboundWeight(1000)
        viewModel.setOutboundWeight(2000)

        // When
        val netWeight = viewModel.calculatedNetWeight.first()

        // Then
        assertEquals(1000, netWeight)
    }
}