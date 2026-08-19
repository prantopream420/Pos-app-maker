package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "shop_profile")
data class ShopProfile(
    @PrimaryKey val id: String = "primary_shop",
    val shopName: String,
    val ownerEmail: String = "preamleelapranto@gmail.com",
    val ownerName: String = "Shop Owner",
    val isGoogleConnected: Boolean = true,
    val driveBackupPath: String = "/Google Drive/Shop_Databases/login_gadget_db.json",
    val currencySymbol: String = "$",
    val defaultServiceWarrantyMonths: Int = 24, // 2 years default
    val defaultReplacementWarrantyDays: Int = 15, // 15 days default
    val isInitialized: Boolean = true,
    val createdAt: Long = System.currentTimeMillis(),
    val lastSyncTimestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "branches")
data class Branch(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val name: String,
    val location: String = "Main Street Plaza",
    val phone: String = "+1 (555) 019-2834",
    val isDefault: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "product_models")
data class ProductModel(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val name: String,
    val brand: String,
    val category: String = "Laptops",
    val description: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "inventory_units")
data class InventoryUnit(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val modelId: String = "",
    val modelName: String,
    val brand: String,
    val category: String = "Laptops", // "Laptops", "Smartphones", "Accessories", "Audio", "Tablets", "Custom"
    val serialNumber: String, // e.g. "SN-LEN-98421" or IMEI
    val branchId: String,
    val condition: String = "Brand New", // "Brand New", "Used", "Open Box", "Special Unit", "Refurbished"
    val cpu: String = "Intel Core i7 13th Gen",
    val gpu: String = "Intel Iris Xe",
    val ram: String = "16 GB DDR5",
    val storage: String = "512 GB NVMe SSD",
    val color: String = "Midnight Slate",
    val purchasePrice: Double, // Shop purchase cost (kept confidential from invoices)
    val suggestedSellingPrice: Double = 0.0,
    val purchaseDate: Long = System.currentTimeMillis(),
    val purchaseSource: String = "Official Tech Distributor", // Supplier / Vendor / Direct Import / Trade-in
    val status: String = "IN_STOCK", // "IN_STOCK", "SOLD", "RETURNED"
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "sales_records")
data class SaleRecord(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val invoiceNumber: String, // e.g. "INV-2026-0001"
    val unitId: String,
    val serialNumber: String,
    val modelName: String,
    val brand: String,
    val category: String,
    val condition: String,
    val cpu: String,
    val gpu: String,
    val ram: String,
    val storage: String,
    val color: String,
    val branchId: String,
    val branchName: String,
    val customerName: String,
    val customerPhone: String,
    val customerAddress: String,
    val purchasePrice: Double, // for internal profit tracking
    val sellingPrice: Double,
    val profit: Double,
    val paymentType: String, // "ONLINE" or "OFFLINE"
    val bankingAppName: String = "", // e.g. "bKash", "Google Pay", "Chase Bank", "Nagad", "Cash"
    val serviceWarrantyYears: Double = 2.0, // e.g. 2 years service warranty
    val replacementWarrantyDays: Int = 15, // e.g. 15 days replacement
    val warrantyTerms: String = "Standard manufacturer & shop hardware warranty. Physical & liquid damage not covered.",
    val saleDate: Long = System.currentTimeMillis(),
    val soldBy: String = "Store Manager"
)
