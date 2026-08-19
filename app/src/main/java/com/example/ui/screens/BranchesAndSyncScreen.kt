package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Branch
import com.example.data.model.InventoryUnit
import com.example.data.model.ShopProfile
import com.example.data.model.SyncState
import com.example.data.model.SyncStatus
import com.example.ui.theme.AmberTertiaryLight
import com.example.ui.theme.BentoAmberCard
import com.example.ui.theme.BentoAmberCardText
import com.example.ui.theme.BentoBlueCard
import com.example.ui.theme.BentoBorder
import com.example.ui.theme.BentoEmeraldCard
import com.example.ui.theme.BentoEmeraldCardText
import com.example.ui.theme.BentoIndigo
import com.example.ui.theme.BentoNavy
import com.example.ui.theme.BentoTextPrimary
import com.example.ui.theme.BentoTextSecondary
import com.example.ui.theme.EmeraldSecondaryLight
import com.example.ui.theme.IndigoPrimaryLight
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun BranchesAndSyncScreen(
    shopProfile: ShopProfile?,
    branches: List<Branch>,
    inventoryUnits: List<InventoryUnit>,
    syncStatus: SyncStatus,
    onTriggerSync: () -> Unit,
    onTriggerRestore: () -> Unit,
    onCreateBranch: (name: String, location: String, phone: String) -> Unit,
    onDeleteBranch: (String) -> Unit,
    onUpdateProfile: (ShopProfile) -> Unit,
    onExportJson: (suspend () -> String)? = null,
    onImportJson: ((String, (Boolean, String) -> Unit) -> Unit)? = null
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var showAddBranchDialog by remember { mutableStateOf(false) }
    var newBranchName by remember { mutableStateOf("") }
    var newBranchLocation by remember { mutableStateOf("") }
    var newBranchPhone by remember { mutableStateOf("") }

    var showEditProfileDialog by remember { mutableStateOf(false) }
    var editShopName by remember { mutableStateOf(shopProfile?.shopName ?: "LOGIN GADGET") }
    var editCurrency by remember { mutableStateOf(shopProfile?.currencySymbol ?: "$") }

    var showExportJsonDialog by remember { mutableStateOf(false) }
    var exportedJsonText by remember { mutableStateOf("") }

    var showImportJsonDialog by remember { mutableStateOf(false) }
    var importJsonInput by remember { mutableStateOf("") }

    val dateFormat = SimpleDateFormat("dd MMM yyyy • hh:mm:ss a", Locale.getDefault())

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Header
        item {
            Text(
                text = "Cloud Sync & Branches",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = BentoNavy
            )
            Text(
                text = "Manage Google Drive cloud database backup and multi-branch operations",
                style = MaterialTheme.typography.bodySmall,
                color = BentoTextSecondary
            )
        }

        // Google Drive Sync Center Card
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, BentoBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(BentoBlueCard),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CloudDone,
                                    contentDescription = null,
                                    tint = BentoNavy,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Google Drive Database",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = BentoNavy
                                )
                                Text(
                                    text = syncStatus.driveAccount,
                                    fontSize = 12.sp,
                                    color = BentoIndigo,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = when (syncStatus.state) {
                                SyncState.SYNCED -> BentoEmeraldCard
                                SyncState.SYNCING -> BentoBlueCard
                                SyncState.UNSYNCED_CHANGES -> BentoAmberCard
                                else -> Color(0xFFFEE2E2)
                            },
                            border = BorderStroke(
                                1.dp,
                                when (syncStatus.state) {
                                    SyncState.SYNCED -> BentoEmeraldCardText.copy(alpha = 0.3f)
                                    SyncState.SYNCING -> BentoIndigo.copy(alpha = 0.3f)
                                    SyncState.UNSYNCED_CHANGES -> BentoAmberCardText.copy(alpha = 0.3f)
                                    else -> Color(0xFFDC2626).copy(alpha = 0.3f)
                                }
                            )
                        ) {
                            Text(
                                text = when (syncStatus.state) {
                                    SyncState.SYNCED -> "Active & Synced"
                                    SyncState.SYNCING -> "Syncing..."
                                    SyncState.UNSYNCED_CHANGES -> "Local Changes"
                                    else -> "Offline Mode"
                                },
                                color = when (syncStatus.state) {
                                    SyncState.SYNCED -> BentoEmeraldCardText
                                    SyncState.SYNCING -> BentoNavy
                                    SyncState.UNSYNCED_CHANGES -> BentoAmberCardText
                                    else -> Color(0xFFDC2626)
                                },
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            )
                        }
                    }

                    // Cloud File Location
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFF8FAFC),
                        border = BorderStroke(1.dp, BentoBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Folder, contentDescription = null, tint = BentoIndigo, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("DRIVE BACKUP JSON LOCATION", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = BentoTextSecondary)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = syncStatus.driveFilePath,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = BentoNavy
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Last Snapshot: ${dateFormat.format(Date(syncStatus.lastSyncTime))}",
                                fontSize = 11.sp,
                                color = BentoTextSecondary
                            )
                        }
                    }

                    // Offline-first explanation
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Info, contentDescription = null, tint = BentoIndigo, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Snapshots are written directly as valid .json files to your external Google Drive backup path.",
                            fontSize = 11.sp,
                            color = BentoTextSecondary
                        )
                    }

                    // Sync & Restore Action Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = {
                                onTriggerSync()
                                Toast.makeText(context, "Google Drive database snapshot created & synced!", Toast.LENGTH_SHORT).show()
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = BentoNavy),
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .testTag("sync_backup_now_button")
                        ) {
                            if (syncStatus.state == SyncState.SYNCING) {
                                CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White, strokeWidth = 2.dp)
                            } else {
                                Icon(Icons.Default.CloudSync, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.White)
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Sync Drive Now", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }

                        OutlinedButton(
                            onClick = {
                                onTriggerRestore()
                                Toast.makeText(context, "Database restored from latest cloud snapshot!", Toast.LENGTH_SHORT).show()
                            },
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, BentoNavy),
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .testTag("sync_restore_button")
                        ) {
                            Icon(Icons.Default.CloudDownload, contentDescription = null, modifier = Modifier.size(16.dp), tint = BentoNavy)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Restore Backup", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = BentoNavy)
                        }
                    }

                    // Additional Manual JSON Backup Utilities
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                coroutineScope.launch {
                                    if (onExportJson != null) {
                                        exportedJsonText = onExportJson()
                                    } else {
                                        exportedJsonText = "{\"shopName\":\"${shopProfile?.shopName}\",\"branches\":${branches.size},\"units\":${inventoryUnits.size}}"
                                    }
                                    showExportJsonDialog = true
                                }
                            },
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, BentoBorder),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(14.dp), tint = BentoNavy)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Export JSON", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = BentoNavy)
                        }

                        OutlinedButton(
                            onClick = { showImportJsonDialog = true },
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, BentoBorder),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Storage, contentDescription = null, modifier = Modifier.size(14.dp), tint = BentoNavy)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Import JSON", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = BentoNavy)
                        }
                    }
                }
            }
        }

        // Shop Profile Information
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, BentoBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Store, contentDescription = null, tint = BentoNavy)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Shop Information", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = BentoNavy)
                        }

                        IconButton(onClick = { showEditProfileDialog = true }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit Profile", tint = BentoIndigo, modifier = Modifier.size(18.dp))
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Shop Name", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = BentoTextSecondary)
                            Text(shopProfile?.shopName ?: "LOGIN GADGET", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = BentoNavy)
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text("Default Currency", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = BentoTextSecondary)
                            Text(shopProfile?.currencySymbol ?: "$", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = BentoNavy)
                        }
                    }
                }
            }
        }

        // Multi-Branch Management Section
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Shop Branches",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = BentoNavy)
                    )
                    Text(
                        text = "${branches.size} registered branches",
                        style = MaterialTheme.typography.bodySmall,
                        color = BentoTextSecondary
                    )
                }

                Button(
                    onClick = { showAddBranchDialog = true },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BentoNavy),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                    modifier = Modifier.testTag("add_branch_button")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.White)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Branch", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }

        items(branches, key = { it.id }) { branch ->
            val branchStockCount = inventoryUnits.count { it.branchId == branch.id && it.status == "IN_STOCK" }

            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, BentoBorder),
                modifier = Modifier.fillMaxWidth()
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
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(BentoBlueCard),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Business, contentDescription = null, tint = BentoNavy, modifier = Modifier.size(18.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = branch.name, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = BentoNavy)
                                if (branch.isDefault) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = BentoEmeraldCard
                                    ) {
                                        Text("HQ", color = BentoEmeraldCardText, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp))
                                    }
                                }
                            }
                            Text(text = "Location: ${branch.location} • ${branch.phone}", fontSize = 11.sp, color = BentoTextSecondary)
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFFF1F5F9)
                        ) {
                            Text(
                                text = "$branchStockCount items",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = BentoNavy,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }

                        if (branches.size > 1) {
                            IconButton(
                                onClick = { onDeleteBranch(branch.id) },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFEF4444).copy(alpha = 0.7f), modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(20.dp))
        }
    }

    // Dialog for Exporting JSON
    if (showExportJsonDialog) {
        AlertDialog(
            onDismissRequest = { showExportJsonDialog = false },
            title = { Text("Database Backup JSON", fontWeight = FontWeight.Bold, color = BentoNavy) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Here is your full database backup JSON snapshot:", fontSize = 12.sp, color = BentoTextSecondary)
                    OutlinedTextField(
                        value = exportedJsonText,
                        onValueChange = {},
                        readOnly = true,
                        maxLines = 8,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("Database JSON", exportedJsonText)
                        clipboard.setPrimaryClip(clip)
                        Toast.makeText(context, "Copied JSON to clipboard!", Toast.LENGTH_SHORT).show()
                        showExportJsonDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BentoNavy)
                ) {
                    Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Copy JSON")
                }
            },
            dismissButton = {
                TextButton(onClick = { showExportJsonDialog = false }) {
                    Text("Close", color = BentoNavy)
                }
            }
        )
    }

    // Dialog for Importing JSON
    if (showImportJsonDialog) {
        AlertDialog(
            onDismissRequest = { showImportJsonDialog = false },
            title = { Text("Import Database JSON", fontWeight = FontWeight.Bold, color = BentoNavy) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Paste a valid JSON snapshot string to restore all branches, items, and sales records:", fontSize = 12.sp, color = BentoTextSecondary)
                    OutlinedTextField(
                        value = importJsonInput,
                        onValueChange = { importJsonInput = it },
                        placeholder = { Text("Paste JSON snapshot here...") },
                        maxLines = 6,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (importJsonInput.isNotBlank() && onImportJson != null) {
                            onImportJson(importJsonInput) { success, msg ->
                                Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                                if (success) {
                                    showImportJsonDialog = false
                                }
                            }
                        } else {
                            Toast.makeText(context, "Please paste valid JSON text", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BentoNavy)
                ) {
                    Text("Restore from JSON")
                }
            },
            dismissButton = {
                TextButton(onClick = { showImportJsonDialog = false }) {
                    Text("Cancel", color = BentoNavy)
                }
            }
        )
    }

    // Dialog for Adding New Branch
    if (showAddBranchDialog) {
        AlertDialog(
            onDismissRequest = { showAddBranchDialog = false },
            title = { Text("Create New Branch", fontWeight = FontWeight.Bold, color = BentoNavy) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = newBranchName,
                        onValueChange = { newBranchName = it },
                        label = { Text("Branch Name *", fontWeight = FontWeight.SemiBold, color = BentoNavy) },
                        placeholder = { Text("e.g. Airport Plaza Outlet") },
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFFF8FAFC),
                            unfocusedContainerColor = Color(0xFFF8FAFC),
                            focusedBorderColor = BentoIndigo,
                            unfocusedBorderColor = BentoBorder,
                            focusedTextColor = BentoNavy,
                            unfocusedTextColor = BentoNavy
                        )
                    )
                    OutlinedTextField(
                        value = newBranchLocation,
                        onValueChange = { newBranchLocation = it },
                        label = { Text("Location / Address", fontWeight = FontWeight.SemiBold, color = BentoNavy) },
                        placeholder = { Text("e.g. Shop 42, Floor 2") },
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFFF8FAFC),
                            unfocusedContainerColor = Color(0xFFF8FAFC),
                            focusedBorderColor = BentoIndigo,
                            unfocusedBorderColor = BentoBorder,
                            focusedTextColor = BentoNavy,
                            unfocusedTextColor = BentoNavy
                        )
                    )
                    OutlinedTextField(
                        value = newBranchPhone,
                        onValueChange = { newBranchPhone = it },
                        label = { Text("Branch Phone", fontWeight = FontWeight.SemiBold, color = BentoNavy) },
                        placeholder = { Text("+1 555-0192") },
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFFF8FAFC),
                            unfocusedContainerColor = Color(0xFFF8FAFC),
                            focusedBorderColor = BentoIndigo,
                            unfocusedBorderColor = BentoBorder,
                            focusedTextColor = BentoNavy,
                            unfocusedTextColor = BentoNavy
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newBranchName.isNotBlank()) {
                            onCreateBranch(newBranchName, newBranchLocation, newBranchPhone)
                            newBranchName = ""
                            newBranchLocation = ""
                            newBranchPhone = ""
                            showAddBranchDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BentoNavy)
                ) {
                    Text("Add Branch")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddBranchDialog = false }) {
                    Text("Cancel", color = BentoNavy)
                }
            }
        )
    }

    // Dialog for Editing Shop Profile
    if (showEditProfileDialog && shopProfile != null) {
        AlertDialog(
            onDismissRequest = { showEditProfileDialog = false },
            title = { Text("Edit Shop Profile", fontWeight = FontWeight.Bold, color = BentoNavy) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = editShopName,
                        onValueChange = { editShopName = it },
                        label = { Text("Shop Name", fontWeight = FontWeight.SemiBold, color = BentoNavy) },
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFFF8FAFC),
                            unfocusedContainerColor = Color(0xFFF8FAFC),
                            focusedBorderColor = BentoIndigo,
                            unfocusedBorderColor = BentoBorder,
                            focusedTextColor = BentoNavy,
                            unfocusedTextColor = BentoNavy
                        )
                    )
                    OutlinedTextField(
                        value = editCurrency,
                        onValueChange = { editCurrency = it },
                        label = { Text("Currency Symbol (e.g. $, ৳, ₹, €)", fontWeight = FontWeight.SemiBold, color = BentoNavy) },
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFFF8FAFC),
                            unfocusedContainerColor = Color(0xFFF8FAFC),
                            focusedBorderColor = BentoIndigo,
                            unfocusedBorderColor = BentoBorder,
                            focusedTextColor = BentoNavy,
                            unfocusedTextColor = BentoNavy
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onUpdateProfile(
                            shopProfile.copy(
                                shopName = editShopName.trim(),
                                currencySymbol = editCurrency.trim()
                            )
                        )
                        showEditProfileDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BentoNavy)
                ) {
                    Text("Save Changes")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditProfileDialog = false }) {
                    Text("Cancel", color = BentoNavy)
                }
            }
        )
    }
}
