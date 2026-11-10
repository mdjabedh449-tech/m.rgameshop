package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.model.Product
import com.example.model.ProductCategory
import com.example.ui.components.DeleteConfirmationDialog
import com.example.ui.components.EmptyStateView
import com.example.ui.components.GamingCard
import com.example.ui.components.GamingSearchBar
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceBorder
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldContainer
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.GoldSecondary
import com.example.ui.theme.OnGold
import com.example.ui.theme.StatusCancelled
import com.example.ui.theme.StatusDelivered
import com.example.ui.theme.StatusLowStock
import com.example.ui.theme.StatusPending
import com.example.ui.theme.TextLight
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextWhite
import com.example.ui.viewmodel.AdminViewModel
import com.example.ui.viewmodel.CurrentScreen

@Composable
fun ProductsScreen(
    viewModel: AdminViewModel,
    modifier: Modifier = Modifier
) {
    val products by viewModel.filteredProducts.collectAsState()
    val allProductsCount by viewModel.allProducts.collectAsState()
    val searchQuery by viewModel.productSearchQuery.collectAsState()
    val selectedCategory by viewModel.selectedProductCategory.collectAsState()
    val stockFilter by viewModel.stockFilter.collectAsState()
    val pendingDelete by viewModel.productPendingDelete.collectAsState()

    // Delete dialog if triggered
    pendingDelete?.let { prod ->
        DeleteConfirmationDialog(
            itemName = prod.name,
            itemType = "product",
            onConfirm = { viewModel.confirmDeleteProduct() },
            onDismiss = { viewModel.cancelDeleteProduct() }
        )
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = DarkBackground,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.navigateTo(CurrentScreen.AddProduct) },
                containerColor = GoldPrimary,
                contentColor = OnGold,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .padding(bottom = 72.dp)
                    .testTag("add_product_fab")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Add Product")
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Add Item", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // Top Title & Inventory Stats
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "PRODUCTS CATALOG",
                        color = GoldPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "${products.size} of ${allProductsCount.size} items listed",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Search Bar
            GamingSearchBar(
                query = searchQuery,
                onQueryChange = { viewModel.setProductSearchQuery(it) },
                placeholder = "Search by gear name, SKU, platform..."
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Category Filter Chips
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(ProductCategory.values()) { category ->
                    val isSelected = selectedCategory == category
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.setProductCategory(category) },
                        label = {
                            Text(
                                text = category.displayName,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = DarkSurfaceElevated,
                            selectedContainerColor = GoldPrimary,
                            labelColor = TextLight,
                            selectedLabelColor = OnGold
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            borderColor = DarkSurfaceBorder,
                            selectedBorderColor = GoldPrimary,
                            enabled = true,
                            selected = isSelected
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Stock Status Filter Chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                val filters = listOf(
                    "ALL" to "All",
                    "IN_STOCK" to "In Stock",
                    "LOW_STOCK" to "Low Stock",
                    "OUT_OF_STOCK" to "Out of Stock"
                )
                filters.forEach { (key, label) ->
                    val isSelected = stockFilter == key
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { viewModel.setStockFilter(key) },
                        shape = RoundedCornerShape(8.dp),
                        color = if (isSelected) GoldContainer else DarkSurface,
                        border = BorderStroke(
                            1.dp,
                            if (isSelected) GoldPrimary else DarkSurfaceBorder
                        )
                    ) {
                        Text(
                            text = label,
                            color = if (isSelected) GoldPrimary else TextSecondary,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Product List or Empty State
            if (products.isEmpty()) {
                EmptyStateView(
                    icon = Icons.Default.SearchOff,
                    title = "No Gaming Products Found",
                    subtitle = "Try adjusting your search query or switching category filters.",
                    actionButtonText = "Reset Filters",
                    onActionClick = {
                        viewModel.setProductSearchQuery("")
                        viewModel.setProductCategory(ProductCategory.ALL)
                        viewModel.setStockFilter("ALL")
                    }
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(products, key = { it.id }) { product ->
                        ProductItemCard(
                            product = product,
                            onEdit = { viewModel.navigateTo(CurrentScreen.EditProduct(product)) },
                            onDelete = { viewModel.requestDeleteProduct(product) },
                            onToggleFeatured = { viewModel.toggleProductFeatured(product.id) }
                        )
                    }
                    item {
                        Spacer(modifier = Modifier.height(90.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun ProductItemCard(
    product: Product,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onToggleFeatured: () -> Unit
) {
    val context = LocalContext.current

    GamingCard(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = DarkSurfaceElevated,
        borderStroke = BorderStroke(1.dp, if (product.isFeatured) GoldPrimary.copy(alpha = 0.5f) else DarkSurfaceBorder)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                // Product Image Thumbnail
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(DarkSurface),
                    contentAlignment = Alignment.Center
                ) {
                    if (product.imageUrl.isNotBlank()) {
                        AsyncImage(
                            model = ImageRequest.Builder(context)
                                .data(product.imageUrl)
                                .crossfade(true)
                                .build(),
                            contentDescription = product.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.SportsEsports,
                            contentDescription = null,
                            tint = GoldSecondary,
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    // Platform tag overlay
                    Surface(
                        shape = RoundedCornerShape(topStart = 0.dp, bottomEnd = 8.dp),
                        color = Color.Black.copy(alpha = 0.75f),
                        modifier = Modifier.align(Alignment.TopStart)
                    ) {
                        Text(
                            text = product.platform.name,
                            color = GoldSecondary,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Info & Price Column
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = product.sku,
                            color = TextMuted,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                        IconButton(
                            onClick = onToggleFeatured,
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = if (product.isFeatured) Icons.Filled.Star else Icons.Outlined.StarOutline,
                                contentDescription = "Featured",
                                tint = if (product.isFeatured) GoldPrimary else TextMuted,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Text(
                        text = product.name,
                        color = TextWhite,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // Price & Stock row
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "$${String.format("%.2f", product.currentPrice)}",
                            color = GoldPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black
                        )
                        if (product.salePrice != null) {
                            Text(
                                text = "$${String.format("%.2f", product.price)}",
                                color = TextMuted,
                                fontSize = 12.sp,
                                textDecoration = TextDecoration.LineThrough
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Footer with Stock indicator and action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Stock Pill
                val (stockBg, stockColor, stockLabel) = when {
                    product.isOutOfStock -> Triple(StatusCancelled.copy(alpha = 0.15f), StatusCancelled, "Out of Stock")
                    product.isLowStock -> Triple(StatusLowStock.copy(alpha = 0.15f), StatusLowStock, "Low: ${product.stockQuantity} left")
                    else -> Triple(StatusDelivered.copy(alpha = 0.15f), StatusDelivered, "In Stock: ${product.stockQuantity}")
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = stockBg,
                    border = BorderStroke(1.dp, stockColor.copy(alpha = 0.4f))
                ) {
                    Text(
                        text = stockLabel,
                        color = stockColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }

                // Edit & Delete Action Buttons
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onEdit() },
                        color = GoldContainer,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit",
                                tint = GoldPrimary,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "Edit", color = GoldPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(30.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = StatusCancelled,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}
