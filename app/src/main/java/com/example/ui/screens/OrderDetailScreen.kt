package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.model.Order
import com.example.model.OrderStatus
import com.example.ui.components.GamingCard
import com.example.ui.components.GoldGradientButton
import com.example.ui.components.GoldOutlinedButton
import com.example.ui.components.OrderStatusBadge
import com.example.ui.components.PaymentStatusBadge
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
import com.example.ui.theme.StatusPending
import com.example.ui.theme.StatusProcessing
import com.example.ui.theme.StatusShipped
import com.example.ui.theme.TextLight
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextWhite
import com.example.ui.viewmodel.AdminViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailScreen(
    orderId: String,
    viewModel: AdminViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val orders by viewModel.allOrders.collectAsState()
    val order = orders.find { it.id == orderId }

    if (order == null) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(DarkBackground)
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Order Not Found", color = TextWhite, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))
            GoldGradientButton(text = "Back to Orders", onClick = { viewModel.navigateBack() })
        }
        return
    }

    var trackingInput by remember { mutableStateOf(order.trackingNumber ?: "") }
    var courierInput by remember { mutableStateOf(order.courierName ?: "GameZone Express Freight") }
    var statusMenuExpanded by remember { mutableStateOf(false) }

    val formattedDate = remember(order.timestamp) {
        val sdf = SimpleDateFormat("MMMM dd, yyyy • hh:mm a", Locale.getDefault())
        sdf.format(Date(order.timestamp))
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = DarkBackground,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "ORDER #${order.id}",
                        color = GoldPrimary,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateBack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = GoldPrimary
                        )
                    }
                },
                actions = {
                    OrderStatusBadge(status = order.orderStatus, modifier = Modifier.padding(end = 12.dp))
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkSurface
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Order Status Stepper
            GamingCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = DarkSurfaceElevated,
                borderStroke = BorderStroke(1.dp, DarkSurfaceBorder)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "ORDER FULFILLMENT STATUS",
                        color = GoldSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    // Status Timeline Visualizer
                    val steps = listOf(
                        OrderStatus.PENDING,
                        OrderStatus.PROCESSING,
                        OrderStatus.SHIPPED,
                        OrderStatus.DELIVERED
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        steps.forEachIndexed { index, step ->
                            val isCompleted = order.orderStatus.ordinal >= step.ordinal && order.orderStatus != OrderStatus.CANCELLED
                            val isCurrent = order.orderStatus == step

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .background(
                                            if (isCompleted) GoldPrimary else DarkSurface,
                                            CircleShape
                                        )
                                        .border(
                                            1.dp,
                                            if (isCurrent) GoldAccent else DarkSurfaceBorder,
                                            CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isCompleted) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            tint = OnGold,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    } else {
                                        Text(
                                            text = "${index + 1}",
                                            color = TextMuted,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = step.title,
                                    color = if (isCompleted) GoldSecondary else TextMuted,
                                    fontSize = 10.sp,
                                    fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Update Status Quick Dropdown Selector
                    Text(
                        text = "Change Status:",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(OrderStatus.values()) { statusOption ->
                            val isSelected = order.orderStatus == statusOption
                            Surface(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable {
                                        viewModel.updateOrderStatus(order.id, statusOption)
                                    },
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSelected) GoldContainer else DarkSurface,
                                border = BorderStroke(1.dp, if (isSelected) GoldPrimary else DarkSurfaceBorder)
                            ) {
                                Text(
                                    text = statusOption.title,
                                    color = if (isSelected) GoldPrimary else TextLight,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Shipping & Tracking Section
            GamingCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = DarkSurfaceElevated
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LocalShipping, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "COURIER & TRACKING",
                                color = GoldSecondary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = courierInput,
                        onValueChange = { courierInput = it },
                        label = { Text("Logistics Carrier / Courier", color = TextSecondary) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = DarkSurface,
                            unfocusedContainerColor = DarkSurface,
                            focusedBorderColor = GoldPrimary,
                            unfocusedBorderColor = DarkSurfaceBorder,
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = trackingInput,
                        onValueChange = { trackingInput = it },
                        label = { Text("Waybill / Tracking Number", color = TextSecondary) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = DarkSurface,
                            unfocusedContainerColor = DarkSurface,
                            focusedBorderColor = GoldPrimary,
                            unfocusedBorderColor = DarkSurfaceBorder,
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    GoldOutlinedButton(
                        text = "Update Tracking Info",
                        onClick = {
                            viewModel.updateOrderTracking(order.id, trackingInput, courierInput)
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // Customer Details Card
            GamingCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = DarkSurfaceElevated
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Person, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "CUSTOMER & SHIPPING DESTINATION",
                                color = GoldSecondary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(text = order.customerName, color = TextWhite, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    Text(text = order.customerEmail, color = TextSecondary, fontSize = 13.sp)
                    Text(text = order.customerPhone, color = TextSecondary, fontSize = 13.sp)

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Shipping Address:", color = TextMuted, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    Text(text = order.shippingAddress, color = TextLight, fontSize = 13.sp)

                    if (!order.notes.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "Customer Note:", color = GoldAccent, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        Text(text = order.notes, color = TextLight, fontSize = 12.sp)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Contact Actions
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        GoldOutlinedButton(
                            text = "Call",
                            leadingIcon = Icons.Default.Call,
                            onClick = {
                                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${order.customerPhone}"))
                                context.startActivity(intent)
                            },
                            modifier = Modifier.weight(1f)
                        )
                        GoldOutlinedButton(
                            text = "Email",
                            leadingIcon = Icons.Default.Email,
                            onClick = {
                                val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:${order.customerEmail}")).apply {
                                    putExtra(Intent.EXTRA_SUBJECT, "Update on M.R Game Zone Order #${order.id}")
                                }
                                context.startActivity(intent)
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Ordered Items Card
            GamingCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = DarkSurfaceElevated
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "ORDER ITEMS (${order.items.size})",
                        color = GoldSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    order.items.forEach { item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(DarkSurface),
                                contentAlignment = Alignment.Center
                            ) {
                                if (item.imageUrl.isNotBlank()) {
                                    AsyncImage(
                                        model = ImageRequest.Builder(context).data(item.imageUrl).crossfade(true).build(),
                                        contentDescription = item.productName,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                } else {
                                    Icon(Icons.Default.Receipt, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(24.dp))
                                }
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = item.productName,
                                    color = TextWhite,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    maxLines = 1
                                )
                                Text(
                                    text = "${item.quantity} × $${String.format("%.2f", item.unitPrice)}",
                                    color = TextSecondary,
                                    fontSize = 12.sp
                                )
                            }

                            Text(
                                text = "$${String.format("%.2f", item.subtotal)}",
                                color = GoldPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        HorizontalDivider(color = DarkSurfaceBorder, thickness = 0.5.dp)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Billing Calculation
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Subtotal", color = TextSecondary, fontSize = 12.sp)
                        Text("$${String.format("%.2f", order.subtotal)}", color = TextLight, fontSize = 12.sp)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Shipping", color = TextSecondary, fontSize = 12.sp)
                        Text(if (order.shippingFee > 0) "$${String.format("%.2f", order.shippingFee)}" else "FREE", color = TextLight, fontSize = 12.sp)
                    }
                    if (order.discountAmount > 0) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Discount / Coupon", color = StatusDelivered, fontSize = 12.sp)
                            Text("-$${String.format("%.2f", order.discountAmount)}", color = StatusDelivered, fontSize = 12.sp)
                        }
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Estimated Tax", color = TextSecondary, fontSize = 12.sp)
                        Text("$${String.format("%.2f", order.tax)}", color = TextLight, fontSize = 12.sp)
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider(color = DarkSurfaceBorder)
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Grand Total", color = TextWhite, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        Text(
                            text = "$${String.format("%.2f", order.total)}",
                            color = GoldPrimary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }

            // Cancel Order Action
            if (order.orderStatus != OrderStatus.CANCELLED) {
                Button(
                    onClick = { viewModel.cancelOrder(order.id, "Cancelled by Admin Console") },
                    colors = ButtonDefaults.buttonColors(containerColor = StatusCancelled.copy(alpha = 0.15f)),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, StatusCancelled.copy(alpha = 0.4f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                ) {
                    Icon(imageVector = Icons.Default.Cancel, contentDescription = null, tint = StatusCancelled, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Cancel Order", color = StatusCancelled, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}
