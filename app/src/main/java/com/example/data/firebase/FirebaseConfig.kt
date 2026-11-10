package com.example.data.firebase

import android.util.Log
import com.example.model.Customer
import com.example.model.Order
import com.example.model.Product
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Prepares and manages Firebase Authentication and Cloud Firestore schema bindings.
 * Automatically checks for existing initialized Firebase configurations
 * and provides safe fallbacks when running in local demonstration mode.
 */
object FirebaseConfig {
    private const val TAG = "GameZoneFirebase"

    // Firestore Collection Names
    const val COLLECTION_PRODUCTS = "products"
    const val COLLECTION_ORDERS = "orders"
    const val COLLECTION_CUSTOMERS = "customers"
    const val COLLECTION_ADMIN_USERS = "admin_users"
    const val COLLECTION_STORE_METRICS = "store_metrics"

    val isFirebaseInitialized: Boolean
        get() = try {
            FirebaseAuth.getInstance().app != null
        } catch (e: Exception) {
            false
        }

    fun getFirestoreInstance(): FirebaseFirestore? {
        return try {
            FirebaseFirestore.getInstance()
        } catch (e: Exception) {
            Log.d(TAG, "Firestore not yet configured with google-services.json: ${e.message}")
            null
        }
    }

    fun getAuthInstance(): FirebaseAuth? {
        return try {
            FirebaseAuth.getInstance()
        } catch (e: Exception) {
            Log.d(TAG, "FirebaseAuth not yet configured with google-services.json: ${e.message}")
            null
        }
    }

    /**
     * Map a Product into a Firestore-ready Document map
     */
    fun productToFirestoreMap(product: Product): Map<String, Any?> = mapOf(
        "id" to product.id,
        "name" to product.name,
        "sku" to product.sku,
        "category" to product.category.name,
        "platform" to product.platform.name,
        "price" to product.price,
        "salePrice" to product.salePrice,
        "stockQuantity" to product.stockQuantity,
        "lowStockThreshold" to product.lowStockThreshold,
        "description" to product.description,
        "specifications" to product.specifications,
        "imageUrl" to product.imageUrl,
        "isFeatured" to product.isFeatured,
        "isActive" to product.isActive,
        "updatedAt" to System.currentTimeMillis()
    )

    /**
     * Map an Order into a Firestore-ready Document map
     */
    fun orderToFirestoreMap(order: Order): Map<String, Any?> = mapOf(
        "id" to order.id,
        "customerId" to order.customerId,
        "customerName" to order.customerName,
        "customerEmail" to order.customerEmail,
        "customerPhone" to order.customerPhone,
        "shippingAddress" to order.shippingAddress,
        "subtotal" to order.subtotal,
        "shippingFee" to order.shippingFee,
        "discountAmount" to order.discountAmount,
        "tax" to order.tax,
        "total" to order.total,
        "orderStatus" to order.orderStatus.name,
        "paymentStatus" to order.paymentStatus.name,
        "paymentMethod" to order.paymentMethod,
        "trackingNumber" to order.trackingNumber,
        "courierName" to order.courierName,
        "timestamp" to order.timestamp,
        "notes" to order.notes,
        "items" to order.items.map { item ->
            mapOf(
                "productId" to item.productId,
                "productName" to item.productName,
                "sku" to item.sku,
                "unitPrice" to item.unitPrice,
                "quantity" to item.quantity,
                "imageUrl" to item.imageUrl
            )
        }
    )

    /**
     * Map a Customer into a Firestore-ready Document map
     */
    fun customerToFirestoreMap(customer: Customer): Map<String, Any?> = mapOf(
        "id" to customer.id,
        "fullName" to customer.fullName,
        "email" to customer.email,
        "phoneNumber" to customer.phoneNumber,
        "address" to customer.address,
        "totalOrders" to customer.totalOrders,
        "totalSpent" to customer.totalSpent,
        "membershipTier" to customer.membershipTier.name,
        "isActive" to customer.isActive,
        "joinedDate" to customer.joinedDate,
        "favoriteCategory" to customer.favoriteCategory
    )

    /**
     * Sync local dataset to Cloud Firestore if connected
     */
    suspend fun syncBatchToFirestore(
        products: List<Product>,
        orders: List<Order>,
        customers: List<Customer>
    ): Result<String> = withContext(Dispatchers.IO) {
        val firestore = getFirestoreInstance()
            ?: return@withContext Result.failure(
                IllegalStateException("Firebase Firestore is in offline readiness mode. Connect your google-services.json to sync live.")
            )

        try {
            val batch = firestore.batch()
            for (p in products) {
                val doc = firestore.collection(COLLECTION_PRODUCTS).document(p.id)
                batch.set(doc, productToFirestoreMap(p))
            }
            for (o in orders) {
                val doc = firestore.collection(COLLECTION_ORDERS).document(o.id)
                batch.set(doc, orderToFirestoreMap(o))
            }
            for (c in customers) {
                val doc = firestore.collection(COLLECTION_CUSTOMERS).document(c.id)
                batch.set(doc, customerToFirestoreMap(c))
            }
            // Commit batch
            batch.commit()
            Result.success("Successfully synchronized ${products.size} products, ${orders.size} orders, and ${customers.size} customers to Firestore!")
        } catch (e: Exception) {
            Log.e(TAG, "Firestore sync failed", e)
            Result.failure(e)
        }
    }
}
