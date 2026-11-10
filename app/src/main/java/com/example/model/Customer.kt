package com.example.model

enum class MembershipTier(val label: String) {
    VIP_GOLD("VIP Gold Gamer"),
    ELITE("Elite Member"),
    PRO("Pro Gamer"),
    STANDARD("Standard")
}

data class Customer(
    val id: String,
    val fullName: String,
    val email: String,
    val phoneNumber: String,
    val address: String,
    val totalOrders: Int,
    val totalSpent: Double,
    val membershipTier: MembershipTier = MembershipTier.STANDARD,
    val isActive: Boolean = true,
    val joinedDate: String = "Jan 2026",
    val favoriteCategory: String = "PlayStation"
)
