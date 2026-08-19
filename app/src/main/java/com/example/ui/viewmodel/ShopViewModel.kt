package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.local.AddUnitDraft
import com.example.data.local.BillingDraft
import com.example.data.local.DraftManager
import com.example.data.model.Branch
import com.example.data.model.InventoryUnit
import com.example.data.model.ProductModel
import com.example.data.model.SaleRecord
import com.example.data.model.ShopProfile
import com.example.data.model.SyncStatus
import com.example.data.repository.ShopRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

enum class SortOption {
    LATEST,
    OLDEST,
    ALPHABETICAL_AZ,
    ALPHABETICAL_ZA,
    PRICE_HIGH_LOW,
    PRICE_LOW_HIGH
}

data class TopProductStat(
    val modelName: String,
    val brand: String,
    val category: String,
    val unitsSold: Int,
    val totalRevenue: Double,
    val totalProfit: Double
)

data class TopCustomerStat(
    val customerName: String,
    val customerPhone: String,
    val totalPurchasesCount: Int,
    val totalSpent: Double,
    val lastPurchaseDate: Long
)

data class TimePeriodFinance(
    val periodLabel: String,
    val purchaseAmount: Double,
    val salesAmount: Double,
    val profitAmount: Double,
    val unitsSold: Int
)

data class DashboardAnalytics(
    val totalInventoryStockValue: Double = 0.0,
    val totalEstimatedRetailValue: Double = 0.0,
    val totalInStockUnitsCount: Int = 0,
    val totalSoldUnitsCount: Int = 0,
    val totalLifetimeRevenue: Double = 0.0,
    val totalLifetimeProfit: Double = 0.0,
    val topSellingProducts: List<TopProductStat> = emptyList(),
    val topCustomers: List<TopCustomerStat> = emptyList(),
    val monthlyFinances: List<TimePeriodFinance> = emptyList(),
    val yearlyFinances: List<TimePeriodFinance> = emptyList(),
    val conditionBreakdown: Map<String, Int> = emptyMap(),
    val branchStockBreakdown: Map<String, Int> = emptyMap()
)

