package com.pucetec.products.services

import com.pucetec.products.mappers.InvoiceMapper
import com.pucetec.products.models.entities.Invoice
import com.pucetec.products.models.entities.InvoiceDetail
import com.pucetec.products.models.entities.Product
import com.pucetec.products.models.requests.InvoiceRequest
import com.pucetec.products.repositories.InvoiceDetailRepository
import com.pucetec.products.repositories.InvoiceRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import kotlin.test.assertEquals

class InvoiceServiceTest {

    private lateinit var invoiceRepositoryMock: InvoiceRepository
    private lateinit var invoiceMapper: InvoiceMapper
    private lateinit var invoiceDetailRepositoryMock: InvoiceDetailRepository

    private lateinit var invoiceService: InvoiceService

    @BeforeEach
    fun init() {
        invoiceRepositoryMock = mock(InvoiceRepository::class.java)
        invoiceMapper = InvoiceMapper()
        invoiceDetailRepositoryMock = mock(InvoiceDetailRepository::class.java)

        invoiceService = InvoiceService(
            invoiceRepository = invoiceRepositoryMock,
            invoiceMapper = invoiceMapper,
            invoiceDetailsRepository = invoiceDetailRepositoryMock
        )
    }

    @Test
    fun `SHOULD save an invoice GIVEN a valid invoice request`() {

        val request = InvoiceRequest(
            clientCi = "1723456789",
            clientName = "Juan Perez",
            clientAddress = "Quito",
            totalBeforeTaxes = 10.0F,
            taxes = 1.2F,
            totalAfterTaxes = 11.2F
        )

        val invoiceToSave = Invoice(
            clientCi = "1723456789",
            clientName = "Juan Perez",
            clientAddress = "Quito",
            totalBeforeTaxes = 10.0F,
            taxes = 1.2F,
            totalAfterTaxes = 11.2F
        )

        val savedInvoice = Invoice(
            clientCi = "1723456789",
            clientName = "Juan Perez",
            clientAddress = "Quito",
            totalBeforeTaxes = 10.0F,
            taxes = 1.2F,
            totalAfterTaxes = 11.2F
        ).apply { id = 1L }

        // Mockear save
        `when`(invoiceRepositoryMock.save(invoiceToSave))
            .thenReturn(savedInvoice)

        val response = invoiceService.save(request)

        assertEquals(savedInvoice.id, response.id)
        assertEquals(savedInvoice.clientCi, response.clientCi)
        assertEquals(savedInvoice.clientName, response.clientName)
        assertEquals(savedInvoice.clientAddress, response.clientAddress)
        assertEquals(savedInvoice.totalBeforeTaxes, response.totalBeforeTaxes)
        assertEquals(savedInvoice.taxes, response.taxes)
        assertEquals(savedInvoice.totalAfterTaxes, response.totalAfterTaxes)
    }

    @Test
    fun `SHOULD return an empty list GIVEN no invoices in database`() {

        `when`(invoiceRepositoryMock.findAll())
            .thenReturn(emptyList())

        val response = invoiceService.findAll()

        assertEquals(0, response.size)
    }

    @Test
    fun `SHOULD return invoices with details GIVEN invoices exist in database`() {

        val invoice1 = Invoice(
            clientCi = "1723456789",
            clientName = "Juan Perez",
            clientAddress = "Quito",
            totalBeforeTaxes = 10.0F,
            taxes = 1.2F,
            totalAfterTaxes = 11.2F
        ).apply { id = 1L }

        val invoice2 = Invoice(
            clientCi = "0923456789",
            clientName = "Maria Lopez",
            clientAddress = "Guayaquil",
            totalBeforeTaxes = 20.0F,
            taxes = 0.8F,
            totalAfterTaxes = 20.8F
        ).apply { id = 2L }

        val product1 = Product(
            name = "telefono",
            price = 0.5,
            stock = 10
        ).apply { id = 1L }

        val product2 = Product(
            name = "laptop",
            price = 1.0,
            stock = 5
        ).apply { id = 2L }

        val detail1 = InvoiceDetail(
            totalPrice = 0.5F,
            product = product1,
            invoice = invoice1
        ).apply { id = 1L }

        val detail2 = InvoiceDetail(
            totalPrice = 1.0F,
            product = product2,
            invoice = invoice2
        ).apply { id = 2L }

        `when`(invoiceRepositoryMock.findAll())
            .thenReturn(listOf(invoice1, invoice2))

        `when`(invoiceDetailRepositoryMock.findAllByInvoiceId(1L))
            .thenReturn(listOf(detail1))

        `when`(invoiceDetailRepositoryMock.findAllByInvoiceId(2L))
            .thenReturn(listOf(detail2))

        val response = invoiceService.findAll()

        assertEquals(2, response.size)
        assertEquals(1, invoice1.invoiceDetails.size)
        assertEquals(1, invoice2.invoiceDetails.size)
    }
}
