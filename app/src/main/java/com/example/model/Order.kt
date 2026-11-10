package com.example.model

enum class OrderStatus(val title: String) {
    PENDING("Pending"),
    PROCESSING("Processing"),
    SHIPPED("Shipped"),
    DELIVERED("Delivered"),
    CANCELLED("Cancelled")
}

enum class PaymentStatus(val title: String) {
    PAID("Paid"),
    PENDING("Payment Pending"),
    CASH_ON_DELIVERY("Cash On Delivery"),
    REFUNDED("Refunded")
}

data class OrderItem(
    val productId: String,
    val productName: String,
    val sku: String,
    val unitPrice: Double,
    val quantity: Int,
    val imageUrl: String = ""
) {
    val subtotal: Double get() = unitPrice * quantity
}

data class Order(
    val id: String,
    val customerId: String,
    val customerName: String,
    val customerEmail: String,
    val customerPhone: String,
    val shippingAddress: String,
    val items: List<OrderItem>,
    val subtotal: Double,
    val shippingFee: Double = 0.0,
    val discountAmount: Double = 0.0,
    val tax: Double = 0.0,
    val total: Double,
    val orderStatus: OrderStatus,
    val paymentStatus: PaymentStatus,
    val paymentMethod: String,
    val trackingNumber: String? = null,
    val courierName: String? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val notes: String? = null
)
