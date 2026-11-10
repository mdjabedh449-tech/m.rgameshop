package com.example.model

enum class ProductCategory(val displayName: String) {
    ALL("All Products"),
    CONSOLES("Gaming Consoles"),
    VIDEO_GAMES("Video Games"),
    ACCESSORIES("Accessories"),
    GAMING_PCS("Gaming PCs & Laptops"),
    DIGITAL_KEYS("Digital Keys & Subscriptions"),
    MERCHANDISE("Gamer Gear & Merch")
}

enum class GamingPlatform(val tag: String) {
    UNIVERSAL("Universal"),
    PS5("PlayStation 5"),
    XBOX("Xbox Series X|S"),
    PC("PC / Steam"),
    NINTENDO("Nintendo Switch"),
    VR("VR Hardware")
}

data class Product(
    val id: String,
    val name: String,
    val sku: String,
    val category: ProductCategory,
    val platform: GamingPlatform,
    val price: Double,
    val salePrice: Double? = null,
    val stockQuantity: Int,
    val lowStockThreshold: Int = 5,
    val description: String,
    val specifications: Map<String, String> = emptyMap(),
    val imageUrl: String = "",
    val isFeatured: Boolean = false,
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
) {
    val isLowStock: Boolean get() = stockQuantity in 1..lowStockThreshold
    val isOutOfStock: Boolean get() = stockQuantity <= 0
    val currentPrice: Double get() = salePrice ?: price
}
