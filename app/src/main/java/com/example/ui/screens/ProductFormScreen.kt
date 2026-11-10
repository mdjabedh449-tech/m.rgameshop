package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.model.GamingPlatform
import com.example.model.Product
import com.example.model.ProductCategory
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
import com.example.ui.theme.TextLight
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextWhite
import com.example.ui.viewmodel.AdminViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductFormScreen(
    viewModel: AdminViewModel,
    existingProduct: Product? = null,
    modifier: Modifier = Modifier
) {
    val isEditMode = existingProduct != null
    val context = LocalContext.current

    var name by remember { mutableStateOf(existingProduct?.name ?: "") }
    var sku by remember { mutableStateOf(existingProduct?.sku ?: "MR-SKU-${(1000..9999).random()}") }
    var category by remember { mutableStateOf(existingProduct?.category ?: ProductCategory.CONSOLES) }
    var platform by remember { mutableStateOf(existingProduct?.platform ?: GamingPlatform.PS5) }
    var priceText by remember { mutableStateOf(existingProduct?.price?.toString() ?: "") }
    var salePriceText by remember { mutableStateOf(existingProduct?.salePrice?.toString() ?: "") }
    var stockQuantityText by remember { mutableStateOf(existingProduct?.stockQuantity?.toString() ?: "10") }
    var lowStockThresholdText by remember { mutableStateOf(existingProduct?.lowStockThreshold?.toString() ?: "5") }
    var description by remember { mutableStateOf(existingProduct?.description ?: "") }
    var imageUrl by remember { mutableStateOf(existingProduct?.imageUrl ?: "") }
    var isFeatured by remember { mutableStateOf(existingProduct?.isFeatured ?: false) }

    var categoryDropdownExpanded by remember { mutableStateOf(false) }
    var platformDropdownExpanded by remember { mutableStateOf(false) }

    // Curated High-Resolution Gaming Gear Presets
    val gamingPresets = listOf(
        "PS5 Pro" to "https://images.unsplash.com/photo-1606813907291-d86efa9b94db?auto=format&fit=crop&w=600&q=80",
        "Apex PC" to "https://images.unsplash.com/photo-1587202372775-e229f172b9d7?auto=format&fit=crop&w=600&q=80",
        "Gold Controller" to "https://images.unsplash.com/photo-1592840496694-26d035b52b48?auto=format&fit=crop&w=600&q=80",
        "Xbox Series" to "https://images.unsplash.com/photo-1621259182978-fbf93132d53d?auto=format&fit=crop&w=600&q=80",
        "Mechanical KB" to "https://images.unsplash.com/photo-1587829741301-dc798b83add3?auto=format&fit=crop&w=600&q=80",
        "Gaming Headset" to "https://images.unsplash.com/photo-1546435770-a3e426bf472b?auto=format&fit=crop&w=600&q=80",
        "Gaming Chair" to "https://images.unsplash.com/photo-1598550476439-6847785fcea6?auto=format&fit=crop&w=600&q=80"
    )

    // Android Zero-Permission Photo Picker launcher
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            imageUrl = uri.toString()
            viewModel.showFeedback("Custom image selected from device")
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = DarkBackground,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isEditMode) "EDIT PRODUCT" else "ADD NEW PRODUCT",
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
            // Image Upload & Selection Card
            GamingCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = DarkSurfaceElevated,
                borderStroke = BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.3f))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "PRODUCT IMAGE UPLOAD",
                        color = GoldSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Image Preview Box
                        Box(
                            modifier = Modifier
                                .size(90.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .border(1.dp, DarkSurfaceBorder, RoundedCornerShape(12.dp))
                                .background(DarkSurface),
                            contentAlignment = Alignment.Center
                        ) {
                            if (imageUrl.isNotBlank()) {
                                AsyncImage(
                                    model = ImageRequest.Builder(context)
                                        .data(imageUrl)
                                        .crossfade(true)
                                        .build(),
                                    contentDescription = "Selected product image",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Image,
                                    contentDescription = null,
                                    tint = TextMuted,
                                    modifier = Modifier.size(36.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            GoldOutlinedButton(
                                text = "Choose Device Photo",
                                onClick = {
                                    photoPickerLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                },
                                leadingIcon = Icons.Default.AddPhotoAlternate,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Supports PNG, JPEG, WEBP or select a gaming preset below",
                                color = TextMuted,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Image URL manual entry
                    OutlinedTextField(
                        value = imageUrl,
                        onValueChange = { imageUrl = it },
                        label = { Text("Or Paste Image URL", color = TextSecondary) },
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

                    Text(
                        text = "Quick Gaming Presets:",
                        color = TextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(gamingPresets) { (label, url) ->
                            val isSelected = imageUrl == url
                            Surface(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { imageUrl = url },
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSelected) GoldContainer else DarkSurface,
                                border = BorderStroke(1.dp, if (isSelected) GoldPrimary else DarkSurfaceBorder)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    if (isSelected) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            tint = GoldPrimary,
                                            modifier = Modifier.size(12.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                    }
                                    Text(
                                        text = label,
                                        color = if (isSelected) GoldPrimary else TextLight,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Basic Information Card
            GamingCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = DarkSurfaceElevated
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "PRODUCT INFORMATION",
                        color = GoldSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    // Title
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Product Title", color = TextSecondary) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("product_title_input"),
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

                    // SKU
                    OutlinedTextField(
                        value = sku,
                        onValueChange = { sku = it },
                        label = { Text("SKU / Barcode Code", color = TextSecondary) },
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

                    Spacer(modifier = Modifier.height(12.dp))

                    // Category Selector
                    ExposedDropdownMenuBox(
                        expanded = categoryDropdownExpanded,
                        onExpandedChange = { categoryDropdownExpanded = !categoryDropdownExpanded }
                    ) {
                        OutlinedTextField(
                            value = category.displayName,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Category", color = TextSecondary) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryDropdownExpanded) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
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
                        ExposedDropdownMenu(
                            expanded = categoryDropdownExpanded,
                            onDismissRequest = { categoryDropdownExpanded = false }
                        ) {
                            ProductCategory.values().filter { it != ProductCategory.ALL }.forEach { cat ->
                                DropdownMenuItem(
                                    text = { Text(cat.displayName) },
                                    onClick = {
                                        category = cat
                                        categoryDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Platform Selector
                    ExposedDropdownMenuBox(
                        expanded = platformDropdownExpanded,
                        onExpandedChange = { platformDropdownExpanded = !platformDropdownExpanded }
                    ) {
                        OutlinedTextField(
                            value = platform.tag,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Gaming Platform", color = TextSecondary) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = platformDropdownExpanded) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
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
                        ExposedDropdownMenu(
                            expanded = platformDropdownExpanded,
                            onDismissRequest = { platformDropdownExpanded = false }
                        ) {
                            GamingPlatform.values().forEach { plt ->
                                DropdownMenuItem(
                                    text = { Text(plt.tag) },
                                    onClick = {
                                        platform = plt
                                        platformDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // Pricing & Stock Card
            GamingCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = DarkSurfaceElevated
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "PRICING & INVENTORY",
                        color = GoldSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = priceText,
                            onValueChange = { priceText = it },
                            label = { Text("Price ($)", color = TextSecondary) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("product_price_input"),
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
                            value = salePriceText,
                            onValueChange = { salePriceText = it },
                            label = { Text("Sale Price ($)", color = TextSecondary) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
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

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = stockQuantityText,
                            onValueChange = { stockQuantityText = it },
                            label = { Text("Stock Quantity", color = TextSecondary) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("product_stock_input"),
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
                            value = lowStockThresholdText,
                            onValueChange = { lowStockThresholdText = it },
                            label = { Text("Low Alert Qty", color = TextSecondary) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
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

                    Spacer(modifier = Modifier.height(12.dp))

                    // Featured switch
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "Feature on Storefront",
                                color = TextWhite,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "Show on hero carousel and top deals",
                                color = TextMuted,
                                fontSize = 11.sp
                            )
                        }
                        Switch(
                            checked = isFeatured,
                            onCheckedChange = { isFeatured = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = OnGold,
                                checkedTrackColor = GoldPrimary,
                                uncheckedTrackColor = DarkSurface
                            )
                        )
                    }
                }
            }

            // Description
            GamingCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = DarkSurfaceElevated
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "DESCRIPTION & SPECS",
                        color = GoldSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Product Description / Features", color = TextSecondary) },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3,
                        maxLines = 6,
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

            // Save CTA Button
            GoldGradientButton(
                text = if (isEditMode) "SAVE CHANGES" else "PUBLISH PRODUCT",
                leadingIcon = Icons.Default.Save,
                onClick = {
                    val priceVal = priceText.toDoubleOrNull() ?: 0.0
                    val salePriceVal = salePriceText.toDoubleOrNull()
                    val stockVal = stockQuantityText.toIntOrNull() ?: 0
                    val alertVal = lowStockThresholdText.toIntOrNull() ?: 5

                    viewModel.saveProduct(
                        id = existingProduct?.id,
                        name = name,
                        sku = sku,
                        category = category,
                        platform = platform,
                        price = priceVal,
                        salePrice = salePriceVal,
                        stockQuantity = stockVal,
                        lowStockThreshold = alertVal,
                        description = description,
                        specifications = existingProduct?.specifications ?: mapOf(
                            "Platform" to platform.tag,
                            "Warranty" to "1 Year M.R Zone Care"
                        ),
                        imageUrl = imageUrl,
                        isFeatured = isFeatured
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                testTag = "save_product_button"
            )

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}
