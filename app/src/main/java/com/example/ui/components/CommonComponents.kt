package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.DailySalesPoint
import com.example.model.OrderStatus
import com.example.model.PaymentStatus
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
import com.example.ui.theme.StatusProcessing
import com.example.ui.theme.StatusShipped
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextWhite

@Composable
fun GamingCard(
    modifier: Modifier = Modifier,
    borderStroke: BorderStroke? = BorderStroke(1.dp, DarkSurfaceBorder),
    backgroundColor: Color = DarkSurface,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    val cardModifier = if (onClick != null) {
        modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() }
    } else {
        modifier.clip(RoundedCornerShape(16.dp))
    }

    Card(
        modifier = cardModifier,
        shape = RoundedCornerShape(16.dp),
        border = borderStroke,
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        content()
    }
}

@Composable
fun GoldGradientButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    leadingIcon: ImageVector? = null,
    testTag: String = "gold_gradient_btn"
) {
    val brush = if (enabled) {
        Brush.horizontalGradient(
            colors = listOf(
                GoldPrimary,
                GoldSecondary,
                GoldAccent
            )
        )
    } else {
        Brush.horizontalGradient(listOf(Color(0xFF33333E), Color(0xFF33333E)))
    }

    Button(
        onClick = onClick,
        enabled = enabled && !isLoading,
        modifier = modifier
            .testTag(testTag)
            .height(50.dp)
            .background(brush, RoundedCornerShape(12.dp)),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(22.dp),
                color = OnGold,
                strokeWidth = 2.5.dp
            )
        } else {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                if (leadingIcon != null) {
                    Icon(
                        imageVector = leadingIcon,
                        contentDescription = null,
                        tint = OnGold,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(
                    text = text,
                    color = if (enabled) OnGold else TextMuted,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun GoldOutlinedButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leadingIcon: ImageVector? = null,
    color: Color = GoldPrimary
) {
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.height(46.dp),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, if (enabled) color else DarkSurfaceBorder),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = color,
            disabledContentColor = TextMuted
        )
    ) {
        if (leadingIcon != null) {
            Icon(
                imageVector = leadingIcon,
                contentDescription = null,
                tint = if (enabled) color else TextMuted,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
        }
        Text(
            text = text,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun StatKpiCard(
    title: String,
    value: String,
    icon: ImageVector,
    trendText: String,
    isPositiveTrend: Boolean = true,
    modifier: Modifier = Modifier,
    accentColor: Color = GoldPrimary
) {
    GamingCard(
        modifier = modifier,
        borderStroke = BorderStroke(1.dp, accentColor.copy(alpha = 0.35f)),
        backgroundColor = DarkSurfaceElevated
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    color = TextSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .background(accentColor.copy(alpha = 0.15f), RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = value,
                color = TextWhite,
                fontSize = 20.sp,
                fontWeight = FontWeight.Black
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                val trendColor = if (isPositiveTrend) StatusDelivered else StatusCancelled
                Text(
                    text = trendText,
                    color = trendColor,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
fun OrderStatusBadge(
    status: OrderStatus,
    modifier: Modifier = Modifier
) {
    val (badgeBg, badgeBorder, badgeText, color) = when (status) {
        OrderStatus.PENDING -> Quad(StatusPending.copy(alpha = 0.15f), StatusPending.copy(alpha = 0.5f), status.title, StatusPending)
        OrderStatus.PROCESSING -> Quad(StatusProcessing.copy(alpha = 0.15f), StatusProcessing.copy(alpha = 0.5f), status.title, StatusProcessing)
        OrderStatus.SHIPPED -> Quad(StatusShipped.copy(alpha = 0.15f), StatusShipped.copy(alpha = 0.5f), status.title, StatusShipped)
        OrderStatus.DELIVERED -> Quad(StatusDelivered.copy(alpha = 0.15f), StatusDelivered.copy(alpha = 0.5f), status.title, StatusDelivered)
        OrderStatus.CANCELLED -> Quad(StatusCancelled.copy(alpha = 0.15f), StatusCancelled.copy(alpha = 0.5f), status.title, StatusCancelled)
    }

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        color = badgeBg,
        border = BorderStroke(1.dp, badgeBorder)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .background(color, CircleShape)
            )
            Text(
                text = badgeText,
                color = color,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

private data class Quad<A, B, C, D>(val a: A, val b: B, val c: C, val d: D)

@Composable
fun PaymentStatusBadge(status: PaymentStatus) {
    val color = when (status) {
        PaymentStatus.PAID -> StatusDelivered
        PaymentStatus.PENDING -> StatusPending
        PaymentStatus.CASH_ON_DELIVERY -> GoldSecondary
        PaymentStatus.REFUNDED -> StatusCancelled
    }
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = color.copy(alpha = 0.12f),
        border = BorderStroke(1.dp, color.copy(alpha = 0.4f))
    ) {
        Text(
            text = status.title,
            color = color,
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
        )
    }
}

@Composable
fun GamingSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    placeholder: String = "Search...",
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = {
            Text(text = placeholder, color = TextMuted, fontSize = 14.sp)
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = GoldPrimary
            )
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Clear",
                        tint = TextSecondary
                    )
                }
            }
        },
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp),
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = DarkSurfaceElevated,
            unfocusedContainerColor = DarkSurface,
            focusedBorderColor = GoldPrimary,
            unfocusedBorderColor = DarkSurfaceBorder,
            focusedTextColor = TextWhite,
            unfocusedTextColor = TextWhite,
            cursorColor = GoldPrimary
        )
    )
}

