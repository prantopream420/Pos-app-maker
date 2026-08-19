package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.AddUnitDraft
import com.example.data.local.DraftManager
import com.example.data.model.Branch
import com.example.data.model.InventoryUnit
import com.example.ui.theme.BentoAmberCard
import com.example.ui.theme.BentoAmberCardText
import com.example.ui.theme.BentoBlueCard
import com.example.ui.theme.BentoBorder
import com.example.ui.theme.BentoEmeraldCard
import com.example.ui.theme.BentoEmeraldCardText
import com.example.ui.theme.BentoIndigo
import com.example.ui.theme.BentoNavy
import com.example.ui.theme.BentoPurpleCard
import com.example.ui.theme.BentoPurpleCardText
import com.example.ui.theme.BentoTextPrimary
import com.example.ui.theme.BentoTextSecondary
import com.example.ui.theme.EmeraldSecondaryLight
import com.example.ui.theme.IndigoPrimaryLight
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddUnitBottomSheet(
    branches: List<Branch>,
    prefillModelName: String? = null,
    prefillBrand: String? = null,
    prefillCategory: String? = null,
    onDismiss: () -> Unit,
    onSaveUnit: (InventoryUnit) -> Unit
) {
    val context = LocalContext.current
    val draftManager = remember { DraftManager(context) }
    val initialDraft = remember { draftManager.getAddUnitDraft() }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val defaultBranch = branches.firstOrNull()?.id ?: ""
    val hasRestoredDraft = remember { initialDraft.hasActiveDraft && prefillModelName == null }

    var modelName by remember {
        mutableStateOf(prefillModelName ?: if (hasRestoredDraft) initialDraft.modelName else "")
    }
    var brand by remember {
        mutableStateOf(prefillBrand ?: if (hasRestoredDraft) initialDraft.brand else "")
    }
    var category by remember {
        mutableStateOf(prefillCategory ?: if (hasRestoredDraft) initialDraft.category else "Laptops")
    }
    var condition by remember {
        mutableStateOf(if (hasRestoredDraft) initialDraft.condition else "Brand New")
    }
    var serialNumber by remember {
        mutableStateOf(if (hasRestoredDraft) initialDraft.serialNumber else "")
    }
    var cpu by remember {
        mutableStateOf(if (hasRestoredDraft) initialDraft.cpu else if (category == "Laptops") "Intel Core i7" else "")
    }
    var gpu by remember {
        mutableStateOf(if (hasRestoredDraft) initialDraft.gpu else if (category == "Laptops") "Intel Iris Xe" else "")
    }
    var ram by remember {
        mutableStateOf(if (hasRestoredDraft) initialDraft.ram else "16 GB")
    }
    var storage by remember {
        mutableStateOf(if (hasRestoredDraft) initialDraft.storage else "512 GB SSD")
    }
    var color by remember {
        mutableStateOf(if (hasRestoredDraft) initialDraft.color else "Black")
    }
    var purchasePriceStr by remember {
        mutableStateOf(if (hasRestoredDraft) initialDraft.purchasePriceStr else "")
    }
    var suggestedPriceStr by remember {
        mutableStateOf(if (hasRestoredDraft) initialDraft.suggestedPriceStr else "")
    }
    var purchaseSource by remember {
        mutableStateOf(if (hasRestoredDraft) initialDraft.purchaseSource else "Official Distributor")
    }
    var notes by remember {
        mutableStateOf(if (hasRestoredDraft) initialDraft.notes else "")
    }
    var selectedBranchId by remember {
        mutableStateOf(
            if (hasRestoredDraft && initialDraft.selectedBranchId.isNotBlank()) initialDraft.selectedBranchId else defaultBranch
        )
    }

    var showDraftBanner by remember { mutableStateOf(hasRestoredDraft) }

    // Auto-save draft on modification
    LaunchedEffect(modelName, brand, category, condition, serialNumber, cpu, gpu, ram, storage, color, purchasePriceStr, suggestedPriceStr, purchaseSource, notes, selectedBranchId) {
        draftManager.saveAddUnitDraft(
            AddUnitDraft(
                modelName = modelName,
                brand = brand,
                category = category,
                condition = condition,
                serialNumber = serialNumber,
                cpu = cpu,
                gpu = gpu,
                ram = ram,
                storage = storage,
                color = color,
                purchasePriceStr = purchasePriceStr,
                suggestedPriceStr = suggestedPriceStr,
                purchaseSource = purchaseSource,
                notes = notes,
                selectedBranchId = selectedBranchId,
                hasActiveDraft = true
            )
        )
    }

    var branchDropdownExpanded by remember { mutableStateOf(false) }
    var categoryDropdownExpanded by remember { mutableStateOf(false) }

    val categories = listOf("Laptops", "Smartphones", "Tablets", "Desktops", "Audio", "Accessories", "Custom")
    val conditions = listOf("Brand New", "Used", "Open Box", "Special Unit")

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = if (prefillModelName != null) "Add Variant / Unit" else "Add Inventory Stock",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = BentoNavy
                        )
                    )
                    Text(
                        text = if (prefillModelName != null) "New serial & specs variant for $prefillModelName" else "Inputs auto-saved as draft in real time",
                        fontSize = 12.sp,
                        color = BentoTextSecondary
                    )
                }

                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = BentoNavy)
                }
            }

            // Restored Draft Notice Banner
            if (showDraftBanner) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = BentoBlueCard.copy(alpha = 0.5f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BentoIndigo.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Restore, contentDescription = null, tint = BentoIndigo, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Draft restored from previous session",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = BentoNavy
                            )
                        }
                        TextButton(
                            onClick = {
                                draftManager.clearAddUnitDraft()
                                modelName = ""
                                brand = ""
                                serialNumber = ""
                                purchasePriceStr = ""
                                suggestedPriceStr = ""
                                notes = ""
                                showDraftBanner = false
                                Toast.makeText(context, "Draft cleared", Toast.LENGTH_SHORT).show()
                            }
                        ) {
                            Text("Discard", fontSize = 12.sp, color = Color(0xFFDC2626), fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Condition Selector Chips
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Unit Condition *",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = BentoNavy
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    conditions.forEach { cond ->
                        val isSelected = condition == cond
                        FilterChip(
                            selected = isSelected,
                            onClick = { condition = cond },
                            label = { Text(cond, fontSize = 12.sp, fontWeight = FontWeight.Bold) },
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

            // Category & Branch Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Category Dropdown
                ExposedDropdownMenuBox(
                    expanded = categoryDropdownExpanded,
                    onExpandedChange = { categoryDropdownExpanded = !categoryDropdownExpanded },
                    modifier = Modifier.weight(1f)
                ) {
                    OutlinedTextField(
                        value = category,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Category", fontWeight = FontWeight.SemiBold, color = BentoNavy) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryDropdownExpanded) },
                        modifier = Modifier.menuAnchor(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFFF8FAFC),
                            unfocusedContainerColor = Color(0xFFF8FAFC),
                            focusedBorderColor = BentoIndigo,
                            unfocusedBorderColor = BentoBorder,
                            focusedTextColor = BentoNavy,
                            unfocusedTextColor = BentoNavy
                        )
                    )
                    ExposedDropdownMenu(
                        expanded = categoryDropdownExpanded,
                        onDismissRequest = { categoryDropdownExpanded = false }
                    ) {
                        categories.forEach { cat ->
                            DropdownMenuItem(
                                text = { Text(cat, fontWeight = FontWeight.Medium, color = BentoNavy) },
                                onClick = {
                                    category = cat
                                    categoryDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                // Branch Dropdown
                val activeBranchName = branches.find { it.id == selectedBranchId }?.name ?: "Main Branch"
                ExposedDropdownMenuBox(
                    expanded = branchDropdownExpanded,
                    onExpandedChange = { branchDropdownExpanded = !branchDropdownExpanded },
                    modifier = Modifier.weight(1f)
                ) {
                    OutlinedTextField(
                        value = activeBranchName,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Assign Branch", fontWeight = FontWeight.SemiBold, color = BentoNavy) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = branchDropdownExpanded) },
                        modifier = Modifier.menuAnchor(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFFF8FAFC),
                            unfocusedContainerColor = Color(0xFFF8FAFC),
                            focusedBorderColor = BentoIndigo,
                            unfocusedBorderColor = BentoBorder,
                            focusedTextColor = BentoNavy,
                            unfocusedTextColor = BentoNavy
                        )
                    )
                    ExposedDropdownMenu(
                        expanded = branchDropdownExpanded,
                        onDismissRequest = { branchDropdownExpanded = false }
                    ) {
                        branches.forEach { br ->
                            DropdownMenuItem(
                                text = { Text(br.name, fontWeight = FontWeight.Medium, color = BentoNavy) },
                                onClick = {
                                    selectedBranchId = br.id
                                    branchDropdownExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            // Product Name & Brand
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = brand,
                    onValueChange = { brand = it },
                    label = { Text("Brand / Company *", fontWeight = FontWeight.SemiBold, color = BentoNavy) },
                    placeholder = { Text("e.g. Lenovo, Apple") },
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
                    modifier = Modifier
                        .weight(0.45f)
                        .testTag("add_unit_brand_input"),
                    shape = RoundedCornerShape(12.dp),
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
                    value = modelName,
                    onValueChange = { modelName = it },
                    label = { Text("Model Name *", fontWeight = FontWeight.SemiBold, color = BentoNavy) },
                    placeholder = { Text("e.g. ThinkPad X1 Carbon") },
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
                    modifier = Modifier
                        .weight(0.55f)
                        .testTag("add_unit_model_input"),
                    shape = RoundedCornerShape(12.dp),
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

            // Serial Number (Unique)
            OutlinedTextField(
                value = serialNumber,
                onValueChange = { serialNumber = it },
                label = { Text("Serial Number / IMEI / Barcode *", fontWeight = FontWeight.SemiBold, color = BentoNavy) },
                placeholder = { Text("e.g. SN-LEN-88910") },
                trailingIcon = {
                    IconButton(onClick = {
                        val prefix = brand.trim().take(3).ifEmpty { "DEV" }.uppercase()
                        serialNumber = "SN-$prefix-${UUID.randomUUID().toString().take(6).uppercase()}"
                    }) {
                        Icon(Icons.Default.QrCodeScanner, contentDescription = "Auto Gen Serial", tint = BentoIndigo)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("add_unit_serial_input"),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFFF8FAFC),
                    unfocusedContainerColor = Color(0xFFF8FAFC),
                    focusedBorderColor = BentoIndigo,
                    unfocusedBorderColor = BentoBorder,
                    focusedTextColor = BentoNavy,
                    unfocusedTextColor = BentoNavy
                )
            )

            // Specs Row (CPU, GPU, RAM, Storage, Color)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = cpu,
                    onValueChange = { cpu = it },
                    label = { Text("CPU", fontWeight = FontWeight.SemiBold, color = BentoNavy) },
                    placeholder = { Text("e.g. i7-1365U") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
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
                    value = gpu,
                    onValueChange = { gpu = it },
                    label = { Text("GPU", fontWeight = FontWeight.SemiBold, color = BentoNavy) },
                    placeholder = { Text("e.g. RTX 4060") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
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

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = ram,
                    onValueChange = { ram = it },
                    label = { Text("RAM", fontWeight = FontWeight.SemiBold, color = BentoNavy) },
                    placeholder = { Text("16 GB") },
                    modifier = Modifier.weight(0.35f),
                    shape = RoundedCornerShape(12.dp),
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
                    value = storage,
                    onValueChange = { storage = it },
                    label = { Text("Storage", fontWeight = FontWeight.SemiBold, color = BentoNavy) },
                    placeholder = { Text("512 GB SSD") },
                    modifier = Modifier.weight(0.35f),
                    shape = RoundedCornerShape(12.dp),
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
                    value = color,
                    onValueChange = { color = it },
                    label = { Text("Color", fontWeight = FontWeight.SemiBold, color = BentoNavy) },
                    placeholder = { Text("Black") },
                    modifier = Modifier.weight(0.3f),
                    shape = RoundedCornerShape(12.dp),
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

            // Purchase Price & Suggested Retail Price Card
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                border = androidx.compose.foundation.BorderStroke(1.dp, BentoBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Info, contentDescription = null, tint = BentoIndigo, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            "Financial Ledger (Cost kept private from receipts)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = BentoNavy
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = purchasePriceStr,
                            onValueChange = { purchasePriceStr = it },
                            label = { Text("Purchase Cost *", fontWeight = FontWeight.SemiBold, color = BentoNavy) },
                            placeholder = { Text("1250.00") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("add_unit_purchase_cost_input"),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,
                                focusedBorderColor = BentoIndigo,
                                unfocusedBorderColor = BentoBorder,
                                focusedTextColor = BentoNavy,
                                unfocusedTextColor = BentoNavy
                            )
                        )

                        OutlinedTextField(
                            value = suggestedPriceStr,
                            onValueChange = { suggestedPriceStr = it },
                            label = { Text("Suggested Retail Price", fontWeight = FontWeight.SemiBold, color = BentoNavy) },
                            placeholder = { Text("1480.00") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,
                                focusedBorderColor = BentoIndigo,
                                unfocusedBorderColor = BentoBorder,
                                focusedTextColor = BentoNavy,
                                unfocusedTextColor = BentoNavy
                            )
                        )
                    }

                    OutlinedTextField(
                        value = purchaseSource,
                        onValueChange = { purchaseSource = it },
                        label = { Text("Purchase Source / Vendor", fontWeight = FontWeight.SemiBold, color = BentoNavy) },
                        placeholder = { Text("e.g. Official Importer, Trade-in") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedBorderColor = BentoIndigo,
                            unfocusedBorderColor = BentoBorder,
                            focusedTextColor = BentoNavy,
                            unfocusedTextColor = BentoNavy
                        )
                    )
                }
            }

            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Unit Notes (e.g. battery health, seal state)", fontWeight = FontWeight.SemiBold, color = BentoNavy) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFFF8FAFC),
                    unfocusedContainerColor = Color(0xFFF8FAFC),
                    focusedBorderColor = BentoIndigo,
                    unfocusedBorderColor = BentoBorder,
                    focusedTextColor = BentoNavy,
                    unfocusedTextColor = BentoNavy
                )
            )

            // Submit Button
            Button(
                onClick = {
                    val pCost = purchasePriceStr.toDoubleOrNull() ?: 0.0
                    val sPrice = suggestedPriceStr.toDoubleOrNull() ?: (pCost * 1.2)
                    if (modelName.isNotBlank() && brand.isNotBlank() && serialNumber.isNotBlank()) {
                        val newUnit = InventoryUnit(
                            modelName = modelName.trim(),
                            brand = brand.trim(),
                            category = category,
                            serialNumber = serialNumber.trim(),
                            branchId = selectedBranchId,
                            condition = condition,
                            cpu = cpu.trim().ifEmpty { "N/A" },
                            gpu = gpu.trim().ifEmpty { "N/A" },
                            ram = ram.trim().ifEmpty { "N/A" },
                            storage = storage.trim().ifEmpty { "N/A" },
                            color = color.trim().ifEmpty { "Standard" },
                            purchasePrice = pCost,
                            suggestedSellingPrice = sPrice,
                            purchaseDate = System.currentTimeMillis(),
                            purchaseSource = purchaseSource.trim(),
                            status = "IN_STOCK",
                            notes = notes.trim()
                        )
                        draftManager.clearAddUnitDraft()
                        onSaveUnit(newUnit)
                    } else {
                        Toast.makeText(context, "Please fill in brand, model name, and serial number", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("add_unit_confirm_button"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BentoNavy)
            ) {
                Text(
                    text = "Save to Inventory",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

