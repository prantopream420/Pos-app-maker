package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.PointOfSale
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.outlined.CloudSync
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.PointOfSale
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.local.AppDatabase
import com.example.data.model.InventoryUnit
import com.example.data.model.SaleRecord
import com.example.data.repository.ShopRepository
import com.example.ui.screens.AddUnitBottomSheet
import com.example.ui.screens.BillingScreen
import com.example.ui.screens.BranchesAndSyncScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.InventoryScreen
import com.example.ui.screens.InvoiceViewScreen
import com.example.ui.screens.OnboardingScreen
import com.example.ui.screens.SalesHistoryScreen
import com.example.ui.theme.BentoBlueCard
import com.example.ui.theme.BentoIndigo
import com.example.ui.theme.BentoNavy
import com.example.ui.theme.BentoTextSecondary
import com.example.ui.theme.EmeraldSecondaryLight
import com.example.ui.theme.IndigoPrimaryLight
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.ShopViewModel
import com.example.ui.viewmodel.ShopViewModelFactory

enum class AppNavDestination {
    DASHBOARD,
    INVENTORY,
    BILLING,
    SALES_HISTORY,
    SYNC_BRANCHES
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = AppDatabase.getDatabase(applicationContext)
        val repository = ShopRepository(database.shopDao(), applicationContext)
        val draftManager = com.example.data.local.DraftManager(applicationContext)

        setContent {
            MyApplicationTheme {
                val viewModel: ShopViewModel = viewModel(
                    factory = ShopViewModelFactory(repository, draftManager)
                )
                ShopAppMain(viewModel)
            }
        }
    }
}

