package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.OrderStatus
import com.example.ui.components.GamingCard
import com.example.ui.components.OrderStatusBadge
import com.example.ui.components.SalesBarChart
import com.example.ui.components.StatKpiCard
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceBorder
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldContainer
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.GoldSecondary
import com.example.ui.theme.OnGold
import com.example.ui.theme.StatusDelivered
import com.example.ui.theme.StatusLowStock
import com.example.ui.theme.StatusPending
import com.example.ui.theme.TextLight
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextWhite
import com.example.ui.viewmodel.AdminViewModel
import com.example.ui.viewmodel.CurrentScreen
import com.example.ui.viewmodel.NavigationTab

@Composable
fun DashboardScreen(
    viewModel: AdminViewModel,
    modifier: Modifier = Modifier
) {
    val stats by viewModel.dashboardStats.collectAsState()
    val admin by viewModel.currentAdmin.collectAsState()
    val products by viewModel.allProducts.collectAsState()
    val orders by viewModel.allOrders.collectAsState()
    val isSyncing by viewModel.isCloudSyncing.collectAsState()

    val lowStockProducts = products.filter { it.isLowStock || it.isOutOfStock }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            // Admin Top Header Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .border(1.5.dp, GoldPrimary, CircleShape)
                            .background(DarkSurfaceElevated),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_mr_game_icon),
                            contentDescription = "Emblem",
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "M.R GAME ZONE",
                                color = GoldPrimary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp
                            )
                        }
                        Text(
                            text = admin?.displayName ?: "Super Admin",
                            color = TextSecondary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Online Store Pill
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = StatusDelivered.copy(alpha = 0.12f),
                        border = BorderStroke(1.dp, StatusDelivered.copy(alpha = 0.4f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .background(StatusDelivered, CircleShape)
                            )
                            Text(
                                text = "LIVE",
                                color = StatusDelivered,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(onClick = {
                        viewModel.showFeedback("All systems operational. No unread critical alerts.")
                    }) {
                        BadgedBox(
                            badge = {
                                Badge(containerColor = GoldPrimary) {
                                    Text(
                                        text = "${stats.pendingOrdersCount}",
                                        color = OnGold,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "Alerts",
                                tint = TextWhite
                            )
                        }
                    }
                }
            }
        }

        // Quick Actions Row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Add Product Quick Button
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { viewModel.navigateTo(CurrentScreen.AddProduct) },
                    shape = RoundedCornerShape(12.dp),
                    color = GoldContainer,
                    border = BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.4f))
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 12.dp, horizontal = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            tint = GoldPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Add Product",
                            color = GoldPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Cloud Sync Quick Button
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { viewModel.syncWithFirestore() },
                    shape = RoundedCornerShape(12.dp),
                    color = DarkSurfaceElevated,
                    border = BorderStroke(1.dp, DarkSurfaceBorder)
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 12.dp, horizontal = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CloudSync,
                            contentDescription = null,
                            tint = GoldSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isSyncing) "Syncing..." else "Cloud Sync",
                            color = TextLight,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // KPI Stats Grid (Row 1)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatKpiCard(
                    title = "TOTAL REVENUE",
                    value = "$${String.format("%,.2f", stats.totalRevenue)}",
                    icon = Icons.Default.AttachMoney,
                    trendText = "+${stats.monthlyRevenueGrowthPercent}% vs last mo.",
                    isPositiveTrend = true,
                    accentColor = GoldPrimary,
                    modifier = Modifier.weight(1f)
                )
                StatKpiCard(
                    title = "PENDING ORDERS",
                    value = "${stats.pendingOrdersCount}",
                    icon = Icons.Default.HourglassTop,
                    trendText = "${stats.totalOrdersCount} Total Orders",
                    isPositiveTrend = stats.pendingOrdersCount == 0,
                    accentColor = StatusPending,
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            viewModel.navigateToTab(NavigationTab.ORDERS)
                            viewModel.setOrderStatusFilter(OrderStatus.PENDING)
                        }
                )
            }
        }

        // KPI Stats Grid (Row 2)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatKpiCard(
                    title = "TOTAL CUSTOMERS",
                    value = "${stats.totalCustomersCount}",
                    icon = Icons.Default.People,
                    trendText = "+56 VIP Gamers",
                    isPositiveTrend = true,
                    accentColor = GoldSecondary,
                    modifier = Modifier.weight(1f)
                )
                StatKpiCard(
                    title = "LOW STOCK ALERTS",
                    value = "${stats.lowStockItemsCount}",
                    icon = Icons.Default.Warning,
                    trendText = if (stats.lowStockItemsCount > 0) "Restock needed" else "Healthy inventory",
                    isPositiveTrend = stats.lowStockItemsCount == 0,
                    accentColor = if (stats.lowStockItemsCount > 0) StatusLowStock else StatusDelivered,
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            viewModel.navigateToTab(NavigationTab.PRODUCTS)
                            viewModel.setStockFilter("LOW_STOCK")
                        }
                )
            }
        }

        // Low Stock Alert Banner (if any)
        if (lowStockProducts.isNotEmpty()) {
            item {
                GamingCard(
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = DarkSurfaceElevated,
                    borderStroke = BorderStroke(1.dp, StatusLowStock.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = StatusLowStock,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Inventory Attention Needed",
                                    color = TextWhite,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            TextButton(onClick = {
                                viewModel.navigateToTab(NavigationTab.PRODUCTS)
                                viewModel.setStockFilter("LOW_STOCK")
                            }) {
                                Text(
                                    text = "Manage All",
                                    color = GoldPrimary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(lowStockProducts) { product ->
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = DarkSurface,
                                    border = BorderStroke(1.dp, DarkSurfaceBorder),
                                    modifier = Modifier.width(220.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(10.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = product.name,
                                                color = TextWhite,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                maxLines = 1
                                            )
                                            Text(
                                                text = if (product.isOutOfStock) "Out of Stock" else "Only ${product.stockQuantity} left",
                                                color = if (product.isOutOfStock) StatusLowStock else StatusPending,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Medium
                                            )
                                        }
                                        TextButton(onClick = {
                                            viewModel.updateStock(product.id, product.stockQuantity + 10)
                                        }) {
                                            Text("+10", color = GoldPrimary, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Sales Performance Canvas Chart
        item {
            SalesBarChart(
                weeklySales = stats.weeklySales,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Recent Orders Section
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "RECENT ORDERS",
                    color = GoldSecondary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                TextButton(onClick = {
                    viewModel.navigateToTab(NavigationTab.ORDERS)
                }) {
                    Text(
                        text = "View All (${orders.size})",
                        color = GoldPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        items(orders.take(4)) { order ->
            GamingCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        viewModel.navigateTo(CurrentScreen.OrderDetail(order.id))
                    },
                backgroundColor = DarkSurfaceElevated,
                borderStroke = BorderStroke(1.dp, DarkSurfaceBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(GoldContainer, RoundedCornerShape(10.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.ReceiptLong,
                                contentDescription = null,
                                tint = GoldPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "#${order.id}",
                                    color = TextWhite,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "• ${order.items.size} item(s)",
                                    color = TextMuted,
                                    fontSize = 12.sp
                                )
                            }
                            Text(
                                text = order.customerName,
                                color = TextSecondary,
                                fontSize = 12.sp
                            )
                        }
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "$${String.format("%.2f", order.total)}",
                            color = GoldPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        OrderStatusBadge(status = order.orderStatus)
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(80.dp)) // Space for bottom bar
        }
    }
}