@Composable
fun EmptyStateView(
    icon: ImageVector,
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    actionButtonText: String? = null,
    onActionClick: (() -> Unit)? = null
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .background(GoldContainer, CircleShape)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = GoldPrimary,
                modifier = Modifier.size(36.dp)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = title,
            color = TextWhite,
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = subtitle,
            color = TextSecondary,
            fontSize = 13.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        if (actionButtonText != null && onActionClick != null) {
            Spacer(modifier = Modifier.height(20.dp))
            GoldGradientButton(
                text = actionButtonText,
                onClick = onActionClick,
                modifier = Modifier.width(180.dp)
            )
        }
    }
}

@Composable
fun SalesBarChart(
    weeklySales: List<DailySalesPoint>,
    modifier: Modifier = Modifier
) {
    val maxRevenue = weeklySales.maxOfOrNull { it.revenue }?.takeIf { it > 0 } ?: 1.0

    GamingCard(
        modifier = modifier,
        borderStroke = BorderStroke(1.dp, DarkSurfaceBorder),
        backgroundColor = DarkSurfaceElevated
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "WEEKLY REVENUE TREND",
                        color = TextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "$${String.format("%,.2f", weeklySales.sumOf { it.revenue })}",
                        color = GoldPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black
                    )
                }
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = GoldContainer
                ) {
                    Text(
                        text = "Past 7 Days",
                        color = GoldSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Bar chart canvas
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val barCount = weeklySales.size
                    val spacing = size.width / (barCount * 2f + 1f)
                    val barWidth = spacing

                    // Draw subtle grid lines
                    val gridLines = 3
                    for (i in 1..gridLines) {
                        val y = size.height * (i.toFloat() / (gridLines + 1))
                        drawLine(
                            color = Color(0xFF262638),
                            start = Offset(0f, y),
                            end = Offset(size.width, y),
                            strokeWidth = 1.dp.toPx()
                        )
                    }

                    weeklySales.forEachIndexed { index, point ->
                        val barHeight = (point.revenue / maxRevenue).toFloat() * (size.height - 20.dp.toPx())
                        val left = spacing + (index * (barWidth + spacing))
                        val top = size.height - barHeight

                        // Golden gradient bar
                        drawRoundRect(
                            brush = Brush.verticalGradient(
                                colors = listOf(GoldPrimary, GoldAccent, Color(0xFF996515)),
                                startY = top,
                                endY = size.height
                            ),
                            topLeft = Offset(left, top),
                            size = Size(barWidth, barHeight),
                            cornerRadius = CornerRadius(6.dp.toPx(), 6.dp.toPx())
                        )
                    }
                }
            }

            // Day labels row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                weeklySales.forEach { point ->
                    Text(
                        text = point.dayLabel,
                        color = TextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
fun DeleteConfirmationDialog(
    itemName: String,
    itemType: String = "product",
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkSurfaceElevated,
        icon = {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                tint = StatusCancelled,
                modifier = Modifier.size(36.dp)
            )
        },
        title = {
            Text(
                text = "Delete $itemType?",
                color = TextWhite,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        },
        text = {
            Text(
                text = "Are you sure you want to permanently delete \"$itemName\"? This action cannot be reversed.",
                color = TextSecondary,
                fontSize = 14.sp
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = StatusCancelled)
            ) {
                Text("Delete", color = Color.White, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextSecondary)
            }
        }
    )
}
