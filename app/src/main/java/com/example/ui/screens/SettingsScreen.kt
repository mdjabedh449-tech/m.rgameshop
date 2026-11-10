package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.firebase.FirebaseConfig
import com.example.ui.components.GamingCard
import com.example.ui.components.GoldGradientButton
import com.example.ui.components.GoldOutlinedButton
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
import com.example.ui.theme.TextLight
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextWhite
import com.example.ui.viewmodel.AdminViewModel

@Composable
fun SettingsScreen(
    viewModel: AdminViewModel,
    modifier: Modifier = Modifier
) {
    val admin by viewModel.currentAdmin.collectAsState()
    val isSyncing by viewModel.isCloudSyncing.collectAsState()

    var storeName by remember { mutableStateOf(admin?.storeName ?: "M.R GAME ZONE") }
    var selectedCurrency by remember { mutableStateOf("USD ($)") }
    var taxRate by remember { mutableStateOf("5.0%") }

    var orderNotifications by remember { mutableStateOf(true) }
    var stockAlertNotifications by remember { mutableStateOf(true) }
    var dailySummaryNotification by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        // Title
        Text(
            text = "SETTINGS & INTEGRATIONS",
            color = GoldPrimary,
            fontSize = 18.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 1.sp
        )

        // Admin Profile Card
        GamingCard(
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = DarkSurfaceElevated,
            borderStroke = BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.4f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .border(1.5.dp, GoldPrimary, CircleShape)
                        .background(DarkSurface),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_mr_game_icon),
                        contentDescription = "Admin Avatar",
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = admin?.displayName ?: "M.R Admin",
                        color = TextWhite,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = admin?.role ?: "Super Administrator",
                        color = GoldSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = admin?.email ?: "admin@mrgamezone.com",
                        color = TextMuted,
                        fontSize = 11.sp
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = GoldContainer,
                    border = BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.4f))
                ) {
                    Text(
                        text = "MASTER",
                        color = GoldPrimary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }

        // Firebase Cloud Firestore & Auth Integration Card
        GamingCard(
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = DarkSurfaceElevated,
            borderStroke = BorderStroke(1.dp, GoldAccent.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CloudSync,
                            contentDescription = null,
                            tint = GoldPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "FIREBASE & CLOUD FIRESTORE",
                            color = GoldPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = StatusDelivered.copy(alpha = 0.15f),
                        border = BorderStroke(1.dp, StatusDelivered.copy(alpha = 0.4f))
                    ) {
                        Text(
                            text = "Ready to Sync",
                            color = StatusDelivered,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Prepared for your existing Firebase project. All schemas, zero-trust collection layouts (products, orders, customers, store_metrics), and Firebase Authentication models are wired.",
                    color = TextLight,
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Schema Collections Pills
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf("products", "orders", "customers", "metrics").forEach { coll ->
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = DarkSurface,
                            border = BorderStroke(1.dp, DarkSurfaceBorder)
                        ) {
                            Text(
                                text = "📁 $coll",
                                color = GoldSecondary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Sync Now CTA
                GoldGradientButton(
                    text = if (isSyncing) "SYNCHRONIZING..." else "SYNC WITH CLOUD FIRESTORE",
                    onClick = { viewModel.syncWithFirestore() },
                    isLoading = isSyncing,
                    leadingIcon = Icons.Default.CloudDone,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // Store Configuration Card
        GamingCard(
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = DarkSurfaceElevated
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Store, contentDescription = null, tint = GoldSecondary, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "STORE CONFIGURATION",
                        color = GoldSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = storeName,
                    onValueChange = { storeName = it },
                    label = { Text("Storefront Name", color = TextSecondary) },
                    modifier = Modifier.fillMaxWidth(),
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

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = selectedCurrency,
                        onValueChange = { selectedCurrency = it },
                        label = { Text("Currency", color = TextSecondary) },
                        modifier = Modifier.weight(1f),
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
                    OutlinedTextField(
                        value = taxRate,
                        onValueChange = { taxRate = it },
                        label = { Text("Default Tax", color = TextSecondary) },
                        modifier = Modifier.weight(1f),
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
                }
            }
        }

        // Notification Preferences
        GamingCard(
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = DarkSurfaceElevated
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Notifications, contentDescription = null, tint = GoldSecondary, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "ADMIN PUSH ALERTS",
                        color = GoldSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = "New Order Notifications", color = TextWhite, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        Text(text = "Sound & push notification for incoming orders", color = TextMuted, fontSize = 11.sp)
                    }
                    Switch(
                        checked = orderNotifications,
                        onCheckedChange = { orderNotifications = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = OnGold,
                            checkedTrackColor = GoldPrimary,
                            uncheckedTrackColor = DarkSurface
                        )
                    )
                }

                HorizontalDivider(color = DarkSurfaceBorder, modifier = Modifier.padding(vertical = 10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = "Low Stock Alerts", color = TextWhite, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        Text(text = "Instant alert when inventory drops below threshold", color = TextMuted, fontSize = 11.sp)
                    }
                    Switch(
                        checked = stockAlertNotifications,
                        onCheckedChange = { stockAlertNotifications = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = OnGold,
                            checkedTrackColor = GoldPrimary,
                            uncheckedTrackColor = DarkSurface
                        )
                    )
                }
            }
        }

        // Catalog Maintenance & Reset
        GamingCard(
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = DarkSurfaceElevated
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Refresh, contentDescription = null, tint = GoldSecondary, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "DATA MAINTENANCE",
                        color = GoldSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Need to test with pristine fresh gaming data? Re-seed sample gaming consoles, orders, and customer accounts anytime.",
                    color = TextSecondary,
                    fontSize = 12.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                GoldOutlinedButton(
                    text = "Restore Default Gaming Catalog",
                    onClick = { viewModel.resetCatalogData() },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // Logout Button
        Button(
            onClick = { viewModel.logout() },
            colors = ButtonDefaults.buttonColors(containerColor = StatusCancelled.copy(alpha = 0.15f)),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, StatusCancelled.copy(alpha = 0.4f)),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Icon(imageVector = Icons.Default.Logout, contentDescription = null, tint = StatusCancelled, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "LOGOUT OF ADMIN PORTAL", color = StatusCancelled, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        }

        Spacer(modifier = Modifier.height(100.dp))
    }
}
