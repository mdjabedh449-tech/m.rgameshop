package com.example.data

import com.example.model.AdminUser
import com.example.model.Customer
import com.example.model.DailySalesPoint
import com.example.model.DashboardStats
import com.example.model.GamingPlatform
import com.example.model.MembershipTier
import com.example.model.Order
import com.example.model.OrderItem
import com.example.model.OrderStatus
import com.example.model.PaymentStatus
import com.example.model.Product
import com.example.model.ProductCategory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

class GameZoneRepository {

    // Current logged-in admin state
    private val _currentAdmin = MutableStateFlow<AdminUser?>(
        AdminUser(
            id = "admin-001",
            email = "admin@mrgamezone.com",
            displayName = "M.R Commander",
            role = "Super Admin & Zone Master",
            storeName = "M.R GAME ZONE"
        )
    )
    val currentAdmin: StateFlow<AdminUser?> = _currentAdmin.asStateFlow()

    // Products StateFlow
    private val _products = MutableStateFlow<List<Product>>(createInitialProducts())
    val products: StateFlow<List<Product>> = _products.asStateFlow()

    // Orders StateFlow
    private val _orders = MutableStateFlow<List<Order>>(createInitialOrders())
    val orders: StateFlow<List<Order>> = _orders.asStateFlow()

    // Customers StateFlow
    private val _customers = MutableStateFlow<List<Customer>>(createInitialCustomers())
    val customers: StateFlow<List<Customer>> = _customers.asStateFlow()

    // Admin Authentication Simulation
    fun login(email: String, pass: String): Boolean {
        if (email.isNotBlank() && pass.length >= 4) {
            _currentAdmin.value = AdminUser(
                id = "admin-001",
                email = email.trim(),
                displayName = if (email.contains("@")) email.substringBefore("@").replaceFirstChar { it.uppercase() } else "Admin",
                role = "Super Administrator",
                storeName = "M.R GAME ZONE"
            )
            return true
        }
        return false
    }

    fun logout() {
        _currentAdmin.value = null
    }

    // --- Product Operations ---
    fun addProduct(product: Product) {
        val list = _products.value.toMutableList()
        list.add(0, product)
        _products.value = list
    }

    fun updateProduct(product: Product) {
        _products.value = _products.value.map {
            if (it.id == product.id) product else it
        }
    }

    fun deleteProduct(productId: String) {
        _products.value = _products.value.filterNot { it.id == productId }
    }

    fun toggleProductFeatured(productId: String) {
        _products.value = _products.value.map {
            if (it.id == productId) it.copy(isFeatured = !it.isFeatured) else it
        }
    }

    fun updateProductStock(productId: String, newStock: Int) {
        _products.value = _products.value.map {
            if (it.id == productId) it.copy(stockQuantity = newStock.coerceAtLeast(0)) else it
        }
    }

    // --- Order Operations ---
    fun updateOrderStatus(orderId: String, newStatus: OrderStatus) {
        _orders.value = _orders.value.map {
            if (it.id == orderId) it.copy(orderStatus = newStatus) else it
        }
    }

    fun updateOrderTracking(orderId: String, trackingNumber: String, courier: String) {
        _orders.value = _orders.value.map {
            if (it.id == orderId) it.copy(
                trackingNumber = trackingNumber,
                courierName = courier,
                orderStatus = if (it.orderStatus == OrderStatus.PENDING || it.orderStatus == OrderStatus.PROCESSING) OrderStatus.SHIPPED else it.orderStatus
            ) else it
        }
    }

    fun cancelOrder(orderId: String, reason: String? = null) {
        _orders.value = _orders.value.map {
            if (it.id == orderId) it.copy(
                orderStatus = OrderStatus.CANCELLED,
                notes = if (reason.isNullOrBlank()) it.notes else "Cancelled: $reason"
            ) else it
        }
    }

    // --- Customer Operations ---
    fun toggleCustomerActive(customerId: String) {
        _customers.value = _customers.value.map {
            if (it.id == customerId) it.copy(isActive = !it.isActive) else it
        }
    }

