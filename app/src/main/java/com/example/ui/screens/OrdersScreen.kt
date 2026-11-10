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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Order
import com.example.model.OrderStatus
import com.example.ui.components.EmptyStateView
import com.example.ui.components.GamingCard
import com.example.ui.components.GamingSearchBar
import com.example.ui.components.OrderStatusBadge
import com.example.ui.components.PaymentStatusBadge
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceBorder
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.GoldContainer
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.GoldSecondary
import com.example.ui.theme.OnGold
import com.example.ui.theme.TextLight
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextWhite
import com.example.ui.viewmodel.AdminViewModel
import com.example.ui.viewmodel.CurrentScreen
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun OrdersScreen(
    viewModel: AdminViewModel,
    modifier: Modifier = Modifier
) {
    val orders by viewModel.filteredOrders.collectAsState()
    val allOrdersCount by viewModel.allOrders.collectAsState()
    val searchQuery by viewModel.orderSearchQuery.collectAsState()
    val selectedStatus by viewModel.selectedOrderStatus.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        // Title Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "ORDERS FULFILLMENT",
                    color = GoldPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "${orders.size} of ${allOrdersCount.size} orders showing",
                    color = TextSecondary,
                    fontSize = 12.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Search Bar
        GamingSearchBar(
            query = searchQuery,
            onQueryChange = { viewModel.setOrderSearchQuery(it) },
            placeholder = "Search order ID (#MR-...), customer, courier..."
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Status Tabs
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            item {
                val isAllSelected = selectedStatus == null
                FilterChip(
                    selected = isAllSelected,
                    onClick = { viewModel.setOrderStatusFilter(null) },
                    label = { Text("All Orders") },
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
                        selected = isAllSelected
                    )
                )
            }
            items(OrderStatus.values()) { status ->
                val isSelected = selectedStatus == status
                FilterChip(
                    selected = isSelected,
                    onClick = { viewModel.setOrderStatusFilter(status) },
                    label = { Text(status.title) },
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

        Spacer(modifier = Modifier.height(12.dp))

        // Orders List or Empty State
        if (orders.isEmpty()) {
            EmptyStateView(
                icon = Icons.Default.SearchOff,
                title = "No Orders Found",
                subtitle = "No customer orders match the current status filter or query.",
                actionButtonText = "Show All Orders",
                onActionClick = {
                    viewModel.setOrderSearchQuery("")
                    viewModel.setOrderStatusFilter(null)
                }
            )
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(orders, key = { it.id }) { order ->
                    OrderItemCard(
                        order = order,
                        onClick = { viewModel.navigateTo(CurrentScreen.OrderDetail(order.id)) }
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(90.dp))
                }
            }
        }
    }
}

@Composable
fun OrderItemCard(
    order: Order,
    onClick: () -> Unit
) {
    val dateStr = remember(order.timestamp) {
        val sdf = SimpleDateFormat("MMM dd, yyyy • hh:mm a", Locale.getDefault())
        sdf.format(Date(order.timestamp))
    }

    GamingCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        backgroundColor = DarkSurfaceElevated,
        borderStroke = BorderStroke(1.dp, DarkSurfaceBorder)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header Row: ID, Date, Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "#${order.id}",
                        color = GoldPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    PaymentStatusBadge(status = order.paymentStatus)
                }
                OrderStatusBadge(status = order.orderStatus)
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = dateStr,
                color = TextMuted,
                fontSize = 11.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Customer Name & Shipping City
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = order.customerName,
                        color = TextWhite,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${order.items.size} item(s): " + order.items.joinToString(", ") { "${it.quantity}x ${it.productName}" },
                        color = TextSecondary,
                        fontSize = 12.sp,
                        maxLines = 1
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "$${String.format("%.2f", order.total)}",
                        color = GoldPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = order.paymentMethod,
                        color = TextMuted,
                        fontSize = 11.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Footer row with Courier info & arrow
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (!order.courierName.isNullOrBlank()) {
                    Text(
                        text = "Courier: ${order.courierName}",
                        color = GoldSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                } else {
                    Text(
                        text = "Pending courier assignment",
                        color = TextMuted,
                        fontSize = 11.sp
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Manage Order",
                        color = GoldPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = GoldPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}
