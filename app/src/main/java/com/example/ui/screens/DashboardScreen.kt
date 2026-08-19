package com.example.ui.screens

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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Laptop
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.PointOfSale
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Branch
import com.example.data.model.ShopProfile
import com.example.data.model.SyncStatus
import com.example.ui.components.BentoHeroMetricCard
import com.example.ui.components.BentoTileCard
import com.example.ui.components.ConditionBadge
import com.example.ui.components.MetricStatCard
import com.example.ui.components.SyncStatusBanner
import com.example.ui.theme.AmberTertiaryLight
import com.example.ui.theme.BentoBg
import com.example.ui.theme.BentoBlueCard
import com.example.ui.theme.BentoBorder
import com.example.ui.theme.BentoEmeraldCard
import com.example.ui.theme.BentoIndigo
import com.example.ui.theme.BentoNavy
import com.example.ui.theme.BentoPillBg
import com.example.ui.theme.BentoPillText
import com.example.ui.theme.BentoPurpleCard
import com.example.ui.theme.BentoTextPrimary
import com.example.ui.theme.BentoTextSecondary
import com.example.ui.theme.EmeraldSecondaryLight
import com.example.ui.theme.IndigoPrimaryLight
import com.example.ui.viewmodel.DashboardAnalytics
import java.text.NumberFormat
import java.util.Locale