    fun updateCustomerTier(customerId: String, tier: MembershipTier) {
        _customers.value = _customers.value.map {
            if (it.id == customerId) it.copy(membershipTier = tier) else it
        }
    }

    // --- Statistics Computation ---
    fun computeDashboardStats(): DashboardStats {
        val currentOrders = _orders.value
        val totalRevenue = currentOrders
            .filter { it.orderStatus != OrderStatus.CANCELLED }
            .sumOf { it.total }

        val pending = currentOrders.count { it.orderStatus == OrderStatus.PENDING }
        val processing = currentOrders.count { it.orderStatus == OrderStatus.PROCESSING }
        val delivered = currentOrders.count { it.orderStatus == OrderStatus.DELIVERED }
        val lowStock = _products.value.count { it.isLowStock || it.isOutOfStock }

        val weeklySales = listOf(
            DailySalesPoint("Mon", 3450.0, 18),
            DailySalesPoint("Tue", 4820.0, 24),
            DailySalesPoint("Wed", 4120.0, 21),
            DailySalesPoint("Thu", 5600.0, 29),
            DailySalesPoint("Fri", 7890.0, 42),
            DailySalesPoint("Sat", 11450.0, 68),
            DailySalesPoint("Sun", 9280.0, 52)
        )

        return DashboardStats(
            totalRevenue = totalRevenue,
            monthlyRevenueGrowthPercent = 18.4,
            totalOrdersCount = currentOrders.size,
            pendingOrdersCount = pending,
            processingOrdersCount = processing,
            completedOrdersCount = delivered,
            totalCustomersCount = _customers.value.size,
            lowStockItemsCount = lowStock,
            weeklySales = weeklySales
        )
    }

    // Reset with Fresh Gaming Catalog
    fun resetToDefaultData() {
        _products.value = createInitialProducts()
        _orders.value = createInitialOrders()
        _customers.value = createInitialCustomers()
    }

