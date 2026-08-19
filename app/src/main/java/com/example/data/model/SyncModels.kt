package com.example.data.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class DatabaseBackupPayload(
    val exportVersion: Int = 1,
    val exportedAt: Long = System.currentTimeMillis(),
    val shopProfile: ShopProfile?,
    val branches: List<Branch>,
    val productModels: List<ProductModel>,
    val inventoryUnits: List<InventoryUnit>,
    val salesRecords: List<SaleRecord>
)

enum class SyncState {
    SYNCED,
    SYNCING,
    OFFLINE_LOCAL,
    UNSYNCED_CHANGES,
    ERROR
}

data class SyncStatus(
    val state: SyncState = SyncState.SYNCED,
    val lastSyncTime: Long = System.currentTimeMillis(),
    val pendingChangesCount: Int = 0,
    val driveAccount: String = "preamleelapranto@gmail.com",
    val driveFilePath: String = "Google Drive > Shop_Backups > login_gadget_db.json",
    val lastMessage: String = "Connected & database synced with Google Drive"
)
