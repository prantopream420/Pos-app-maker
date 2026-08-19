package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Laptop
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.PointOfSale
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Branch
import com.example.data.model.InventoryUnit
import com.example.ui.components.ConditionBadge
import com.example.ui.theme.AmberTertiaryLight
import com.example.ui.theme.BentoAmberCard
import com.example.ui.theme.BentoBlueCard
import com.example.ui.theme.BentoBorder
import com.example.ui.theme.BentoEmeraldCard
import com.example.ui.theme.BentoEmeraldCardText
import com.example.ui.theme.BentoIndigo
import com.example.ui.theme.BentoNavy
import com.example.ui.theme.BentoPillBg
import com.example.ui.theme.BentoPillText
import com.example.ui.theme.BentoTextPrimary
import com.example.ui.theme.BentoTextSecondary
import com.example.ui.theme.EmeraldSecondaryLight
import com.example.ui.theme.IndigoPrimaryLight
import com.example.ui.viewmodel.SortOption
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventoryScreen(
    inventoryUnits: List<InventoryUnit>,
    branches: List<Branch>,
    selectedBranchFilter: String?,
    selectedCategoryFilter: String,
    selectedConditionFilter: String,
    searchQuery: String,
    sortOption: SortOption,
    currencySymbol: String,
    onBranchFilterChange: (String?) -> Unit,
    onCategoryFilterChange: (String) -> Unit,
    onConditionFilterChange: (String) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onSortOptionChange: (SortOption) -> Unit,
    onAddUnitClick: () -> Unit,
    onAddVariantClick: (modelName: String, brand: String, category: String) -> Unit,
    onSellUnitClick: (InventoryUnit) -> Unit,
    onDeleteUnitClick: (String) -> Unit
) {
    val context = LocalContext.current
    var showCostConfidentiality by remember { mutableStateOf(false) }
    var sortMenuExpanded by remember { mutableStateOf(false) }

    val categories = listOf("All", "Laptops", "Smartphones", "Accessories", "Audio", "Tablets", "Custom")
    val conditions = listOf("All", "Brand New", "Used", "Open Box", "Special Unit")

    val numFormat = NumberFormat.getNumberInstance(Locale.US).apply {
        maximumFractionDigits = 2
        minimumFractionDigits = 0
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 14.dp, bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header & Branch Tabs
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Inventory Stock",
                                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                                color = BentoNavy
                            )
                            Text(
                                text = "${inventoryUnits.size} available units in stock",
                                style = MaterialTheme.typography.bodySmall,
                                color = BentoTextSecondary
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // Confidential cost view toggle
                            IconButton(
                                onClick = { showCostConfidentiality = !showCostConfidentiality },
                                modifier = Modifier.testTag("inventory_toggle_cost_button")
                            ) {
                                Icon(
                                    imageVector = if (showCostConfidentiality) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = "Toggle Cost Visibility",
                                    tint = BentoNavy
                                )
                            }

                            // Sort dropdown button
                            Box {
                                IconButton(onClick = { sortMenuExpanded = true }) {
                                    Icon(Icons.Default.Sort, contentDescription = "Sort Stock", tint = BentoNavy)
                                }

                                DropdownMenu(
                                    expanded = sortMenuExpanded,
                                    onDismissRequest = { sortMenuExpanded = false }
                                ) {
                                    DropdownMenuItem(
                                        text = { Text("Newest Added") },
                                        onClick = { onSortOptionChange(SortOption.LATEST); sortMenuExpanded = false }
                                    )
                                    DropdownMenuItem(
                                        text = { Text("Price: Low to High") },
                                        onClick = { onSortOptionChange(SortOption.PRICE_LOW_HIGH); sortMenuExpanded = false }
                                    )
                                    DropdownMenuItem(
                                        text = { Text("Price: High to Low") },
                                        onClick = { onSortOptionChange(SortOption.PRICE_HIGH_LOW); sortMenuExpanded = false }
                                    )
                                    DropdownMenuItem(
                                        text = { Text("Brand / Model (A-Z)") },
                                        onClick = { onSortOptionChange(SortOption.ALPHABETICAL_AZ); sortMenuExpanded = false }
                                    )
                                }
                            }
                        }
                    }

                    // Branch filter tab row
                    if (branches.size > 1) {
                        ScrollableTabRow(
                            selectedTabIndex = if (selectedBranchFilter == null) 0 else (branches.indexOfFirst { it.id == selectedBranchFilter } + 1).coerceAtLeast(0),
                            edgePadding = 0.dp,
                            containerColor = Color.Transparent,
                            divider = {}
                        ) {
                            Tab(
                                selected = selectedBranchFilter == null,
                                onClick = { onBranchFilterChange(null) },
                                text = { Text("All Branches", fontWeight = if (selectedBranchFilter == null) FontWeight.Bold else FontWeight.Normal, color = BentoNavy) }
                            )
                            branches.forEach { branch ->
                                Tab(
                                    selected = selectedBranchFilter == branch.id,
                                    onClick = { onBranchFilterChange(branch.id) },
                                    text = { Text(branch.name, fontWeight = if (selectedBranchFilter == branch.id) FontWeight.Bold else FontWeight.Normal, color = BentoNavy) }
                                )
                            }
                        }
                    }
                }
            }

            // Search Bar
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChange,
                    placeholder = { Text("Search model, brand, serial number, CPU...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = BentoNavy) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { onSearchQueryChange("") }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear", tint = BentoNavy)
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFFF8FAFC),
                        unfocusedContainerColor = Color(0xFFF8FAFC),
                        focusedBorderColor = BentoIndigo,
                        unfocusedBorderColor = BentoBorder,
                        focusedTextColor = BentoNavy,
                        unfocusedTextColor = BentoNavy
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("inventory_search_input")
                )
            }

            // Category & Condition Filter Chips
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(categories) { cat ->
                            FilterChip(
                                selected = selectedCategoryFilter == cat,
                                onClick = { onCategoryFilterChange(cat) },
                                label = { Text(cat, fontSize = 12.sp, fontWeight = if (selectedCategoryFilter == cat) FontWeight.Bold else FontWeight.Normal) },
                                shape = RoundedCornerShape(10.dp),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = BentoNavy,
                                    selectedLabelColor = Color.White,
                                    containerColor = Color(0xFFF1F5F9),
                                    labelColor = BentoNavy
                                )
                            )
                        }
                    }

                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(conditions) { cond ->
                            FilterChip(
                                selected = selectedConditionFilter == cond,
                                onClick = { onConditionFilterChange(cond) },
                                label = { Text(cond, fontSize = 12.sp, fontWeight = if (selectedConditionFilter == cond) FontWeight.Bold else FontWeight.Normal) },
                                shape = RoundedCornerShape(10.dp),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = BentoNavy,
                                    selectedLabelColor = Color.White,
                                    containerColor = Color(0xFFF1F5F9),
                                    labelColor = BentoNavy
                                )
                            )
                        }
                    }
                }
            }

            // Inventory List Items
            if (inventoryUnits.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, BentoBorder)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(28.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Laptop,
                                contentDescription = null,
                                tint = BentoIndigo,
                                modifier = Modifier.size(48.dp)
                            )
                            Text(
                                text = "No inventory matches your search",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleMedium,
                                color = BentoNavy
                            )
                            Text(
                                text = "Try adjusting your filters or tap + Add Stock below.",
                                fontSize = 13.sp,
                                color = BentoTextSecondary
                            )
                            Button(
                                onClick = onAddUnitClick,
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = BentoNavy)
                            ) {
                                Text("Add New Product", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            } else {
                items(inventoryUnits, key = { it.id }) { unit ->
                    val branchName = branches.find { it.id == unit.branchId }?.name ?: "Branch"
                    val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, BentoBorder),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("inventory_item_${unit.serialNumber}")
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Top Bar: Model Name, Brand, Condition Badge, and Quick "+" Variant Button
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Column {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Text(
                                                text = "${unit.brand} ${unit.modelName}",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 16.sp,
                                                color = BentoNavy,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            // Plus icon near model name to quick-add variant (requested by user)
                                            IconButton(
                                                onClick = {
                                                    onAddVariantClick(unit.modelName, unit.brand, unit.category)
                                                },
                                                modifier = Modifier
                                                    .size(26.dp)
                                                    .background(BentoBlueCard, CircleShape)
                                                    .testTag("add_variant_${unit.serialNumber}")
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Add,
                                                    contentDescription = "Add Variant",
                                                    tint = BentoNavy,
                                                    modifier = Modifier.size(15.dp)
                                                )
                                            }
                                        }

                                        Text(
                                            text = "$branchName • ${unit.category}",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = BentoTextSecondary
                                        )
                                    }
                                }

                                ConditionBadge(condition = unit.condition)
                            }

                            // Serial Number Tag & Copy
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color(0xFFF8FAFC),
                                border = BorderStroke(1.dp, BentoBorder),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.QrCode,
                                            contentDescription = null,
                                            tint = BentoIndigo,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "S/N: ${unit.serialNumber}",
                                            fontFamily = FontFamily.Monospace,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            color = BentoNavy
                                        )
                                    }

                                    IconButton(
                                        onClick = {
                                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                            val clip = ClipData.newPlainText("Serial Number", unit.serialNumber)
                                            clipboard.setPrimaryClip(clip)
                                            Toast.makeText(context, "Copied ${unit.serialNumber}", Toast.LENGTH_SHORT).show()
                                        },
                                        modifier = Modifier.size(26.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.ContentCopy,
                                            contentDescription = "Copy Serial",
                                            tint = BentoIndigo,
                                            modifier = Modifier.size(15.dp)
                                        )
                                    }
                                }
                            }

                            // Device Specs Pills (CPU, GPU, RAM, ROM, Color)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                if (unit.cpu.isNotBlank() && unit.cpu != "N/A") {
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = Color(0xFFF8FAFC),
                                        border = BorderStroke(1.dp, BentoBorder),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Column(modifier = Modifier.padding(8.dp)) {
                                            Text("CPU", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = BentoTextSecondary)
                                            Text(unit.cpu, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = BentoNavy, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                        }
                                    }
                                }

                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color(0xFFF8FAFC),
                                    border = BorderStroke(1.dp, BentoBorder),
                                    modifier = Modifier.weight(0.7f)
                                ) {
                                    Column(modifier = Modifier.padding(8.dp)) {
                                        Text("RAM", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = BentoTextSecondary)
                                        Text(unit.ram, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = BentoNavy, maxLines = 1)
                                    }
                                }

                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color(0xFFF8FAFC),
                                    border = BorderStroke(1.dp, BentoBorder),
                                    modifier = Modifier.weight(0.9f)
                                ) {
                                    Column(modifier = Modifier.padding(8.dp)) {
                                        Text("STORAGE", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = BentoTextSecondary)
                                        Text(unit.storage, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = BentoNavy, maxLines = 1)
                                    }
                                }
                            }

                            // Price & Source Information
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    if (showCostConfidentiality) {
                                        Text(
                                            text = "Cost: $currencySymbol ${numFormat.format(unit.purchasePrice)}",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = BentoIndigo
                                        )
                                    }
                                    Text(
                                        text = "Selling: $currencySymbol ${numFormat.format(unit.suggestedSellingPrice)}",
                                        fontWeight = FontWeight.Black,
                                        fontSize = 15.sp,
                                        color = BentoNavy
                                    )
                                    Text(
                                        text = "Source: ${unit.purchaseSource} • ${dateFormat.format(Date(unit.purchaseDate))}",
                                        fontSize = 11.sp,
                                        color = BentoTextSecondary
                                    )
                                }

                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    IconButton(
                                        onClick = { onDeleteUnitClick(unit.id) },
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Delete",
                                            tint = Color(0xFFEF4444).copy(alpha = 0.7f),
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }

                                    Button(
                                        onClick = { onSellUnitClick(unit) },
                                        shape = RoundedCornerShape(12.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = BentoNavy),
                                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                                        modifier = Modifier.testTag("sell_unit_button_${unit.serialNumber}")
                                    ) {
                                        Icon(Icons.Default.PointOfSale, contentDescription = null, modifier = Modifier.size(15.dp), tint = Color.White)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Bill / Sell", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Floating Action Button to Add New Product / Unit
        FloatingActionButton(
            onClick = onAddUnitClick,
            containerColor = BentoNavy,
            contentColor = Color.White,
            shape = CircleShape,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 20.dp, bottom = 96.dp)
                .testTag("inventory_fab_add_product")
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Product", tint = Color.White)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Add Stock", fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    }
}