    companion object {
        private fun createInitialProducts(): List<Product> = listOf(
            Product(
                id = "PRD-101",
                name = "PlayStation 5 Pro Console (2TB SSD)",
                sku = "MR-PS5-PRO-2TB",
                category = ProductCategory.CONSOLES,
                platform = GamingPlatform.PS5,
                price = 699.99,
                salePrice = 649.99,
                stockQuantity = 8,
                lowStockThreshold = 5,
                description = "PlayStation Spectral Super Resolution (PSSR), 60FPS Fidelity mode ray tracing, advanced liquid crystal cooling, ultra-fast 2TB NVMe PCIe Gen 4 SSD.",
                specifications = mapOf(
                    "Storage" to "2TB Custom NVMe SSD",
                    "Resolution" to "Up to 8K, 4K 120Hz HDR",
                    "GPU" to "RDNA 3.5 Custom 16.7 TFLOPS",
                    "Audio" to "Tempest 3D AudioTech"
                ),
                imageUrl = "https://images.unsplash.com/photo-1606813907291-d86efa9b94db?auto=format&fit=crop&w=600&q=80",
                isFeatured = true,
                isActive = true
            ),
            Product(
                id = "PRD-102",
                name = "M.R Apex Titan Gaming PC (RTX 4090 / i9-14900KS)",
                sku = "MR-PC-TITAN-4090",
                category = ProductCategory.GAMING_PCS,
                platform = GamingPlatform.PC,
                price = 3899.00,
                salePrice = 3699.00,
                stockQuantity = 3,
                lowStockThreshold = 4,
                description = "Custom liquid-cooled black & gold tempered glass beast. 64GB DDR5-6400 RAM, ROG Maximus Dark Hero motherboard, PCIe 5.0 SSD 4TB, Corsair 1200W Titanium.",
                specifications = mapOf(
                    "GPU" to "GeForce RTX 4090 24GB GDDR6X",
                    "CPU" to "Intel Core i9-14900KS (6.2 GHz)",
                    "RAM" to "64GB DDR5 6400MHz RGB",
                    "Cooling" to "Custom 360mm Gold Hardline Loop"
                ),
                imageUrl = "https://images.unsplash.com/photo-1587202372775-e229f172b9d7?auto=format&fit=crop&w=600&q=80",
                isFeatured = true,
                isActive = true
            ),
            Product(
                id = "PRD-103",
                name = "DualSense Edge Gold Edition Wireless Controller",
                sku = "MR-ACC-DSE-GOLD",
                category = ProductCategory.ACCESSORIES,
                platform = GamingPlatform.PS5,
                price = 219.99,
                salePrice = null,
                stockQuantity = 14,
                lowStockThreshold = 6,
                description = "Customizable stick caps, mappable rear buttons, adjustable trigger stops, and signature metallic gold faceplate exclusive to M.R Game Zone.",
                specifications = mapOf(
                    "Connectivity" to "USB-C, Bluetooth 5.1 Low Latency",
                    "Haptics" to "Dual actuators with adaptive triggers",
                    "Battery" to "Rechargeable Li-ion 1560mAh"
                ),
                imageUrl = "https://images.unsplash.com/photo-1592840496694-26d035b52b48?auto=format&fit=crop&w=600&q=80",
                isFeatured = true,
                isActive = true
            ),
            Product(
                id = "PRD-104",
                name = "Cyberpunk 2077 Phantom Liberty Collector's Box",
                sku = "MR-VG-CP77-COL",
                category = ProductCategory.VIDEO_GAMES,
                platform = GamingPlatform.PC,
                price = 149.99,
                salePrice = 119.99,
                stockQuantity = 2,
                lowStockThreshold = 5,
                description = "Physical collector's edition including steelbook, Dogtown embroidered patch, Quadra Turbo-R V-Tech scale diecast model, artbook, and digital bonus DLC.",
                specifications = mapOf(
                    "Format" to "Deluxe Physical Box + GOG / Steam Code",
                    "Rating" to "ESRB Mature 17+",
                    "Genre" to "Open World Cyberpunk RPG"
                ),
                imageUrl = "https://images.unsplash.com/photo-1542751371-adc38448a05e?auto=format&fit=crop&w=600&q=80",
                isFeatured = false,
                isActive = true
            ),
            Product(
                id = "PRD-105",
                name = "Xbox Series X 1TB All-Digital Robot White",
                sku = "MR-XBX-1TB-WHT",
                category = ProductCategory.CONSOLES,
                platform = GamingPlatform.XBOX,
                price = 449.99,
                salePrice = null,
                stockQuantity = 11,
                lowStockThreshold = 5,
                description = "12 teraflops raw graphical processing power, DirectX Raytracing, Quick Resume for multiple titles, and seamless Xbox Game Pass Ultimate integration.",
                specifications = mapOf(
                    "Storage" to "1TB Custom NVMe SSD",
                    "Resolution" to "True 4K Gaming up to 120 FPS",
                    "CPU" to "Custom 8-Core AMD Zen 2 @ 3.8GHz"
                ),
                imageUrl = "https://images.unsplash.com/photo-1621259182978-fbf93132d53d?auto=format&fit=crop&w=600&q=80",
                isFeatured = false,
                isActive = true
            ),
            Product(
                id = "PRD-106",
                name = "Razer BlackWidow V4 Pro Mechanical Gaming Keyboard",
                sku = "MR-ACC-RZ-BW4P",
                category = ProductCategory.ACCESSORIES,
                platform = GamingPlatform.PC,
                price = 229.99,
                salePrice = 199.99,
                stockQuantity = 19,
                lowStockThreshold = 5,
                description = "Yellow linear mechanical switches, Razer Command Dial, dedicated macro keys, magnetic plush leatherette underglow wrist rest, 8000Hz polling.",
                specifications = mapOf(
                    "Switches" to "Razer Yellow Linear Mechanical",
                    "Polling Rate" to "True 8000 Hz HyperPolling",
                    "Lighting" to "Per-key Razer Chroma RGB"
                ),
                imageUrl = "https://images.unsplash.com/photo-1587829741301-dc798b83add3?auto=format&fit=crop&w=600&q=80",
                isFeatured = false,
                isActive = true
            ),
            Product(
                id = "PRD-107",
                name = "PlayStation Plus Deluxe 12-Month Membership Card",
                sku = "MR-DIG-PSN-12M",
                category = ProductCategory.DIGITAL_KEYS,
                platform = GamingPlatform.PS5,
                price = 159.99,
                salePrice = 139.99,
                stockQuantity = 45,
                lowStockThreshold = 10,
                description = "Full access to Games Catalog, Classics Catalog, Cloud Streaming, Game Trials, monthly games, and exclusive online multiplayer access.",
                specifications = mapOf(
                    "Region" to "Global / Region Free Digital Key",
                    "Validity" to "365 Days from Activation"
                ),
                imageUrl = "https://images.unsplash.com/photo-1550745165-9bc0b252726f?auto=format&fit=crop&w=600&q=80",
                isFeatured = true,
                isActive = true
            ),
            Product(
                id = "PRD-108",
                name = "M.R Game Zone Championship Esports Jersey (Gold Foil)",
                sku = "MR-MERCH-JRSY-GF",
                category = ProductCategory.MERCHANDISE,
                platform = GamingPlatform.UNIVERSAL,
                price = 79.99,
                salePrice = 64.99,
                stockQuantity = 0, // Out of stock example
                lowStockThreshold = 5,
                description = "Official pro-team tournament jersey with metallic gold heat-pressed emblems, breathable anti-microbial fabric, and custom gamer tag on back.",
                specifications = mapOf(
                    "Material" to "100% Recycled Aerocool Polyester",
                    "Fit" to "Athletic Performance Fit"
                ),
                imageUrl = "https://images.unsplash.com/photo-1511512578047-dfb367046420?auto=format&fit=crop&w=600&q=80",
                isFeatured = false,
                isActive = true
            )
        )

        private fun createInitialOrders(): List<Order> = listOf(
            Order(
                id = "MR-9842",
                customerId = "CUST-001",
                customerName = "Zubair Al-Mansoor",
                customerEmail = "zubair.gamer@gmail.com",
                customerPhone = "+1 (555) 234-8901",
                shippingAddress = "450 Silicon Boulevard, Suite 12B, Austin, TX 78701",
                items = listOf(
                    OrderItem(
                        productId = "PRD-101",
                        productName = "PlayStation 5 Pro Console (2TB SSD)",
                        sku = "MR-PS5-PRO-2TB",
                        unitPrice = 649.99,
                        quantity = 1,
                        imageUrl = "https://images.unsplash.com/photo-1606813907291-d86efa9b94db?auto=format&fit=crop&w=600&q=80"
                    ),
                    OrderItem(
                        productId = "PRD-103",
                        productName = "DualSense Edge Gold Edition Wireless Controller",
                        sku = "MR-ACC-DSE-GOLD",
                        unitPrice = 219.99,
                        quantity = 1,
                        imageUrl = "https://images.unsplash.com/photo-1592840496694-26d035b52b48?auto=format&fit=crop&w=600&q=80"
                    )
                ),
                subtotal = 869.98,
                shippingFee = 15.00,
                discountAmount = 30.00,
                tax = 52.20,
                total = 907.18,
                orderStatus = OrderStatus.PENDING,
                paymentStatus = PaymentStatus.PAID,
                paymentMethod = "Apple Pay",
                trackingNumber = null,
                courierName = null,
                timestamp = System.currentTimeMillis() - (1000 * 60 * 45), // 45 mins ago
                notes = "Please wrap carefully. Birthday gift for brother."
            ),
            Order(
                id = "MR-9841",
                customerId = "CUST-002",
                customerName = "Marcus Vance",
                customerEmail = "m.vance.esports@outlook.com",
                customerPhone = "+1 (555) 987-1234",
                shippingAddress = "720 Lakeview Terrace, Apt 4C, Seattle, WA 98109",
                items = listOf(
                    OrderItem(
                        productId = "PRD-102",
                        productName = "M.R Apex Titan Gaming PC (RTX 4090 / i9-14900KS)",
                        sku = "MR-PC-TITAN-4090",
                        unitPrice = 3699.00,
                        quantity = 1,
                        imageUrl = "https://images.unsplash.com/photo-1587202372775-e229f172b9d7?auto=format&fit=crop&w=600&q=80"
                    )
                ),
                subtotal = 3699.00,
                shippingFee = 0.00, // Free white-glove shipping
                discountAmount = 100.00,
                tax = 215.94,
                total = 3814.94,
                orderStatus = OrderStatus.PROCESSING,
                paymentStatus = PaymentStatus.PAID,
                paymentMethod = "Credit Card (Visa ending in 8892)",
                trackingNumber = "MR-EXP-7721908",
                courierName = "GameZone Express Freight",
                timestamp = System.currentTimeMillis() - (1000 * 60 * 180), // 3 hours ago
                notes = "Pre-tested benchmarks requested prior to dispatch."
            ),
            Order(
                id = "MR-9840",
                customerId = "CUST-003",
                customerName = "Elena Rostova",
                customerEmail = "elena.r@cybernet.io",
                customerPhone = "+1 (555) 345-6789",
                shippingAddress = "108 Ocean Drive, Penthouse 6, Miami, FL 33139",
                items = listOf(
                    OrderItem(
                        productId = "PRD-106",
                        productName = "Razer BlackWidow V4 Pro Mechanical Gaming Keyboard",
                        sku = "MR-ACC-RZ-BW4P",
                        unitPrice = 199.99,
                        quantity = 2,
                        imageUrl = "https://images.unsplash.com/photo-1587829741301-dc798b83add3?auto=format&fit=crop&w=600&q=80"
                    )
                ),
                subtotal = 399.98,
                shippingFee = 9.99,
                discountAmount = 0.0,
                tax = 28.00,
                total = 437.97,
                orderStatus = OrderStatus.SHIPPED,
                paymentStatus = PaymentStatus.PAID,
                paymentMethod = "PayPal",
                trackingNumber = "FDX-9982341029",
                courierName = "FedEx Priority",
                timestamp = System.currentTimeMillis() - (1000 * 60 * 60 * 18),
                notes = "Gate code #4491"
            ),
            Order(
                id = "MR-9839",
                customerId = "CUST-004",
                customerName = "Derrick Thorne",
                customerEmail = "thornegaming@twitch.tv",
                customerPhone = "+1 (555) 789-0123",
                shippingAddress = "88 Sunset Strip, West Hollywood, CA 90069",
                items = listOf(
                    OrderItem(
                        productId = "PRD-107",
                        productName = "PlayStation Plus Deluxe 12-Month Membership Card",
                        sku = "MR-DIG-PSN-12M",
                        unitPrice = 139.99,
                        quantity = 1,
                        imageUrl = "https://images.unsplash.com/photo-1550745165-9bc0b252726f?auto=format&fit=crop&w=600&q=80"
                    )
                ),
                subtotal = 139.99,
                shippingFee = 0.0,
                discountAmount = 0.0,
                tax = 11.20,
                total = 151.19,
                orderStatus = OrderStatus.DELIVERED,
                paymentStatus = PaymentStatus.PAID,
                paymentMethod = "Crypto (USDT-Polygon)",
                trackingNumber = "INSTANT-DIGITAL-DISPATCH",
                courierName = "Automated Key Dispatcher",
                timestamp = System.currentTimeMillis() - (1000 * 60 * 60 * 36),
                notes = "Redeemed instantly via gamer portal."
            ),
            Order(
                id = "MR-9838",
                customerId = "CUST-005",
                customerName = "Kaelen Chen",
                customerEmail = "kaelen.chen@gmail.com",
                customerPhone = "+1 (555) 456-7890",
                shippingAddress = "120 Broadway, New York, NY 10005",
                items = listOf(
                    OrderItem(
                        productId = "PRD-104",
                        productName = "Cyberpunk 2077 Phantom Liberty Collector's Box",
                        sku = "MR-VG-CP77-COL",
                        unitPrice = 119.99,
                        quantity = 1,
                        imageUrl = "https://images.unsplash.com/photo-1542751371-adc38448a05e?auto=format&fit=crop&w=600&q=80"
                    )
                ),
                subtotal = 119.99,
                shippingFee = 8.00,
                discountAmount = 0.0,
                tax = 10.24,
                total = 138.23,
                orderStatus = OrderStatus.CANCELLED,
                paymentStatus = PaymentStatus.REFUNDED,
                paymentMethod = "Credit Card (Mastercard)",
                trackingNumber = null,
                courierName = null,
                timestamp = System.currentTimeMillis() - (1000 * 60 * 60 * 50),
                notes = "Customer requested cancellation before fulfillment."
            )
        )

        private fun createInitialCustomers(): List<Customer> = listOf(
            Customer(
                id = "CUST-001",
                fullName = "Zubair Al-Mansoor",
                email = "zubair.gamer@gmail.com",
                phoneNumber = "+1 (555) 234-8901",
                address = "450 Silicon Boulevard, Suite 12B, Austin, TX",
                totalOrders = 14,
                totalSpent = 4850.50,
                membershipTier = MembershipTier.VIP_GOLD,
                isActive = true,
                joinedDate = "March 2025",
                favoriteCategory = "Consoles & Accessories"
            ),
            Customer(
                id = "CUST-002",
                fullName = "Marcus Vance",
                email = "m.vance.esports@outlook.com",
                phoneNumber = "+1 (555) 987-1234",
                address = "720 Lakeview Terrace, Seattle, WA",
                totalOrders = 8,
                totalSpent = 7920.00,
                membershipTier = MembershipTier.VIP_GOLD,
                isActive = true,
                joinedDate = "August 2024",
                favoriteCategory = "Custom Gaming PCs"
            ),
            Customer(
                id = "CUST-003",
                fullName = "Elena Rostova",
                email = "elena.r@cybernet.io",
                phoneNumber = "+1 (555) 345-6789",
                address = "108 Ocean Drive, Miami, FL",
                totalOrders = 6,
                totalSpent = 1420.25,
                membershipTier = MembershipTier.ELITE,
                isActive = true,
                joinedDate = "November 2025",
                favoriteCategory = "Peripherals"
            ),
            Customer(
                id = "CUST-004",
                fullName = "Derrick Thorne",
                email = "thornegaming@twitch.tv",
                phoneNumber = "+1 (555) 789-0123",
                address = "88 Sunset Strip, West Hollywood, CA",
                totalOrders = 19,
                totalSpent = 3280.90,
                membershipTier = MembershipTier.ELITE,
                isActive = true,
                joinedDate = "February 2024",
                favoriteCategory = "Digital Keys & Subscriptions"
            ),
            Customer(
                id = "CUST-005",
                fullName = "Kaelen Chen",
                email = "kaelen.chen@gmail.com",
                phoneNumber = "+1 (555) 456-7890",
                address = "120 Broadway, New York, NY",
                totalOrders = 3,
                totalSpent = 420.00,
                membershipTier = MembershipTier.PRO,
                isActive = true,
                joinedDate = "January 2026",
                favoriteCategory = "Video Games"
            ),
            Customer(
                id = "CUST-006",
                fullName = "Aria Sterling",
                email = "aria.sterling@vrworld.com",
                phoneNumber = "+1 (555) 876-5432",
                address = "901 Tech Ridge Dr, Denver, CO",
                totalOrders = 5,
                totalSpent = 1890.00,
                membershipTier = MembershipTier.PRO,
                isActive = true,
                joinedDate = "May 2025",
                favoriteCategory = "VR Hardware"
            )
        )
    }
}
