package com.example.data.local

import android.content.Context
import android.content.SharedPreferences

data class AddUnitDraft(
    val modelName: String = "",
    val brand: String = "",
    val category: String = "Laptops",
    val condition: String = "Brand New",
    val serialNumber: String = "",
    val cpu: String = "Intel Core i7",
    val gpu: String = "Intel Iris Xe",
    val ram: String = "16 GB",
    val storage: String = "512 GB SSD",
    val color: String = "Black",
    val purchasePriceStr: String = "",
    val suggestedPriceStr: String = "",
    val purchaseSource: String = "Official Distributor",
    val notes: String = "",
    val selectedBranchId: String = "",
    val hasActiveDraft: Boolean = false
)

data class BillingDraft(
    val selectedUnitId: String? = null,
    val customerName: String = "",
    val customerPhone: String = "",
    val customerAddress: String = "",
    val sellingPriceStr: String = "",
    val paymentType: String = "OFFLINE",
    val bankingAppName: String = "bKash / Google Pay / Card",
    val serviceWarrantyYears: Double = 2.0,
    val replacementWarrantyDays: Int = 15,
    val warrantyTerms: String = "Standard shop hardware service warranty. Covers internal components. Physical and liquid damage void warranty.",
    val hasActiveDraft: Boolean = false
)

class DraftManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("login_gadget_drafts", Context.MODE_PRIVATE)

    // --- Add Unit Draft ---
    fun saveAddUnitDraft(draft: AddUnitDraft) {
        val isSubstantive = draft.modelName.isNotBlank() ||
                draft.brand.isNotBlank() ||
                draft.serialNumber.isNotBlank() ||
                draft.purchasePriceStr.isNotBlank() ||
                draft.notes.isNotBlank()

        prefs.edit().apply {
            putBoolean("unit_has_draft", isSubstantive)
            putString("unit_modelName", draft.modelName)
            putString("unit_brand", draft.brand)
            putString("unit_category", draft.category)
            putString("unit_condition", draft.condition)
            putString("unit_serialNumber", draft.serialNumber)
            putString("unit_cpu", draft.cpu)
            putString("unit_gpu", draft.gpu)
            putString("unit_ram", draft.ram)
            putString("unit_storage", draft.storage)
            putString("unit_color", draft.color)
            putString("unit_purchasePriceStr", draft.purchasePriceStr)
            putString("unit_suggestedPriceStr", draft.suggestedPriceStr)
            putString("unit_purchaseSource", draft.purchaseSource)
            putString("unit_notes", draft.notes)
            putString("unit_selectedBranchId", draft.selectedBranchId)
            apply()
        }
    }

    fun getAddUnitDraft(): AddUnitDraft {
        val hasDraft = prefs.getBoolean("unit_has_draft", false)
        return AddUnitDraft(
            modelName = prefs.getString("unit_modelName", "") ?: "",
            brand = prefs.getString("unit_brand", "") ?: "",
            category = prefs.getString("unit_category", "Laptops") ?: "Laptops",
            condition = prefs.getString("unit_condition", "Brand New") ?: "Brand New",
            serialNumber = prefs.getString("unit_serialNumber", "") ?: "",
            cpu = prefs.getString("unit_cpu", "Intel Core i7") ?: "Intel Core i7",
            gpu = prefs.getString("unit_gpu", "Intel Iris Xe") ?: "Intel Iris Xe",
            ram = prefs.getString("unit_ram", "16 GB") ?: "16 GB",
            storage = prefs.getString("unit_storage", "512 GB SSD") ?: "512 GB SSD",
            color = prefs.getString("unit_color", "Black") ?: "Black",
            purchasePriceStr = prefs.getString("unit_purchasePriceStr", "") ?: "",
            suggestedPriceStr = prefs.getString("unit_suggestedPriceStr", "") ?: "",
            purchaseSource = prefs.getString("unit_purchaseSource", "Official Distributor") ?: "Official Distributor",
            notes = prefs.getString("unit_notes", "") ?: "",
            selectedBranchId = prefs.getString("unit_selectedBranchId", "") ?: "",
            hasActiveDraft = hasDraft
        )
    }

    fun clearAddUnitDraft() {
        prefs.edit().apply {
            remove("unit_has_draft")
            remove("unit_modelName")
            remove("unit_brand")
            remove("unit_category")
            remove("unit_condition")
            remove("unit_serialNumber")
            remove("unit_cpu")
            remove("unit_gpu")
            remove("unit_ram")
            remove("unit_storage")
            remove("unit_color")
            remove("unit_purchasePriceStr")
            remove("unit_suggestedPriceStr")
            remove("unit_purchaseSource")
            remove("unit_notes")
            remove("unit_selectedBranchId")
            apply()
        }
    }

    // --- Billing Draft ---
    fun saveBillingDraft(draft: BillingDraft) {
        val isSubstantive = draft.customerName.isNotBlank() ||
                draft.customerPhone.isNotBlank() ||
                draft.customerAddress.isNotBlank() ||
                draft.sellingPriceStr.isNotBlank() ||
                draft.selectedUnitId != null

        prefs.edit().apply {
            putBoolean("billing_has_draft", isSubstantive)
            putString("billing_unitId", draft.selectedUnitId)
            putString("billing_custName", draft.customerName)
            putString("billing_custPhone", draft.customerPhone)
            putString("billing_custAddress", draft.customerAddress)
            putString("billing_priceStr", draft.sellingPriceStr)
            putString("billing_payType", draft.paymentType)
            putString("billing_bankName", draft.bankingAppName)
            putFloat("billing_warrantyYears", draft.serviceWarrantyYears.toFloat())
            putInt("billing_replaceDays", draft.replacementWarrantyDays)
            putString("billing_warrantyTerms", draft.warrantyTerms)
            apply()
        }
    }

    fun getBillingDraft(): BillingDraft {
        val hasDraft = prefs.getBoolean("billing_has_draft", false)
        return BillingDraft(
            selectedUnitId = prefs.getString("billing_unitId", null),
            customerName = prefs.getString("billing_custName", "") ?: "",
            customerPhone = prefs.getString("billing_custPhone", "") ?: "",
            customerAddress = prefs.getString("billing_custAddress", "") ?: "",
            sellingPriceStr = prefs.getString("billing_priceStr", "") ?: "",
            paymentType = prefs.getString("billing_payType", "OFFLINE") ?: "OFFLINE",
            bankingAppName = prefs.getString("billing_bankName", "bKash / Google Pay / Card") ?: "bKash / Google Pay / Card",
            serviceWarrantyYears = prefs.getFloat("billing_warrantyYears", 2.0f).toDouble(),
            replacementWarrantyDays = prefs.getInt("billing_replaceDays", 15),
            warrantyTerms = prefs.getString(
                "billing_warrantyTerms",
                "Standard shop hardware service warranty. Covers internal components. Physical and liquid damage void warranty."
            ) ?: "Standard shop hardware service warranty. Covers internal components. Physical and liquid damage void warranty.",
            hasActiveDraft = hasDraft
        )
    }

    fun clearBillingDraft() {
        prefs.edit().apply {
            remove("billing_has_draft")
            remove("billing_unitId")
            remove("billing_custName")
            remove("billing_custPhone")
            remove("billing_custAddress")
            remove("billing_priceStr")
            remove("billing_payType")
            remove("billing_bankName")
            remove("billing_warrantyYears")
            remove("billing_replaceDays")
            remove("billing_warrantyTerms")
            apply()
        }
    }
}
