package com.example.model

data class AdminUser(
    val id: String,
    val email: String,
    val displayName: String,
    val role: String = "Super Administrator",
    val avatarUrl: String = "",
    val storeName: String = "M.R GAME ZONE",
    val permissions: List<String> = listOf("ALL_ACCESS", "PRODUCT_WRITE", "ORDER_MANAGEMENT", "FINANCE_VIEW")
)

data class DailySalesPoint(
    val dayLabel: String,
    val revenue: Double,
    val ordersCount: Int
)

data class DashboardStats(
    val totalRevenue: Double,
    val monthlyRevenueGrowthPercent: Double,
    val totalOrdersCount: Int,
    val pendingOrdersCount: Int,
    val processingOrdersCount: Int,
    val completedOrdersCount: Int,
    val totalCustomersCount: Int,
    val lowStockItemsCount: Int,
    val weeklySales: List<DailySalesPoint>
)