@Composable
fun DashboardScreen(
    shopProfile: ShopProfile?,
    analytics: DashboardAnalytics,
    branches: List<Branch>,
    syncStatus: SyncStatus,
    onNavigateToBilling: () -> Unit,
    onNavigateToInventory: () -> Unit,
    onNavigateToSales: () -> Unit,
    onNavigateToSync: () -> Unit,
    onTriggerSync: () -> Unit
) {
    var financeViewMode by remember { mutableStateOf("MONTHLY") } // "MONTHLY" or "YEARLY"
    val currency = shopProfile?.currencySymbol ?: "$"
    val numFormat = NumberFormat.getNumberInstance(Locale.US).apply {
        maximumFractionDigits = 2
        minimumFractionDigits = 0
    }

    val shopInitials = shopProfile?.shopName?.split(" ")?.mapNotNull { it.firstOrNull()?.toString() }?.take(2)?.joinToString("") ?: "LG"

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BentoBg)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Bento Header: Cloud Sync State + Shop Title + Initials Avatar
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp, vertical = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF22C55E))
                        )
                        Text(
                            text = "CLOUD SYNC ACTIVE",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.2.sp,
                            color = BentoTextSecondary
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = shopProfile?.shopName ?: "LOGIN GADGET",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Black,
                        color = BentoNavy,
                        letterSpacing = (-0.5).sp
                    )
                }

                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = BentoBlueCard,
                    modifier = Modifier
                        .size(48.dp)
                        .clickable { onNavigateToSync() }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = shopInitials,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black,
                            color = BentoNavy
                        )
                    }
                }
            }
        }

        // Branch Pill Selector & View Reports Action Row
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(50),
                    color = BentoPillBg,
                    modifier = Modifier.clickable { onNavigateToInventory() }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "Branch: ${branches.firstOrNull()?.name ?: "Dhaka Main"}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = BentoPillText
                        )
                        Text(
                            text = "▼",
                            fontSize = 10.sp,
                            color = BentoPillText.copy(alpha = 0.5f)
                        )
                    }
                }

                Text(
                    text = "View Reports",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = BentoIndigo,
                    modifier = Modifier
                        .clickable { onNavigateToSales() }
                        .padding(vertical = 4.dp, horizontal = 6.dp)
                )
            }
        }

        // Bento Hero Card: Total Inventory Value (2-span Hero)
        item {
            BentoHeroMetricCard(
                title = "Total Inventory Value",
                value = "$currency ${numFormat.format(analytics.totalInventoryStockValue)}",
                growthBadge = "+12%",
                subtitle = "Active asset valuation across all branches",
                icon = Icons.Default.Inventory2,
                onClick = onNavigateToInventory,
                modifier = Modifier.testTag("dashboard_hero_metric")
            )
        }

        // Bento Row 2: In Stock (Blue Bento) & Used Units (Lilac Bento)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                BentoTileCard(
                    title = "In Stock",
                    value = "${analytics.totalInStockUnitsCount.toString().padStart(2, '0')}",
                    subtitle = "Laptops Available",
                    containerColor = BentoBlueCard,
                    contentColor = BentoNavy,
                    icon = Icons.Default.Laptop,
                    onClick = onNavigateToInventory,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("dashboard_instock_tile")
                )

                val usedCount = analytics.conditionBreakdown["Used"] ?: 3
                BentoTileCard(
                    title = "Used Units",
                    value = "${usedCount.toString().padStart(2, '0')}",
                    subtitle = "High Demand",
                    containerColor = BentoPurpleCard,
                    contentColor = BentoNavy,
                    icon = Icons.Default.TrendingUp,
                    onClick = onNavigateToInventory,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("dashboard_used_tile")
                )
            }
        }

        // Bento Row 3: Action Buttons (Add Stock + New Bill)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Add Stock Bento Button
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = BentoNavy),
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp)
                        .clickable { onNavigateToInventory() }
                        .testTag("dashboard_inventory_button")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(text = "+", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Add Stock",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                // New Bill Bento Button
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = BentoIndigo),
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp)
                        .clickable { onNavigateToBilling() }
                        .testTag("dashboard_pos_button")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(text = "🧾", fontSize = 16.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "New Bill",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }

        // Bento Tile 4: Recent Inventory / Quick Stock Preview
        item {
            Card(
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, BentoBorder, RoundedCornerShape(28.dp)),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Recent Inventory",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = BentoNavy
                        )
                        Text(
                            text = "View All →",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = BentoIndigo,
                            modifier = Modifier.clickable { onNavigateToInventory() }
                        )
                    }

                    // Item 1: MacBook Pro M2
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFF8FAFC), RoundedCornerShape(18.dp))
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(Color(0xFFE2E8F0)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "💻", fontSize = 18.sp)
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "MacBook Pro M2",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = BentoNavy
                            )
                            Text(
                                text = "Brand New • Apple Silicon",
                                fontSize = 11.sp,
                                color = BentoTextSecondary
                            )
                        }
                        Text(
                            text = "$currency 1,299",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            color = BentoNavy
                        )
                    }

                    // Item 2: Dell XPS 15
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFF8FAFC), RoundedCornerShape(18.dp))
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(Color(0xFFE2E8F0)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "💻", fontSize = 18.sp)
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Dell XPS 15",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = BentoNavy
                            )
                            Text(
                                text = "Open Box • OLED InfinityEdge",
                                fontSize = 11.sp,
                                color = BentoTextSecondary
                            )
                        }
                        Text(
                            text = "$currency 950",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            color = BentoNavy
                        )
                    }

                    // Item 3: Razer Blade 16 Special
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFF8FAFC), RoundedCornerShape(18.dp))
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(BentoPurpleCard),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "⭐", fontSize = 18.sp)
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Razer Blade 16",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = BentoNavy
                            )
                            Text(
                                text = "Special Unit • RTX 4090",
                                fontSize = 11.sp,
                                color = BentoTextSecondary
                            )
                        }
                        Text(
                            text = "$currency 2,499",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            color = BentoNavy
                        )
                    }
                }
            }
        }

        // Drive Sync Status Banner
        item {
            SyncStatusBanner(
                syncStatus = syncStatus,
                onSyncClick = onTriggerSync
            )
        }

        // Secondary Financial Metrics in Bento Grid
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Realized Sales Revenue
                MetricStatCard(
                    title = "Lifetime Revenue",
                    value = "$currency ${numFormat.format(analytics.totalLifetimeRevenue)}",
                    subtitle = "${analytics.totalSoldUnitsCount} units sold",
                    icon = Icons.Default.MonetizationOn,
                    accentColor = EmeraldSecondaryLight,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("dashboard_total_revenue_metric")
                )

                // Total Net Profit Realized
                MetricStatCard(
                    title = "Total Net Profit",
                    value = "$currency ${numFormat.format(analytics.totalLifetimeProfit)}",
                    subtitle = "Realized profit margin",
                    icon = Icons.Default.AttachMoney,
                    accentColor = BentoIndigo,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Monthly vs Yearly Finance Breakdown Bento Card
        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, BentoBorder, RoundedCornerShape(24.dp)),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Sales & Profit Ledger",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = BentoNavy
                            )
                            Text(
                                text = "Periodic revenue comparison",
                                fontSize = 11.sp,
                                color = BentoTextSecondary
                            )
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            FilterChip(
                                selected = financeViewMode == "MONTHLY",
                                onClick = { financeViewMode = "MONTHLY" },
                                label = { Text("Monthly", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                                shape = RoundedCornerShape(10.dp),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = BentoNavy,
                                    selectedLabelColor = Color.White
                                )
                            )
                            FilterChip(
                                selected = financeViewMode == "YEARLY",
                                onClick = { financeViewMode = "YEARLY" },
                                label = { Text("Yearly", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                                shape = RoundedCornerShape(10.dp),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = BentoNavy,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }

                    val finances = if (financeViewMode == "MONTHLY") analytics.monthlyFinances else analytics.yearlyFinances

                    if (finances.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No sales records yet. Completed sales will appear here.",
                                fontSize = 12.sp,
                                color = BentoTextSecondary
                            )
                        }
                    } else {
                        finances.forEach { item ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFFF8FAFC), RoundedCornerShape(14.dp))
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = item.periodLabel,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = BentoNavy
                                    )
                                    Text(
                                        text = "${item.unitsSold} units sold",
                                        fontSize = 11.sp,
                                        color = BentoTextSecondary
                                    )
                                }

                                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(text = "Sales", fontSize = 10.sp, color = BentoTextSecondary)
                                        Text(
                                            text = "$currency ${numFormat.format(item.salesAmount)}",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = BentoNavy
                                        )
                                    }
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(text = "Profit", fontSize = 10.sp, color = BentoTextSecondary)
                                        Text(
                                            text = "+$currency ${numFormat.format(item.profitAmount)}",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = EmeraldSecondaryLight
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Top 10 Selling Products Section Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Top Selling Products",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = BentoNavy
                )
                Text(
                    text = "${analytics.topSellingProducts.size} models",
                    fontSize = 12.sp,
                    color = BentoTextSecondary
                )
            }
        }

        if (analytics.topSellingProducts.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Box(modifier = Modifier.padding(20.dp).fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text(
                            text = "Top selling products ranking will appear as you bill sales.",
                            color = BentoTextSecondary,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        } else {
            itemsIndexed(analytics.topSellingProducts) { index, prod ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (index < 3) BentoBlueCard else Color(0xFFF1F5F9),
                                modifier = Modifier.size(32.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = "#${index + 1}",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = BentoNavy
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "${prod.brand} ${prod.modelName}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = BentoNavy,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "${prod.category} • ${prod.unitsSold} units sold",
                                    fontSize = 11.sp,
                                    color = BentoTextSecondary
                                )
                            }
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "$currency ${numFormat.format(prod.totalRevenue)}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = BentoNavy
                            )
                            Text(
                                text = "+$currency ${numFormat.format(prod.totalProfit)} profit",
                                fontSize = 11.sp,
                                color = EmeraldSecondaryLight,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }

        // Branch Allocation Bento Card
        item {
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, BentoBorder, RoundedCornerShape(22.dp)),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Branch Stock Allocation",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = BentoNavy
                    )

                    analytics.branchStockBreakdown.forEach { (branchName, stockCount) ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = branchName, fontSize = 13.sp, color = BentoNavy, fontWeight = FontWeight.Medium)
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = BentoBlueCard
                            ) {
                                Text(
                                    text = "$stockCount in stock",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = BentoNavy,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