@Composable
fun ShopAppMain(viewModel: ShopViewModel) {
    val shopProfile by viewModel.shopProfile.collectAsState()
    val branches by viewModel.branches.collectAsState()
    val inStockUnits by viewModel.inStockUnits.collectAsState()
    val filteredUnits by viewModel.filteredInventoryUnits.collectAsState()
    val salesHistory by viewModel.salesHistory.collectAsState()
    val dashboardAnalytics by viewModel.dashboardAnalytics.collectAsState()
    val syncStatus by viewModel.syncStatus.collectAsState()

    val selectedBranchFilter by viewModel.selectedBranchFilter.collectAsState()
    val selectedCategoryFilter by viewModel.selectedCategoryFilter.collectAsState()
    val selectedConditionFilter by viewModel.selectedConditionFilter.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val sortOption by viewModel.sortOption.collectAsState()

    val selectedUnitForBilling by viewModel.selectedUnitForBilling.collectAsState()
    val activeInvoice by viewModel.activeInvoice.collectAsState()
    val billingErrorMessage by viewModel.billingErrorMessage.collectAsState()

    var currentDestination by remember { mutableStateOf(AppNavDestination.DASHBOARD) }

    // Bottom sheet state for Adding Inventory or Model Variant
    var showAddUnitSheet by remember { mutableStateOf(false) }
    var variantPrefillModel by remember { mutableStateOf<String?>(null) }
    var variantPrefillBrand by remember { mutableStateOf<String?>(null) }
    var variantPrefillCategory by remember { mutableStateOf<String?>(null) }

    // If no shop profile exists yet, show onboarding screen
    if (shopProfile == null) {
        OnboardingScreen(
            onComplete = { shopName, ownerEmail, branch1, branch2, currency, prefill ->
                viewModel.initializeShop(
                    shopName = shopName,
                    ownerEmail = ownerEmail,
                    branch1Name = branch1,
                    branch2Name = branch2,
                    currency = currency,
                    prefillSampleLaptops = prefill
                )
            }
        )
    } else if (activeInvoice != null) {
        // Dedicated Invoice View
        InvoiceViewScreen(
            invoice = activeInvoice!!,
            shopProfile = shopProfile,
            onBackClick = {
                viewModel.clearActiveInvoice()
                currentDestination = AppNavDestination.SALES_HISTORY
            },
            onNewSaleClick = {
                viewModel.clearActiveInvoice()
                currentDestination = AppNavDestination.BILLING
            }
        )
    } else {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            bottomBar = {
                NavigationBar(
                    containerColor = androidx.compose.ui.graphics.Color.White,
                    tonalElevation = 2.dp
                ) {
                    NavigationBarItem(
                        selected = currentDestination == AppNavDestination.DASHBOARD,
                        onClick = { currentDestination = AppNavDestination.DASHBOARD },
                        icon = {
                            Icon(
                                if (currentDestination == AppNavDestination.DASHBOARD) Icons.Filled.Dashboard else Icons.Outlined.Dashboard,
                                contentDescription = "Dashboard"
                            )
                        },
                        label = { Text("Dashboard", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = BentoNavy,
                            selectedTextColor = BentoNavy,
                            indicatorColor = BentoBlueCard,
                            unselectedIconColor = BentoTextSecondary,
                            unselectedTextColor = BentoTextSecondary
                        ),
                        modifier = Modifier.testTag("nav_dashboard")
                    )

                    NavigationBarItem(
                        selected = currentDestination == AppNavDestination.INVENTORY,
                        onClick = { currentDestination = AppNavDestination.INVENTORY },
                        icon = {
                            BadgedBox(
                                badge = {
                                    if (inStockUnits.isNotEmpty()) {
                                        Badge(containerColor = BentoNavy) {
                                            Text("${inStockUnits.size}")
                                        }
                                    }
                                }
                            ) {
                                Icon(
                                    if (currentDestination == AppNavDestination.INVENTORY) Icons.Filled.Inventory2 else Icons.Outlined.Inventory2,
                                    contentDescription = "Inventory"
                                )
                            }
                        },
                        label = { Text("Stock", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = BentoNavy,
                            selectedTextColor = BentoNavy,
                            indicatorColor = BentoBlueCard,
                            unselectedIconColor = BentoTextSecondary,
                            unselectedTextColor = BentoTextSecondary
                        ),
                        modifier = Modifier.testTag("nav_inventory")
                    )

                    NavigationBarItem(
                        selected = currentDestination == AppNavDestination.BILLING,
                        onClick = { currentDestination = AppNavDestination.BILLING },
                        icon = {
                            Icon(
                                if (currentDestination == AppNavDestination.BILLING) Icons.Filled.PointOfSale else Icons.Outlined.PointOfSale,
                                contentDescription = "POS Bill"
                            )
                        },
                        label = { Text("POS Sale", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = BentoIndigo,
                            selectedTextColor = BentoIndigo,
                            indicatorColor = BentoIndigo.copy(alpha = 0.15f),
                            unselectedIconColor = BentoTextSecondary,
                            unselectedTextColor = BentoTextSecondary
                        ),
                        modifier = Modifier.testTag("nav_billing")
                    )

                    NavigationBarItem(
                        selected = currentDestination == AppNavDestination.SALES_HISTORY,
                        onClick = { currentDestination = AppNavDestination.SALES_HISTORY },
                        icon = {
                            Icon(
                                if (currentDestination == AppNavDestination.SALES_HISTORY) Icons.Filled.ReceiptLong else Icons.Outlined.ReceiptLong,
                                contentDescription = "Invoices"
                            )
                        },
                        label = { Text("Invoices", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = BentoNavy,
                            selectedTextColor = BentoNavy,
                            indicatorColor = BentoBlueCard,
                            unselectedIconColor = BentoTextSecondary,
                            unselectedTextColor = BentoTextSecondary
                        ),
                        modifier = Modifier.testTag("nav_sales")
                    )

                    NavigationBarItem(
                        selected = currentDestination == AppNavDestination.SYNC_BRANCHES,
                        onClick = { currentDestination = AppNavDestination.SYNC_BRANCHES },
                        icon = {
                            Icon(
                                if (currentDestination == AppNavDestination.SYNC_BRANCHES) Icons.Filled.CloudSync else Icons.Outlined.CloudSync,
                                contentDescription = "Drive Sync"
                            )
                        },
                        label = { Text("Sync & Hub", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = BentoNavy,
                            selectedTextColor = BentoNavy,
                            indicatorColor = BentoBlueCard,
                            unselectedIconColor = BentoTextSecondary,
                            unselectedTextColor = BentoTextSecondary
                        ),
                        modifier = Modifier.testTag("nav_sync")
                    )
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                AnimatedContent(
                    targetState = currentDestination,
                    transitionSpec = { fadeIn() togetherWith fadeOut() },
                    label = "ScreenTransition"
                ) { target ->
                    when (target) {
                        AppNavDestination.DASHBOARD -> {
                            DashboardScreen(
                                shopProfile = shopProfile,
                                analytics = dashboardAnalytics,
                                branches = branches,
                                syncStatus = syncStatus,
                                onNavigateToBilling = { currentDestination = AppNavDestination.BILLING },
                                onNavigateToInventory = { currentDestination = AppNavDestination.INVENTORY },
                                onNavigateToSales = { currentDestination = AppNavDestination.SALES_HISTORY },
                                onNavigateToSync = { currentDestination = AppNavDestination.SYNC_BRANCHES },
                                onTriggerSync = { viewModel.syncToGoogleDrive() }
                            )
                        }

                        AppNavDestination.INVENTORY -> {
                            InventoryScreen(
                                inventoryUnits = filteredUnits,
                                branches = branches,
                                selectedBranchFilter = selectedBranchFilter,
                                selectedCategoryFilter = selectedCategoryFilter,
                                selectedConditionFilter = selectedConditionFilter,
                                searchQuery = searchQuery,
                                sortOption = sortOption,
                                currencySymbol = shopProfile?.currencySymbol ?: "$",
                                onBranchFilterChange = { viewModel.setBranchFilter(it) },
                                onCategoryFilterChange = { viewModel.setCategoryFilter(it) },
                                onConditionFilterChange = { viewModel.setConditionFilter(it) },
                                onSearchQueryChange = { viewModel.setSearchQuery(it) },
                                onSortOptionChange = { viewModel.setSortOption(it) },
                                onAddUnitClick = {
                                    variantPrefillModel = null
                                    variantPrefillBrand = null
                                    variantPrefillCategory = null
                                    showAddUnitSheet = true
                                },
                                onAddVariantClick = { modelName, brand, category ->
                                    variantPrefillModel = modelName
                                    variantPrefillBrand = brand
                                    variantPrefillCategory = category
                                    showAddUnitSheet = true
                                },
                                onSellUnitClick = { unit ->
                                    viewModel.selectUnitForBilling(unit)
                                    currentDestination = AppNavDestination.BILLING
                                },
                                onDeleteUnitClick = { unitId ->
                                    viewModel.deleteInventoryUnit(unitId)
                                }
                            )
                        }

                        AppNavDestination.BILLING -> {
                            BillingScreen(
                                inStockUnits = inStockUnits,
                                branches = branches,
                                selectedUnit = selectedUnitForBilling,
                                errorMessage = billingErrorMessage,
                                currencySymbol = shopProfile?.currencySymbol ?: "$",
                                onSelectUnit = { unit -> viewModel.selectUnitForBilling(unit) },
                                onProcessSale = { customerName, customerPhone, customerAddress, sellingPrice, paymentType, bankingAppName, serviceWarrantyYears, replacementWarrantyDays, warrantyTerms ->
                                    viewModel.processSale(
                                        customerName = customerName,
                                        customerPhone = customerPhone,
                                        customerAddress = customerAddress,
                                        sellingPrice = sellingPrice,
                                        paymentType = paymentType,
                                        bankingAppName = bankingAppName,
                                        serviceWarrantyYears = serviceWarrantyYears,
                                        replacementWarrantyDays = replacementWarrantyDays,
                                        warrantyTerms = warrantyTerms
                                    )
                                }
                            )
                        }

                        AppNavDestination.SALES_HISTORY -> {
                            SalesHistoryScreen(
                                salesRecords = salesHistory,
                                currencySymbol = shopProfile?.currencySymbol ?: "$",
                                onSelectInvoice = { invoice ->
                                    viewModel.viewExistingInvoice(invoice)
                                }
                            )
                        }

                        AppNavDestination.SYNC_BRANCHES -> {
                            BranchesAndSyncScreen(
                                shopProfile = shopProfile,
                                branches = branches,
                                inventoryUnits = filteredUnits,
                                syncStatus = syncStatus,
                                onTriggerSync = { viewModel.syncToGoogleDrive() },
                                onTriggerRestore = { viewModel.restoreFromGoogleDrive() },
                                onCreateBranch = { name, location, phone ->
                                    viewModel.addBranch(name, location, phone)
                                },
                                onDeleteBranch = { branchId ->
                                    viewModel.deleteBranch(branchId)
                                },
                                onUpdateProfile = { updated ->
                                    viewModel.updateShopProfile(updated)
                                },
                                onExportJson = { viewModel.getBackupJsonString() },
                                onImportJson = { json, cb -> viewModel.restoreFromJson(json, cb) }
                            )
                        }
                    }
                }

                // Add Unit / Variant Modal Bottom Sheet
                if (showAddUnitSheet) {
                    AddUnitBottomSheet(
                        branches = branches,
                        prefillModelName = variantPrefillModel,
                        prefillBrand = variantPrefillBrand,
                        prefillCategory = variantPrefillCategory,
                        onDismiss = { showAddUnitSheet = false },
                        onSaveUnit = { unit ->
                            viewModel.addInventoryUnit(unit)
                            showAddUnitSheet = false
                        }
                    )
                }
            }
        }
    }
}