class ShopViewModel(
    private val repository: ShopRepository,
    private val draftManager: DraftManager? = null
) : ViewModel() {

    val shopProfile: StateFlow<ShopProfile?> = repository.shopProfile
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val branches: StateFlow<List<Branch>> = repository.allBranches
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val productModels: StateFlow<List<ProductModel>> = repository.allProductModels
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allUnits: StateFlow<List<InventoryUnit>> = repository.allInventoryUnits
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val inStockUnits: StateFlow<List<InventoryUnit>> = repository.inStockInventoryUnits
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val salesHistory: StateFlow<List<SaleRecord>> = repository.allSaleRecords
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val syncStatus: StateFlow<SyncStatus> = repository.syncStatus

    // Filtering and sorting state
    private val _selectedBranchFilter = MutableStateFlow<String?>("ALL")
    val selectedBranchFilter = _selectedBranchFilter.asStateFlow()

    private val _selectedCategoryFilter = MutableStateFlow("All")
    val selectedCategoryFilter = _selectedCategoryFilter.asStateFlow()

    private val _selectedConditionFilter = MutableStateFlow("All")
    val selectedConditionFilter = _selectedConditionFilter.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _sortOption = MutableStateFlow(SortOption.LATEST)
    val sortOption = _sortOption.asStateFlow()

    // Filter criteria helper flow
    data class FilterCriteria(
        val branchId: String?,
        val category: String,
        val condition: String,
        val query: String,
        val sort: SortOption
    )

    private val filterCriteriaFlow = combine(
        _selectedBranchFilter,
        _selectedCategoryFilter,
        _selectedConditionFilter,
        _searchQuery,
        _sortOption
    ) { branchId, category, condition, query, sort ->
        FilterCriteria(branchId, category, condition, query, sort)
    }

    val filteredInventoryUnits: StateFlow<List<InventoryUnit>> = combine(
        inStockUnits,
        filterCriteriaFlow
    ) { units, criteria ->
        var list = units.filter { it.status == "IN_STOCK" }

        if (criteria.branchId != null && criteria.branchId != "ALL") {
            list = list.filter { it.branchId == criteria.branchId }
        }

        if (criteria.category != "All") {
            list = list.filter { it.category.equals(criteria.category, ignoreCase = true) }
        }

        if (criteria.condition != "All") {
            list = list.filter { it.condition.equals(criteria.condition, ignoreCase = true) }
        }

        if (criteria.query.isNotBlank()) {
            val q = criteria.query.trim().lowercase()
            list = list.filter {
                it.modelName.lowercase().contains(q) ||
                it.brand.lowercase().contains(q) ||
                it.serialNumber.lowercase().contains(q) ||
                it.cpu.lowercase().contains(q) ||
                it.ram.lowercase().contains(q) ||
                it.storage.lowercase().contains(q)
            }
        }

        when (criteria.sort) {
            SortOption.LATEST -> list.sortedByDescending { it.purchaseDate }
            SortOption.OLDEST -> list.sortedBy { it.purchaseDate }
            SortOption.ALPHABETICAL_AZ -> list.sortedBy { it.modelName.lowercase() }
            SortOption.ALPHABETICAL_ZA -> list.sortedByDescending { it.modelName.lowercase() }
            SortOption.PRICE_HIGH_LOW -> list.sortedByDescending { it.suggestedSellingPrice }
            SortOption.PRICE_LOW_HIGH -> list.sortedBy { it.suggestedSellingPrice }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Dashboard Analytics flow
    val dashboardAnalytics: StateFlow<DashboardAnalytics> = combine(
        inStockUnits,
        salesHistory,
        branches
    ) { inStock, sales, branchList ->
        val inStockValue = inStock.sumOf { it.purchasePrice }
        val retailValue = inStock.sumOf { if (it.suggestedSellingPrice > 0) it.suggestedSellingPrice else it.purchasePrice * 1.2 }

        val lifetimeRev = sales.sumOf { it.sellingPrice }
        val lifetimeProf = sales.sumOf { it.profit }

        // Top 10 Products
        val topProducts = sales.groupBy { "${it.brand} ${it.modelName}" }
            .map { (_, records) ->
                val first = records.first()
                TopProductStat(
                    modelName = first.modelName,
                    brand = first.brand,
                    category = first.category,
                    unitsSold = records.size,
                    totalRevenue = records.sumOf { it.sellingPrice },
                    totalProfit = records.sumOf { it.profit }
                )
            }
            .sortedByDescending { it.unitsSold }
            .take(10)

        // Top 10 Customers
        val topCustomers = sales.groupBy { it.customerPhone.ifEmpty { it.customerName } }
            .map { (_, records) ->
                val first = records.first()
                TopCustomerStat(
                    customerName = first.customerName,
                    customerPhone = first.customerPhone,
                    totalPurchasesCount = records.size,
                    totalSpent = records.sumOf { it.sellingPrice },
                    lastPurchaseDate = records.maxOfOrNull { it.saleDate } ?: 0L
                )
            }
            .sortedByDescending { it.totalSpent }
            .take(10)

        // Monthly Finance
        val monthFormat = SimpleDateFormat("MMM yyyy", Locale.getDefault())
        val monthlyFinances = sales.groupBy {
            monthFormat.format(Date(it.saleDate))
        }.map { (monthLabel, records) ->
            TimePeriodFinance(
                periodLabel = monthLabel,
                purchaseAmount = records.sumOf { it.purchasePrice },
                salesAmount = records.sumOf { it.sellingPrice },
                profitAmount = records.sumOf { it.profit },
                unitsSold = records.size
            )
        }

        // Yearly Finance
        val yearFormat = SimpleDateFormat("yyyy", Locale.getDefault())
        val yearlyFinances = sales.groupBy {
            yearFormat.format(Date(it.saleDate))
        }.map { (yearLabel, records) ->
            TimePeriodFinance(
                periodLabel = yearLabel,
                purchaseAmount = records.sumOf { it.purchasePrice },
                salesAmount = records.sumOf { it.sellingPrice },
                profitAmount = records.sumOf { it.profit },
                unitsSold = records.size
            )
        }

        // Condition Breakdown
        val conditionMap = inStock.groupBy { it.condition }.mapValues { it.value.size }

        // Branch Breakdown
        val branchMap = mutableMapOf<String, Int>()
        branchList.forEach { b ->
            branchMap[b.name] = inStock.count { it.branchId == b.id }
        }

        DashboardAnalytics(
            totalInventoryStockValue = inStockValue,
            totalEstimatedRetailValue = retailValue,
            totalInStockUnitsCount = inStock.size,
            totalSoldUnitsCount = sales.size,
            totalLifetimeRevenue = lifetimeRev,
            totalLifetimeProfit = lifetimeProf,
            topSellingProducts = topProducts,
            topCustomers = topCustomers,
            monthlyFinances = monthlyFinances,
            yearlyFinances = yearlyFinances,
            conditionBreakdown = conditionMap,
            branchStockBreakdown = branchMap
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DashboardAnalytics())

    // Active Selection State for POS Billing
    private val _selectedUnitForBilling = MutableStateFlow<InventoryUnit?>(null)
    val selectedUnitForBilling = _selectedUnitForBilling.asStateFlow()

    private val _activeInvoice = MutableStateFlow<SaleRecord?>(null)
    val activeInvoice = _activeInvoice.asStateFlow()

    private val _billingErrorMessage = MutableStateFlow<String?>(null)
    val billingErrorMessage = _billingErrorMessage.asStateFlow()

    // Actions
    fun initializeShop(
        shopName: String,
        ownerEmail: String,
        branch1Name: String,
        branch2Name: String,
        currency: String,
        prefillSampleLaptops: Boolean
    ) {
        viewModelScope.launch {
            repository.initializeShop(
                shopName = shopName,
                ownerEmail = ownerEmail,
                branch1Name = branch1Name,
                branch2Name = branch2Name,
                currencySymbol = currency,
                prefillSampleLaptops = prefillSampleLaptops
            )
        }
    }

    fun setBranchFilter(branchId: String?) {
        _selectedBranchFilter.value = branchId
    }

    fun setCategoryFilter(category: String) {
        _selectedCategoryFilter.value = category
    }

    fun setConditionFilter(condition: String) {
        _selectedConditionFilter.value = condition
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSortOption(sort: SortOption) {
        _sortOption.value = sort
    }

    fun addInventoryUnit(unit: InventoryUnit) {
        viewModelScope.launch {
            repository.addInventoryUnit(unit)
            draftManager?.clearAddUnitDraft()
        }
    }

    fun saveAddUnitDraft(draft: AddUnitDraft) {
        draftManager?.saveAddUnitDraft(draft)
    }

    fun getAddUnitDraft(): AddUnitDraft {
        return draftManager?.getAddUnitDraft() ?: AddUnitDraft()
    }

    fun clearAddUnitDraft() {
        draftManager?.clearAddUnitDraft()
    }

    fun saveBillingDraft(draft: BillingDraft) {
        draftManager?.saveBillingDraft(draft)
    }

    fun getBillingDraft(): BillingDraft {
        return draftManager?.getBillingDraft() ?: BillingDraft()
    }

    fun clearBillingDraft() {
        draftManager?.clearBillingDraft()
    }

    suspend fun getBackupJsonString(): String {
        return repository.getBackupJsonString()
    }

    fun restoreFromJson(jsonString: String, onComplete: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val result = repository.restoreFromJsonString(jsonString)
            result.onSuccess { msg ->
                onComplete(true, msg)
            }.onFailure { err ->
                onComplete(false, err.localizedMessage ?: "Restore failed")
            }
        }
    }

    fun deleteInventoryUnit(unitId: String) {
        viewModelScope.launch {
            repository.deleteInventoryUnit(unitId)
        }
    }

    fun selectUnitForBilling(unit: InventoryUnit?) {
        _selectedUnitForBilling.value = unit
        _billingErrorMessage.value = null
    }

    fun processSale(
        customerName: String,
        customerPhone: String,
        customerAddress: String,
        sellingPrice: Double,
        paymentType: String,
        bankingAppName: String,
        serviceWarrantyYears: Double,
        replacementWarrantyDays: Int,
        warrantyTerms: String
    ) {
        val unit = _selectedUnitForBilling.value
        if (unit == null) {
            _billingErrorMessage.value = "Please select a product from inventory first"
            return
        }
        if (customerName.isBlank()) {
            _billingErrorMessage.value = "Customer name is required"
            return
        }
        if (sellingPrice <= 0) {
            _billingErrorMessage.value = "Selling price must be greater than 0"
            return
        }

        viewModelScope.launch {
            try {
                val invoice = repository.sellInventoryUnit(
                    unitId = unit.id,
                    customerName = customerName.trim(),
                    customerPhone = customerPhone.trim(),
                    customerAddress = customerAddress.trim(),
                    sellingPrice = sellingPrice,
                    paymentType = paymentType,
                    bankingAppName = if (paymentType == "ONLINE") bankingAppName.trim() else "Cash on Counter",
                    serviceWarrantyYears = serviceWarrantyYears,
                    replacementWarrantyDays = replacementWarrantyDays,
                    warrantyTerms = warrantyTerms.trim()
                )
                _activeInvoice.value = invoice
                _selectedUnitForBilling.value = null
                _billingErrorMessage.value = null
                draftManager?.clearBillingDraft()
            } catch (e: Exception) {
                _billingErrorMessage.value = e.localizedMessage ?: "Failed to process sale"
            }
        }
    }

    fun viewExistingInvoice(invoice: SaleRecord) {
        _activeInvoice.value = invoice
    }

    fun clearActiveInvoice() {
        _activeInvoice.value = null
    }

    fun syncToGoogleDrive() {
        viewModelScope.launch {
            repository.syncToGoogleDrive()
        }
    }

    fun restoreFromGoogleDrive() {
        viewModelScope.launch {
            repository.restoreFromGoogleDrive()
        }
    }

    fun addBranch(name: String, location: String, phone: String) {
        viewModelScope.launch {
            repository.addBranch(name, location, phone)
        }
    }

    fun deleteBranch(branchId: String) {
        viewModelScope.launch {
            repository.deleteBranch(branchId)
        }
    }

    fun updateShopProfile(profile: ShopProfile) {
        viewModelScope.launch {
            repository.updateShopProfile(profile)
        }
    }
}

class ShopViewModelFactory(
    private val repository: ShopRepository,
    private val draftManager: DraftManager? = null
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ShopViewModel::class.java)) {
            return ShopViewModel(repository, draftManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
