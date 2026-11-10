package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.GameZoneRepository
import com.example.model.GamingPlatform
import com.example.model.OrderStatus
import com.example.model.Product
import com.example.model.ProductCategory
import com.example.ui.viewmodel.AdminViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("M.R GAME ZONE ADMIN", appName)
    }

    @Test
    fun `test repository initial gaming catalog loads products and orders`() {
        val repo = GameZoneRepository()
        assertTrue(repo.products.value.isNotEmpty())
        assertTrue(repo.orders.value.isNotEmpty())
        assertTrue(repo.customers.value.isNotEmpty())

        val stats = repo.computeDashboardStats()
        assertTrue(stats.totalRevenue > 0)
        assertTrue(stats.totalOrdersCount > 0)
    }

    @Test
    fun `test add and delete product`() {
        val repo = GameZoneRepository()
        val initialCount = repo.products.value.size

        val testProduct = Product(
            id = "PRD-TEST-999",
            name = "Test Gaming Headset",
            sku = "MR-TEST-HDST",
            category = ProductCategory.ACCESSORIES,
            platform = GamingPlatform.UNIVERSAL,
            price = 99.99,
            stockQuantity = 20,
            description = "Test description"
        )

        repo.addProduct(testProduct)
        assertEquals(initialCount + 1, repo.products.value.size)

        repo.deleteProduct("PRD-TEST-999")
        assertEquals(initialCount, repo.products.value.size)
    }

    @Test
    fun `test update order status`() {
        val repo = GameZoneRepository()
        val firstOrder = repo.orders.value.first()

        repo.updateOrderStatus(firstOrder.id, OrderStatus.DELIVERED)
        val updated = repo.orders.value.first { it.id == firstOrder.id }
        assertEquals(OrderStatus.DELIVERED, updated.orderStatus)
    }
}
