package com.pucetec.products.services

import com.pucetec.products.exceptions.ProductAlreadyExistsException
import com.pucetec.products.exceptions.ProductNotFoundException
import com.pucetec.products.exceptions.StockOutOfRangeException
import com.pucetec.products.mappers.ProductMapper
import com.pucetec.products.models.entities.Product
import com.pucetec.products.models.requests.ProductRequest
import com.pucetec.products.repositories.ProductRepository
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import kotlin.test.assertEquals
import java.util.Optional

class ProductServiceTest {

    private lateinit var productRepositoryMock: ProductRepository
    private lateinit var productMapper: ProductMapper

    private lateinit var productService: ProductService

    /**
     * TODO TEST TIENE UN CICLO DE VIDA
     *
     * BEFORE →
     *  @BeforeAll → se ejecuta ANTES de todos los tests
     *  @BeforeEach → se ejecuta ANTES de cada test
     *
     * TEST
     *   @Test
     *
     * AFTER
     *  @AfterEach → se ejecuta DESPUÉS de cada test
     *  @AfterAll → se ejecuta DESPUÉS de todos los tests
     *
     */

    @BeforeEach
    fun init() {
        productRepositoryMock = mock(ProductRepository::class.java)
        productMapper = ProductMapper()

        productService = ProductService(
            productMapper = productMapper,
            productRepository = productRepositoryMock
        )
    }

    /**
     * Sintaxis de un test
     * 1. el test deberia o no deberia hacer algo
     *  should - shouldn't
     *
     * 2. el test debe ejecutar una funcion de la clase objetivo
     *  el nombre de la funcion
     *  should return a product given a valid id
     *
     * 3. el test debe contemplar el input que se le da
     *  SHOULD return a product GIVEN a valid id
     */

    @Test
    fun `SHOULD return a product response GIVEN a valid product id`() {

        val productId = 23L

        val mockProduct = Product(
            name = "telefono",
            price = 0.5,
            stock = 10
        ).apply {
            id = productId
        }

        `when`(productRepositoryMock.findById(productId))
            .thenReturn(Optional.of(mockProduct))

        val response = productService.findById(productId)

        assertEquals(expected = mockProduct.name, actual = response.name)
        assertEquals(expected = mockProduct.id, actual = response.id)
        assertEquals(expected = mockProduct.price, actual = response.price)
        assertEquals(expected = mockProduct.stock, actual = response.stock)
    }

    @Test
    fun `SHOULD return a ProductNotFoundException GIVEN a non existing product id`() {
    }

    /**
     * COVERAGE
     *
     * coverage -> numero de lineas que estan cubiertas
     * por test en un archivo
     */

    @Test
    fun `SHOULD save a product GIVEN a valid product request`() {

        val request = ProductRequest(
            name = "telefono",
            price = 0.5,
            stock = 9
        )

        val productToSave = Product(
            name = "telefono",
            price = 0.5,
            stock = 9
        )

        val savedProduct = Product(
            name = "telefono",
            price = 0.5,
            stock = 9
        ).apply { id = 1L }

        // hay que mockear el findByName
        `when`(productRepositoryMock.findByName("telefono"))
            .thenReturn(null)
        // hay que mockear el save
        `when`(productRepositoryMock.save(productToSave))
            .thenReturn(savedProduct)

        val response = productService.save(request)

        assertEquals(savedProduct.id, response.id)
    }

    @Test
    fun `SHOULD NOT save a product GIVEN an existing product name`() {
        val request = ProductRequest(
            name = "telefono",
            price = 0.5,
            stock = 9
        )

        val productToSave = Product(
            name = "telefono",
            price = 0.5,
            stock = 9
        )

        val savedProduct = Product(
            name = "telefono",
            price = 0.5,
            stock = 9
        ).apply { id = 1L }

        //hay que mockear el findByName
        `when`(productRepositoryMock.findByName("telefono"))
            .thenReturn(savedProduct)
        // hay que mockear el save
        `when`(productRepositoryMock.save(productToSave))
            .thenReturn(savedProduct)

        assertThrows<ProductAlreadyExistsException> {
            productService.save(request)
        }
    }

    @Test
    fun `SHOULD NOT save a product GIVEN a stock equal or bigger than 20`() {
        val request = ProductRequest(
            name = "telefono",
            price = 0.5,
            stock = 21
        )

        assertThrows<StockOutOfRangeException> {
            productService.save(request)
        }
    }
}