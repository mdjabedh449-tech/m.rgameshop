package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.GameZoneRepository
import com.example.data.firebase.FirebaseConfig
import com.example.model.AdminUser
import com.example.model.Customer
import com.example.model.DashboardStats
import com.example.model.GamingPlatform
import com.example.model.MembershipTier
import com.example.model.Order
import com.example.model.OrderStatus
import com.example.model.Product
import com.example.model.ProductCategory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

enum class NavigationTab(val label: String) {
    DASHBOARD("Dashboard"),
    PRODUCTS("Products"),
    ORDERS("Orders"),
    CUSTOMERS("Customers"),
    SETTINGS("Settings")
}

sealed class CurrentScreen {
    object Login : CurrentScreen()
    object Dashboard : CurrentScreen()
    object Products : CurrentScreen()
    object AddProduct : CurrentScreen()
    data class EditProduct(val product: Product) : CurrentScreen()
    object Orders : CurrentScreen()
    data class OrderDetail(val orderId: String) : CurrentScreen()
    object Customers : CurrentScreen()
    data class CustomerDetail(val customerId: String) : CurrentScreen()
    object Settings : CurrentScreen()
}

class AdminViewModel(
    private val repository: GameZoneRepository = GameZoneRepository()
) : ViewModel() {

    // Current navigation state
    private val _currentScreen = MutableStateFlow<CurrentScreen>(CurrentScreen.Dashboard)
    val currentScreen: StateFlow<CurrentScreen> = _currentScreen.asStateFlow()

    private val _selectedTab = MutableStateFlow(NavigationTab.DASHBOARD)
    val selectedTab: StateFlow<NavigationTab> = _selectedTab.asStateFlow()

    // Repository flows
    val currentAdmin: StateFlow<AdminUser?> = repository.currentAdmin
    val allProducts: StateFlow<List<Product>> = repository.products
    val allOrders: StateFlow<List<Order>> = repository.orders
    val allCustomers: StateFlow<List<Customer>> = repository.customers

    // Filter & Search states
    private val _productSearchQuery = MutableStateFlow("")
    val productSearchQuery: StateFlow<String> = _productSearchQuery.asStateFlow()

    private val _selectedProductCategory = MutableStateFlow(ProductCategory.ALL)
    val selectedProductCategory: StateFlow<ProductCategory> = _selectedProductCategory.asStateFlow()

    private val _stockFilter = MutableStateFlow("ALL") // ALL, IN_STOCK, LOW_STOCK, OUT_OF_STOCK
    val stockFilter: StateFlow<String> = _stockFilter.asStateFlow()

    private val _orderSearchQuery = MutableStateFlow("")
    val orderSearchQuery: StateFlow<String> = _orderSearchQuery.asStateFlow()

    private val _selectedOrderStatus = MutableStateFlow<OrderStatus?>(null) // null = ALL
    val selectedOrderStatus: StateFlow<OrderStatus?> = _selectedOrderStatus.asStateFlow()

    private val _customerSearchQuery = MutableStateFlow("")
    val customerSearchQuery: StateFlow<String> = _customerSearchQuery.asStateFlow()

    // Feedback message (Snackbar/Banner)
    private val _feedbackMessage = MutableStateFlow<String?>(null)
    val feedbackMessage: StateFlow<String?> = _feedbackMessage.asStateFlow()

    // Cloud Sync progress indicator
    private val _isCloudSyncing = MutableStateFlow(false)
    val isCloudSyncing: StateFlow<Boolean> = _isCloudSyncing.asStateFlow()

    // Active product for delete confirmation dialog
    private val _productPendingDelete = MutableStateFlow<Product?>(null)
    val productPendingDelete: StateFlow<Product?> = _productPendingDelete.asStateFlow()

    // Filtered Products
    val filteredProducts: StateFlow<List<Product>> = combine(
        allProducts,
        _productSearchQuery,
        _selectedProductCategory,
        _stockFilter
    ) { products, query, category, stock ->
        products.filter { p ->
            val matchesQuery = query.isBlank() ||
                p.name.contains(query, ignoreCase = true) ||
                p.sku.contains(query, ignoreCase = true) ||
                p.platform.tag.contains(query, ignoreCase = true)

            val matchesCategory = category == ProductCategory.ALL || p.category == category

            val matchesStock = when (stock) {
                "IN_STOCK" -> p.stockQuantity > p.lowStockThreshold
                "LOW_STOCK" -> p.isLowStock
                "OUT_OF_STOCK" -> p.isOutOfStock
                else -> true
            }

            matchesQuery && matchesCategory && matchesStock
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Filtered Orders
    val filteredOrders: StateFlow<List<Order>> = combine(
        allOrders,
        _orderSearchQuery,
        _selectedOrderStatus
    ) { orders, query, status ->
        orders.filter { o ->
            val matchesQuery = query.isBlank() ||
                o.id.contains(query, ignoreCase = true) ||
                o.customerName.contains(query, ignoreCase = true) ||
                (o.trackingNumber?.contains(query, ignoreCase = true) == true)

            val matchesStatus = status == null || o.orderStatus == status

            matchesQuery && matchesStatus
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Filtered Customers
    val filteredCustomers: StateFlow<List<Customer>> = combine(
        allCustomers,
        _customerSearchQuery
    ) { customers, query ->
        customers.filter { c ->
            query.isBlank() ||
                c.fullName.contains(query, ignoreCase = true) ||
                c.email.contains(query, ignoreCase = true) ||
                c.phoneNumber.contains(query, ignoreCase = true)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Dashboard Statistics dynamically recomputed
    val dashboardStats: StateFlow<DashboardStats> = combine(
        allProducts,
        allOrders,
        allCustomers
    ) { _, _, _ ->
        repository.computeDashboardStats()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), repository.computeDashboardStats())

    // --- Navigation Handlers ---
    fun navigateToTab(tab: NavigationTab) {
        _selectedTab.value = tab
        _currentScreen.value = when (tab) {
            NavigationTab.DASHBOARD -> CurrentScreen.Dashboard
            NavigationTab.PRODUCTS -> CurrentScreen.Products
            NavigationTab.ORDERS -> CurrentScreen.Orders
            NavigationTab.CUSTOMERS -> CurrentScreen.Customers
            NavigationTab.SETTINGS -> CurrentScreen.Settings
        }
    }

    fun navigateTo(screen: CurrentScreen) {
        _currentScreen.value = screen
    }

    fun navigateBack() {
        when (_currentScreen.value) {
            is CurrentScreen.AddProduct, is CurrentScreen.EditProduct -> {
                _currentScreen.value = CurrentScreen.Products
                _selectedTab.value = NavigationTab.PRODUCTS
            }
            is CurrentScreen.OrderDetail -> {
                _currentScreen.value = CurrentScreen.Orders
                _selectedTab.value = NavigationTab.ORDERS
            }
            is CurrentScreen.CustomerDetail -> {
                _currentScreen.value = CurrentScreen.Customers
                _selectedTab.value = NavigationTab.CUSTOMERS
            }
            is CurrentScreen.Login -> {
                // Stay on login
            }
            else -> {
                if (_currentScreen.value != CurrentScreen.Dashboard) {
                    _currentScreen.value = CurrentScreen.Dashboard
                    _selectedTab.value = NavigationTab.DASHBOARD
                }
            }
        }
    }

    // --- Search & Filter Setters ---
    fun setProductSearchQuery(q: String) { _productSearchQuery.value = q }
    fun setProductCategory(cat: ProductCategory) { _selectedProductCategory.value = cat }
    fun setStockFilter(stock: String) { _stockFilter.value = stock }

    fun setOrderSearchQuery(q: String) { _orderSearchQuery.value = q }
    fun setOrderStatusFilter(status: OrderStatus?) { _selectedOrderStatus.value = status }

    fun setCustomerSearchQuery(q: String) { _customerSearchQuery.value = q }

    fun showFeedback(msg: String) {
        _feedbackMessage.value = msg
    }

    fun clearFeedback() {
        _feedbackMessage.value = null
    }

    // --- Authentication Actions ---
    fun login(email: String, pass: String): Boolean {
        val success = repository.login(email, pass)
        if (success) {
            _currentScreen.value = CurrentScreen.Dashboard
            _selectedTab.value = NavigationTab.DASHBOARD
            showFeedback("Welcome to M.R GAME ZONE Admin Portal")
            return true
        } else {
            showFeedback("Invalid credentials. Try demo credentials.")
            return false
        }
    }

    fun logout() {
        repository.logout()
        _currentScreen.value = CurrentScreen.Login
        showFeedback("Logged out successfully.")
    }

    // --- Product CRUD Actions ---
    fun saveProduct(
        id: String?,
        name: String,
        sku: String,
        category: ProductCategory,
        platform: GamingPlatform,
        price: Double,
        salePrice: Double?,
        stockQuantity: Int,
        lowStockThreshold: Int,
        description: String,
        specifications: Map<String, String>,
        imageUrl: String,
        isFeatured: Boolean
    ) {
        if (id == null) {
            // New Product
            val newProduct = Product(
                id = "PRD-" + (100 + (allProducts.value.size + 1)),
                name = name.ifBlank { "Untitled Gaming Gear" },
                sku = sku.ifBlank { "MR-SKU-" + System.currentTimeMillis().toString().takeLast(4) },
                category = category,
                platform = platform,
                price = price.coerceAtLeast(0.0),
                salePrice = if (salePrice != null && salePrice > 0.0) salePrice else null,
                stockQuantity = stockQuantity.coerceAtLeast(0),
                lowStockThreshold = lowStockThreshold.coerceAtLeast(1),
                description = description,
                specifications = specifications,
                imageUrl = imageUrl,
                isFeatured = isFeatured,
                isActive = true
            )
            repository.addProduct(newProduct)
            showFeedback("Added '${newProduct.name}' to inventory")
        } else {
            // Edit existing
            val existing = allProducts.value.find { it.id == id }
            if (existing != null) {
                val updated = existing.copy(
                    name = name,
                    sku = sku,
                    category = category,
                    platform = platform,
                    price = price,
                    salePrice = salePrice,
                    stockQuantity = stockQuantity,
                    lowStockThreshold = lowStockThreshold,
                    description = description,
                    specifications = specifications,
                    imageUrl = imageUrl,
                    isFeatured = isFeatured
                )
                repository.updateProduct(updated)
                showFeedback("Updated '${updated.name}' successfully")
            }
        }
        _currentScreen.value = CurrentScreen.Products
        _selectedTab.value = NavigationTab.PRODUCTS
    }

    fun requestDeleteProduct(product: Product) {
        _productPendingDelete.value = product
    }

    fun cancelDeleteProduct() {
        _productPendingDelete.value = null
    }

    fun confirmDeleteProduct() {
        val prod = _productPendingDelete.value ?: return
        repository.deleteProduct(prod.id)
        _productPendingDelete.value = null
        showFeedback("Product '${prod.name}' deleted")
    }

    fun toggleProductFeatured(productId: String) {
        repository.toggleProductFeatured(productId)
    }

    fun updateStock(productId: String, newStock: Int) {
        repository.updateProductStock(productId, newStock)
        showFeedback("Stock count adjusted")
    }

    // --- Order Actions ---
    fun updateOrderStatus(orderId: String, newStatus: OrderStatus) {
        repository.updateOrderStatus(orderId, newStatus)
        showFeedback("Order #$orderId updated to ${newStatus.title}")
    }

    fun updateOrderTracking(orderId: String, trackingNumber: String, courier: String) {
        repository.updateOrderTracking(orderId, trackingNumber, courier)
        showFeedback("Tracking assigned: $courier ($trackingNumber)")
    }

    fun cancelOrder(orderId: String, reason: String? = null) {
        repository.cancelOrder(orderId, reason)
        showFeedback("Order #$orderId has been cancelled")
    }

    // --- Customer Actions ---
    fun toggleCustomerActive(customerId: String) {
        repository.toggleCustomerActive(customerId)
        showFeedback("Customer status updated")
    }

    fun updateCustomerTier(customerId: String, tier: MembershipTier) {
        repository.updateCustomerTier(customerId, tier)
        showFeedback("Membership updated to ${tier.label}")
    }

    // --- Cloud Sync / Reset ---
    fun syncWithFirestore() {
        viewModelScope.launch {
            _isCloudSyncing.value = true
            val result = FirebaseConfig.syncBatchToFirestore(
                allProducts.value,
                allOrders.value,
                allCustomers.value
            )
            _isCloudSyncing.value = false
            if (result.isSuccess) {
                showFeedback(result.getOrNull() ?: "Cloud Firestore Sync Completed!")
            } else {
                showFeedback("Ready for Cloud Sync: Local state synchronized. Attach google-services.json to push directly.")
            }
        }
    }

    fun resetCatalogData() {
        repository.resetToDefaultData()
        showFeedback("Catalog restored to M.R GAME ZONE initial seed data.")
    }
}
