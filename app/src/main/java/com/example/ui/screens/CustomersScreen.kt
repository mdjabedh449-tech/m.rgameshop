package com.example.ui.screens

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Customer
import com.example.model.MembershipTier
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
import com.example.ui.theme.StatusProcessing
import com.example.ui.theme.TextLight
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextWhite
import com.example.ui.viewmodel.AdminViewModel

@Composable
fun CustomersScreen(
    viewModel: AdminViewModel,
    modifier: Modifier = Modifier
) {
    val customers by viewModel.filteredCustomers.collectAsState()
    val allCustomers by viewModel.allCustomers.collectAsState()
    val searchQuery by viewModel.customerSearchQuery.collectAsState()

    val vipCount = allCustomers.count { it.membershipTier == MembershipTier.VIP_GOLD }
    val totalSpentSum = allCustomers.sumOf { it.totalSpent }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        // Title & Summary Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "GAMER CLIENTELE",
                    color = GoldPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "${allCustomers.size} Registered Gamers • $vipCount VIP Gold",
                    color = TextSecondary,
                    fontSize = 12.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Search Bar
        GamingSearchBar(
            query = searchQuery,
            onQueryChange = { viewModel.setCustomerSearchQuery(it) },
            placeholder = "Search gamer name, email, phone..."
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Customer List or Empty State
        if (customers.isEmpty()) {
            EmptyStateView(
                icon = Icons.Default.SearchOff,
                title = "No Customers Found",
                subtitle = "No customer matches your search criteria.",
                actionButtonText = "Clear Search",
                onActionClick = { viewModel.setCustomerSearchQuery("") }
            )
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(customers, key = { it.id }) { customer ->
                    CustomerCard(
                        customer = customer,
                        onToggleActive = { viewModel.toggleCustomerActive(customer.id) },
                        onUpdateTier = { tier -> viewModel.updateCustomerTier(customer.id, tier) }
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
fun CustomerCard(
    customer: Customer,
    onToggleActive: () -> Unit,
    onUpdateTier: (MembershipTier) -> Unit
) {
    var tierMenuExpanded by remember { mutableStateOf(false) }

    GamingCard(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = DarkSurfaceElevated,
        borderStroke = BorderStroke(
            1.dp,
            if (customer.membershipTier == MembershipTier.VIP_GOLD) GoldPrimary.copy(alpha = 0.5f) else DarkSurfaceBorder
        )
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Top row: Avatar, Info, Status/Tier
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Gamer Avatar
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(GoldContainer)
                        .border(
                            1.dp,
                            if (customer.membershipTier == MembershipTier.VIP_GOLD) GoldPrimary else DarkSurfaceBorder,
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = customer.fullName.split(" ").mapNotNull { it.firstOrNull()?.toString() }.take(2).joinToString(""),
                        color = GoldPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = customer.fullName,
                            color = TextWhite,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        if (customer.membershipTier == MembershipTier.VIP_GOLD) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                imageVector = Icons.Default.WorkspacePremium,
                                contentDescription = "VIP",
                                tint = GoldPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                    Text(text = customer.email, color = TextSecondary, fontSize = 12.sp)
                    Text(text = customer.phoneNumber, color = TextMuted, fontSize = 11.sp)
                }

                // Tier Pill with Dropdown
                Box {
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { tierMenuExpanded = true },
                        color = when (customer.membershipTier) {
                            MembershipTier.VIP_GOLD -> GoldPrimary.copy(alpha = 0.2f)
                            MembershipTier.ELITE -> StatusProcessing.copy(alpha = 0.2f)
                            MembershipTier.PRO -> GoldSecondary.copy(alpha = 0.2f)
                            MembershipTier.STANDARD -> DarkSurface
                        },
                        border = BorderStroke(
                            1.dp,
                            when (customer.membershipTier) {
                                MembershipTier.VIP_GOLD -> GoldPrimary
                                MembershipTier.ELITE -> StatusProcessing
                                MembershipTier.PRO -> GoldSecondary
                                MembershipTier.STANDARD -> DarkSurfaceBorder
                            }
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = customer.membershipTier.label,
                            color = when (customer.membershipTier) {
                                MembershipTier.VIP_GOLD -> GoldPrimary
                                MembershipTier.ELITE -> StatusProcessing
                                MembershipTier.PRO -> GoldSecondary
                                MembershipTier.STANDARD -> TextLight
                            },
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    DropdownMenu(
                        expanded = tierMenuExpanded,
                        onDismissRequest = { tierMenuExpanded = false }
                    ) {
                        MembershipTier.values().forEach { tier ->
                            DropdownMenuItem(
                                text = { Text(tier.label) },
                                onClick = {
                                    onUpdateTier(tier)
                                    tierMenuExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Stats row: Orders count, Lifetime Spend, Status
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DarkSurface, RoundedCornerShape(8.dp))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = "ORDERS", color = TextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Text(text = "${customer.totalOrders}", color = TextWhite, fontSize = 13.sp, fontWeight = FontWeight.Black)
                }

                Column {
                    Text(text = "TOTAL SPENT", color = TextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Text(
                        text = "$${String.format("%,.2f", customer.totalSpent)}",
                        color = GoldPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black
                    )
                }

                Column {
                    Text(text = "STATUS", color = TextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Row(
                        modifier = Modifier.clickable { onToggleActive() },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (customer.isActive) Icons.Default.CheckCircle else Icons.Default.Block,
                            contentDescription = null,
                            tint = if (customer.isActive) StatusDelivered else StatusCancelled,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (customer.isActive) "Active" else "Blocked",
                            color = if (customer.isActive) StatusDelivered else StatusCancelled,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
