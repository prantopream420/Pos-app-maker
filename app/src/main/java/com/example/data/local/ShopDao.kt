package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.data.model.Branch
import com.example.data.model.InventoryUnit
import com.example.data.model.ProductModel
import com.example.data.model.SaleRecord
import com.example.data.model.ShopProfile
import kotlinx.coroutines.flow.Flow

@Dao
interface ShopDao {

    // --- Shop Profile ---
    @Query("SELECT * FROM shop_profile WHERE id = 'primary_shop' LIMIT 1")
    fun getShopProfile(): Flow<ShopProfile?>

    @Query("SELECT * FROM shop_profile WHERE id = 'primary_shop' LIMIT 1")
    suspend fun getShopProfileDirect(): ShopProfile?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProfile(profile: ShopProfile)

    // --- Branches ---
    @Query("SELECT * FROM branches ORDER BY isDefault DESC, createdAt ASC")
    fun getAllBranches(): Flow<List<Branch>>

    @Query("SELECT * FROM branches ORDER BY createdAt ASC")
    suspend fun getAllBranchesDirect(): List<Branch>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBranch(branch: Branch)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBranches(branches: List<Branch>)

    @Update
    suspend fun updateBranch(branch: Branch)

    @Query("DELETE FROM branches WHERE id = :id")
    suspend fun deleteBranch(id: String)

    // --- Product Catalog Models ---
    @Query("SELECT * FROM product_models ORDER BY name ASC")
    fun getAllProductModels(): Flow<List<ProductModel>>

    @Query("SELECT * FROM product_models ORDER BY name ASC")
    suspend fun getAllProductModelsDirect(): List<ProductModel>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProductModel(model: ProductModel)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProductModels(models: List<ProductModel>)

    // --- Inventory Units ---
    @Query("SELECT * FROM inventory_units ORDER BY purchaseDate DESC")
    fun getAllInventoryUnits(): Flow<List<InventoryUnit>>

    @Query("SELECT * FROM inventory_units WHERE status = 'IN_STOCK' ORDER BY purchaseDate DESC")
    fun getInStockUnits(): Flow<List<InventoryUnit>>

    @Query("SELECT * FROM inventory_units WHERE branchId = :branchId AND status = 'IN_STOCK' ORDER BY purchaseDate DESC")
    fun getInStockUnitsByBranch(branchId: String): Flow<List<InventoryUnit>>

    @Query("SELECT * FROM inventory_units WHERE id = :id LIMIT 1")
    suspend fun getInventoryUnitById(id: String): InventoryUnit?

    @Query("SELECT * FROM inventory_units WHERE serialNumber = :serialNumber LIMIT 1")
    suspend fun getInventoryUnitBySerial(serialNumber: String): InventoryUnit?

    @Query("SELECT * FROM inventory_units ORDER BY purchaseDate DESC")
    suspend fun getAllInventoryUnitsDirect(): List<InventoryUnit>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInventoryUnit(unit: InventoryUnit)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInventoryUnits(units: List<InventoryUnit>)

    @Update
    suspend fun updateInventoryUnit(unit: InventoryUnit)

    @Query("UPDATE inventory_units SET status = :status, updatedAt = :updatedAt WHERE id = :id")
    suspend fun updateUnitStatus(id: String, status: String, updatedAt: Long = System.currentTimeMillis())

    @Query("DELETE FROM inventory_units WHERE id = :id")
    suspend fun deleteInventoryUnit(id: String)

    // --- Sales Records ---
    @Query("SELECT * FROM sales_records ORDER BY saleDate DESC")
    fun getAllSaleRecords(): Flow<List<SaleRecord>>

    @Query("SELECT * FROM sales_records WHERE id = :id LIMIT 1")
    suspend fun getSaleRecordById(id: String): SaleRecord?

    @Query("SELECT * FROM sales_records ORDER BY saleDate DESC")
    suspend fun getAllSaleRecordsDirect(): List<SaleRecord>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSaleRecord(record: SaleRecord)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSaleRecords(records: List<SaleRecord>)

    @Query("DELETE FROM sales_records WHERE id = :id")
    suspend fun deleteSaleRecord(id: String)

    // --- Complete Sale Flow (Transaction) ---
    @Transaction
    suspend fun processSaleTransaction(saleRecord: SaleRecord) {
        insertSaleRecord(saleRecord)
        updateUnitStatus(
            id = saleRecord.unitId,
            status = "SOLD",
            updatedAt = saleRecord.saleDate
        )
    }

    // --- Clear & Restore all tables ---
    @Query("DELETE FROM shop_profile")
    suspend fun clearProfile()

    @Query("DELETE FROM branches")
    suspend fun clearBranches()

    @Query("DELETE FROM product_models")
    suspend fun clearProductModels()

    @Query("DELETE FROM inventory_units")
    suspend fun clearInventoryUnits()

    @Query("DELETE FROM sales_records")
    suspend fun clearSalesRecords()

    @Transaction
    suspend fun restoreDatabase(
        profile: ShopProfile?,
        branches: List<Branch>,
        models: List<ProductModel>,
        units: List<InventoryUnit>,
        sales: List<SaleRecord>
    ) {
        clearBranches()
        clearProductModels()
        clearInventoryUnits()
        clearSalesRecords()
        if (profile != null) {
            insertOrUpdateProfile(profile)
        }
        if (branches.isNotEmpty()) insertBranches(branches)
        if (models.isNotEmpty()) insertProductModels(models)
        if (units.isNotEmpty()) insertInventoryUnits(units)
        if (sales.isNotEmpty()) insertSaleRecords(sales)
    }
}
