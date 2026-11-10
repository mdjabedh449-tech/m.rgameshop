package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.CustomersScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.LoginScreen
import com.example.ui.screens.OrderDetailScreen
import com.example.ui.screens.OrdersScreen
import com.example.ui.screens.ProductFormScreen
import com.example.ui.screens.ProductsScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceBorder
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.GoldContainer
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.GoldSecondary
import com.example.ui.theme.OnGold
import com.example.ui.theme.StatusDelivered
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite
import com.example.ui.viewmodel.AdminViewModel
import com.example.ui.viewmodel.CurrentScreen
import com.example.ui.viewmodel.NavigationTab
import kotlinx.coroutines.delay

@Composable
fun MainScreen(
    viewModel: AdminViewModel = viewModel()
) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val selectedTab by viewModel.selectedTab.collectAsState()
    val admin by viewModel.currentAdmin.collectAsState()
    val feedbackMessage by viewModel.feedbackMessage.collectAsState()
    val stats by viewModel.dashboardStats.collectAsState()

    // Auto-dismiss feedback message after 3 seconds
    LaunchedEffect(feedbackMessage) {
        if (feedbackMessage != null) {
            delay(3000)
            viewModel.clearFeedback()
        }
    }

    // Intercept back button on detail or form screens
    BackHandler(enabled = currentScreen !is CurrentScreen.Dashboard && currentScreen !is CurrentScreen.Login) {
        viewModel.navigateBack()
    }

    if (admin == null || currentScreen is CurrentScreen.Login) {
        LoginScreen(viewModel = viewModel)
    } else {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = DarkBackground,
            bottomBar = {
                // Show bottom navigation bar when on tab screens
                if (currentScreen is CurrentScreen.Dashboard ||
                    currentScreen is CurrentScreen.Products ||
                    currentScreen is CurrentScreen.Orders ||
                    currentScreen is CurrentScreen.Customers ||
                    currentScreen is CurrentScreen.Settings
                ) {
                    NavigationBar(
                        modifier = Modifier.navigationBarsPadding(),
                        containerColor = DarkSurface,
                        tonalElevation = 8.dp
                    ) {
                        // 1. Dashboard Tab
                        NavigationBarItem(
                            selected = selectedTab == NavigationTab.DASHBOARD,
                            onClick = { viewModel.navigateToTab(NavigationTab.DASHBOARD) },
                            icon = {
                                Icon(
                                    imageVector = Icons.Default.Dashboard,
                                    contentDescription = "Dashboard"
                                )
                            },
                            label = {
                                Text(
                                    text = "Dashboard",
                                    fontSize = 10.sp,
                                    fontWeight = if (selectedTab == NavigationTab.DASHBOARD) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = OnGold,
                                selectedTextColor = GoldPrimary,
                                indicatorColor = GoldPrimary,
                                unselectedIconColor = TextMuted,
                                unselectedTextColor = TextMuted
                            )
                        )

                        // 2. Products Tab
                        NavigationBarItem(
                            selected = selectedTab == NavigationTab.PRODUCTS,
                            onClick = { viewModel.navigateToTab(NavigationTab.PRODUCTS) },
                            icon = {
                                Icon(
                                    imageVector = Icons.Default.SportsEsports,
                                    contentDescription = "Products"
                                )
                            },
                            label = {
                                Text(
                                    text = "Products",
                                    fontSize = 10.sp,
                                    fontWeight = if (selectedTab == NavigationTab.PRODUCTS) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = OnGold,
                                selectedTextColor = GoldPrimary,
                                indicatorColor = GoldPrimary,
                                unselectedIconColor = TextMuted,
                                unselectedTextColor = TextMuted
                            )
                        )

                        // 3. Orders Tab
                        NavigationBarItem(
                            selected = selectedTab == NavigationTab.ORDERS,
                            onClick = { viewModel.navigateToTab(NavigationTab.ORDERS) },
                            icon = {
                                if (stats.pendingOrdersCount > 0) {
                                    BadgedBox(
                                        badge = {
                                            Badge(containerColor = GoldPrimary) {
                                                Text(
                                                    text = "${stats.pendingOrdersCount}",
                                                    color = OnGold,
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.ReceiptLong,
                                            contentDescription = "Orders"
                                        )
                                    }
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.ReceiptLong,
                                        contentDescription = "Orders"
                                    )
                                }
                            },
                            label = {
                                Text(
                                    text = "Orders",
                                    fontSize = 10.sp,
                                    fontWeight = if (selectedTab == NavigationTab.ORDERS) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = OnGold,
                                selectedTextColor = GoldPrimary,
                                indicatorColor = GoldPrimary,
                                unselectedIconColor = TextMuted,
                                unselectedTextColor = TextMuted
                            )
                        )

                        // 4. Customers Tab
                        NavigationBarItem(
                            selected = selectedTab == NavigationTab.CUSTOMERS,
                            onClick = { viewModel.navigateToTab(NavigationTab.CUSTOMERS) },
                            icon = {
                                Icon(
                                    imageVector = Icons.Default.People,
                                    contentDescription = "Customers"
                                )
                            },
                            label = {
                                Text(
                                    text = "Customers",
                                    fontSize = 10.sp,
                                    fontWeight = if (selectedTab == NavigationTab.CUSTOMERS) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = OnGold,
                                selectedTextColor = GoldPrimary,
                                indicatorColor = GoldPrimary,
                                unselectedIconColor = TextMuted,
                                unselectedTextColor = TextMuted
                            )
                        )

                        // 5. Settings Tab
                        NavigationBarItem(
                            selected = selectedTab == NavigationTab.SETTINGS,
                            onClick = { viewModel.navigateToTab(NavigationTab.SETTINGS) },
                            icon = {
                                Icon(
                                    imageVector = Icons.Default.Settings,
                                    contentDescription = "Settings"
                                )
                            },
                            label = {
                                Text(
                                    text = "Settings",
                                    fontSize = 10.sp,
                                    fontWeight = if (selectedTab == NavigationTab.SETTINGS) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = OnGold,
                                selectedTextColor = GoldPrimary,
                                indicatorColor = GoldPrimary,
                                unselectedIconColor = TextMuted,
                                unselectedTextColor = TextMuted
                            )
                        )
                    }
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // Active Screen Content
                when (val screen = currentScreen) {
                    is CurrentScreen.Dashboard -> DashboardScreen(viewModel = viewModel)
                    is CurrentScreen.Products -> ProductsScreen(viewModel = viewModel)
                    is CurrentScreen.AddProduct -> ProductFormScreen(viewModel = viewModel, existingProduct = null)
                    is CurrentScreen.EditProduct -> ProductFormScreen(viewModel = viewModel, existingProduct = screen.product)
                    is CurrentScreen.Orders -> OrdersScreen(viewModel = viewModel)
                    is CurrentScreen.OrderDetail -> OrderDetailScreen(orderId = screen.orderId, viewModel = viewModel)
                    is CurrentScreen.Customers -> CustomersScreen(viewModel = viewModel)
                    is CurrentScreen.CustomerDetail -> CustomersScreen(viewModel = viewModel)
                    is CurrentScreen.Settings -> SettingsScreen(viewModel = viewModel)
                    is CurrentScreen.Login -> LoginScreen(viewModel = viewModel)
                }

                // Floating Banner for feedback messages
                AnimatedVisibility(
                    visible = feedbackMessage != null,
                    enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
                    exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 16.dp, start = 16.dp, end = 16.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = DarkSurfaceElevated,
                        border = BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.6f)),
                        shadowElevation = 8.dp
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = GoldPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = feedbackMessage ?: "",
                                color = TextWhite,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }
    }
}
