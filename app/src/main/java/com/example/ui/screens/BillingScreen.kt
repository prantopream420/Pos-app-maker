package com.example.ui.screens

import android.widget.Toast
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PointOfSale
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.BillingDraft
import com.example.data.local.DraftManager
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
import com.example.ui.theme.BentoTextPrimary
import com.example.ui.theme.BentoTextSecondary
import com.example.ui.theme.EmeraldSecondaryLight
import com.example.ui.theme.IndigoPrimaryLight
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BillingScreen(
    inStockUnits: List<InventoryUnit>,
    branches: List<Branch>,
    selectedUnit: InventoryUnit?,
    errorMessage: String?,
    currencySymbol: String,
    onSelectUnit: (InventoryUnit?) -> Unit,
    onProcessSale: (
        customerName: String,
        customerPhone: String,
        customerAddress: String,
        sellingPrice: Double,
        paymentType: String,
        bankingAppName: String,
        serviceWarrantyYears: Double,
        replacementWarrantyDays: Int,
        warrantyTerms: String
    ) -> Unit
) {
    val context = LocalContext.current
    val draftManager = remember { DraftManager(context) }
    val initialDraft = remember { draftManager.getBillingDraft() }

    val hasRestoredDraft = remember { initialDraft.hasActiveDraft }
    var showDraftBanner by remember { mutableStateOf(hasRestoredDraft) }

    var showProductSearchSheet by remember { mutableStateOf(false) }
    var productSearchQuery by remember { mutableStateOf("") }

    var customerName by remember {
        mutableStateOf(if (hasRestoredDraft) initialDraft.customerName else "")
    }
    var customerPhone by remember {
        mutableStateOf(if (hasRestoredDraft) initialDraft.customerPhone else "")
    }
    var customerAddress by remember {
        mutableStateOf(if (hasRestoredDraft) initialDraft.customerAddress else "")
    }

    var sellingPriceStr by remember {
        mutableStateOf(
            if (hasRestoredDraft && initialDraft.sellingPriceStr.isNotBlank()) initialDraft.sellingPriceStr
            else if (selectedUnit != null && selectedUnit.suggestedSellingPrice > 0)
                selectedUnit.suggestedSellingPrice.toString()
            else ""
        )
    }

    var paymentType by remember {
        mutableStateOf(if (hasRestoredDraft && initialDraft.paymentType.isNotBlank()) initialDraft.paymentType else "OFFLINE")
    }
    var bankingAppName by remember {
        mutableStateOf(if (hasRestoredDraft && initialDraft.bankingAppName.isNotBlank()) initialDraft.bankingAppName else "bKash / Google Pay / Card")
    }

    var serviceWarrantyYears by remember {
        mutableDoubleStateOf(if (hasRestoredDraft && initialDraft.serviceWarrantyYears > 0) initialDraft.serviceWarrantyYears else 2.0)
    }
    var replacementWarrantyDays by remember {
        mutableIntStateOf(if (hasRestoredDraft && initialDraft.replacementWarrantyDays > 0) initialDraft.replacementWarrantyDays else 15)
    }
    var warrantyTerms by remember {
        mutableStateOf(
            if (hasRestoredDraft && initialDraft.warrantyTerms.isNotBlank()) initialDraft.warrantyTerms
            else "Standard shop hardware service warranty. Covers internal components. Physical and liquid damage void warranty."
        )
    }

    // Auto-restore selected unit from draft if available and not yet set
    LaunchedEffect(inStockUnits) {
        if (selectedUnit == null && !initialDraft.selectedUnitId.isNullOrBlank()) {
            val matching = inStockUnits.find { it.id == initialDraft.selectedUnitId }
            if (matching != null) {
                onSelectUnit(matching)
            }
        }
    }

    // When unit selection changes, update selling price if blank
    LaunchedEffect(selectedUnit) {
        if (selectedUnit != null && sellingPriceStr.isBlank()) {
            sellingPriceStr = if (selectedUnit.suggestedSellingPrice > 0)
                selectedUnit.suggestedSellingPrice.toString()
            else (selectedUnit.purchasePrice * 1.2).toString()
        }
    }

    // Auto-save billing draft on modification
    LaunchedEffect(selectedUnit, customerName, customerPhone, customerAddress, sellingPriceStr, paymentType, bankingAppName, serviceWarrantyYears, replacementWarrantyDays, warrantyTerms) {
        draftManager.saveBillingDraft(
            BillingDraft(
                selectedUnitId = selectedUnit?.id ?: "",
                customerName = customerName,
                customerPhone = customerPhone,
                customerAddress = customerAddress,
                sellingPriceStr = sellingPriceStr,
                paymentType = paymentType,
                bankingAppName = bankingAppName,
                serviceWarrantyYears = serviceWarrantyYears,
                replacementWarrantyDays = replacementWarrantyDays,
                warrantyTerms = warrantyTerms,
                hasActiveDraft = true
            )
        )
    }

    val numFormat = NumberFormat.getNumberInstance(Locale.US).apply {
        maximumFractionDigits = 2
        minimumFractionDigits = 0
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Spacer(modifier = Modifier.height(4.dp))

        // Title Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Point of Sale (POS) Billing",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    color = BentoNavy
                )
                Text(
                    text = "Generate retail invoice with custom warranty & automatic inventory deduction",
                    style = MaterialTheme.typography.bodySmall,
                    color = BentoTextSecondary
                )
            }

            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(BentoBlueCard),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PointOfSale,
                    contentDescription = null,
                    tint = BentoNavy,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        // Restored Draft Notice Banner
        if (showDraftBanner) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = BentoBlueCard.copy(alpha = 0.5f),
                border = BorderStroke(1.dp, BentoIndigo.copy(alpha = 0.3f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
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
                            draftManager.clearBillingDraft()
                            customerName = ""
                            customerPhone = ""
                            customerAddress = ""
                            sellingPriceStr = ""
                            onSelectUnit(null)
                            showDraftBanner = false
                            Toast.makeText(context, "Billing draft cleared", Toast.LENGTH_SHORT).show()
                        }
                    ) {
                        Text("Discard", fontSize = 12.sp, color = Color(0xFFDC2626), fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Error message banner if any
        if (errorMessage != null) {
            Surface(
                color = Color(0xFFEF4444).copy(alpha = 0.12f),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, Color(0xFFDC2626).copy(alpha = 0.3f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Info, contentDescription = null, tint = Color(0xFFDC2626))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = errorMessage, color = Color(0xFFDC2626), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Step 1: Select Item From Inventory
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
                    Text(
                        text = "1. Selected Device / Product",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = BentoNavy)
                    )

                    Button(
                        onClick = { showProductSearchSheet = true },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BentoNavy),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                        modifier = Modifier.testTag("pos_search_product_button")
                    ) {
                        Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.White)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (selectedUnit == null) "Select from Stock" else "Change Item", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }

                if (selectedUnit == null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, BentoBorder, RoundedCornerShape(14.dp))
                            .background(Color(0xFFF8FAFC), RoundedCornerShape(14.dp))
                            .clickable { showProductSearchSheet = true }
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.QrCodeScanner,
                                contentDescription = null,
                                tint = BentoIndigo,
                                modifier = Modifier.size(38.dp)
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text("Tap here to search product name or serial number", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = BentoNavy)
                            Text("Search through ${inStockUnits.size} available in-stock units", fontSize = 12.sp, color = BentoTextSecondary)
                        }
                    }
                } else {
                    // Selected product specs display (auto-populated, confidential purchase price excluded)
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = Color(0xFFF8FAFC),
                        border = BorderStroke(1.dp, BentoBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "${selectedUnit.brand} ${selectedUnit.modelName}",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        color = BentoNavy
                                    )
                                    Text(
                                        text = "${selectedUnit.category} • Color: ${selectedUnit.color}",
                                        fontSize = 12.sp,
                                        color = BentoTextSecondary
                                    )
                                }
                                ConditionBadge(condition = selectedUnit.condition)
                            }

                            HorizontalDivider(color = BentoBorder)

                            // Serial Number
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("Serial No: ", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = BentoTextSecondary)
                                Text(
                                    text = selectedUnit.serialNumber,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = BentoIndigo
                                )
                            }

                            // Device Specs breakdown
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                if (selectedUnit.cpu.isNotBlank() && selectedUnit.cpu != "N/A") {
                                    Surface(shape = RoundedCornerShape(8.dp), color = Color.White, border = BorderStroke(1.dp, BentoBorder), modifier = Modifier.weight(1f)) {
                                        Column(modifier = Modifier.padding(8.dp)) {
                                            Text("CPU", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = BentoTextSecondary)
                                            Text(selectedUnit.cpu, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = BentoNavy, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                        }
                                    }
                                }
                                Surface(shape = RoundedCornerShape(8.dp), color = Color.White, border = BorderStroke(1.dp, BentoBorder), modifier = Modifier.weight(0.7f)) {
                                    Column(modifier = Modifier.padding(8.dp)) {
                                        Text("RAM", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = BentoTextSecondary)
                                        Text(selectedUnit.ram, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = BentoNavy)
                                    }
                                }
                                Surface(shape = RoundedCornerShape(8.dp), color = Color.White, border = BorderStroke(1.dp, BentoBorder), modifier = Modifier.weight(0.9f)) {
                                    Column(modifier = Modifier.padding(8.dp)) {
                                        Text("STORAGE", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = BentoTextSecondary)
                                        Text(selectedUnit.storage, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = BentoNavy)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Step 2: Customer Details
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
                Text(
                    text = "2. Customer Information",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = BentoNavy)
                )

                OutlinedTextField(
                    value = customerName,
                    onValueChange = { customerName = it },
                    label = { Text("Customer Full Name *", fontWeight = FontWeight.SemiBold, color = BentoNavy) },
                    placeholder = { Text("e.g. John Doe / Alex Smith") },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = BentoNavy) },
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("pos_customer_name_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFFF8FAFC),
                        unfocusedContainerColor = Color(0xFFF8FAFC),
                        focusedBorderColor = BentoIndigo,
                        unfocusedBorderColor = BentoBorder,
                        focusedTextColor = BentoNavy,
                        unfocusedTextColor = BentoNavy
                    )
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = customerPhone,
                        onValueChange = { customerPhone = it },
                        label = { Text("Phone Number *", fontWeight = FontWeight.SemiBold, color = BentoNavy) },
                        placeholder = { Text("+1 (555) 019-2834") },
                        leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = BentoNavy) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("pos_customer_phone_input"),
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

                OutlinedTextField(
                    value = customerAddress,
                    onValueChange = { customerAddress = it },
                    label = { Text("Customer Address / City", fontWeight = FontWeight.SemiBold, color = BentoNavy) },
                    placeholder = { Text("e.g. 742 Evergreen Terrace, Springfield") },
                    leadingIcon = { Icon(Icons.Default.Home, contentDescription = null, tint = BentoNavy) },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
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
        }

        // Step 3: Selling Price & Payment Method
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
                Text(
                    text = "3. Selling Price & Payment Method",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = BentoNavy)
                )

                OutlinedTextField(
                    value = sellingPriceStr,
                    onValueChange = { sellingPriceStr = it },
                    label = { Text("Final Selling Price ($currencySymbol) *", fontWeight = FontWeight.SemiBold, color = BentoNavy) },
                    placeholder = { Text("1480.00") },
                    leadingIcon = { Icon(Icons.Default.AttachMoney, contentDescription = null, tint = BentoNavy) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("pos_selling_price_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFFF8FAFC),
                        unfocusedContainerColor = Color(0xFFF8FAFC),
                        focusedBorderColor = BentoIndigo,
                        unfocusedBorderColor = BentoBorder,
                        focusedTextColor = BentoNavy,
                        unfocusedTextColor = BentoNavy
                    )
                )

                // Owner Profit Indicator (Internal helper)
                val sPrice = sellingPriceStr.toDoubleOrNull() ?: 0.0
                if (selectedUnit != null && sPrice > 0) {
                    val margin = sPrice - selectedUnit.purchasePrice
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (margin >= 0) BentoEmeraldCard else Color(0xFFFEE2E2),
                        border = BorderStroke(1.dp, if (margin >= 0) BentoEmeraldCardText.copy(alpha = 0.3f) else Color(0xFFDC2626).copy(alpha = 0.3f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "Shop Profit Margin:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = BentoNavy)
                            Text(
                                text = "${if (margin >= 0) "+" else ""}$currencySymbol ${numFormat.format(margin)}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = if (margin >= 0) BentoEmeraldCardText else Color(0xFFDC2626)
                            )
                        }
                    }
                }

                // Payment Method Selector
                Text(
                    text = "Payment Method *",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = BentoNavy
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    FilterChip(
                        selected = paymentType == "OFFLINE",
                        onClick = { paymentType = "OFFLINE" },
                        label = { Text("Offline (Cash)", fontWeight = FontWeight.Bold, fontSize = 13.sp) },
                        leadingIcon = { Icon(Icons.Default.Payments, contentDescription = null, modifier = Modifier.size(16.dp)) },
                        shape = RoundedCornerShape(12.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = BentoNavy,
                            selectedLabelColor = Color.White,
                            selectedLeadingIconColor = Color.White,
                            containerColor = Color(0xFFF1F5F9),
                            labelColor = BentoNavy
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("pos_payment_offline")
                    )

                    FilterChip(
                        selected = paymentType == "ONLINE",
                        onClick = { paymentType = "ONLINE" },
                        label = { Text("Online / App", fontWeight = FontWeight.Bold, fontSize = 13.sp) },
                        leadingIcon = { Icon(Icons.Default.AccountBalance, contentDescription = null, modifier = Modifier.size(16.dp)) },
                        shape = RoundedCornerShape(12.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = BentoNavy,
                            selectedLabelColor = Color.White,
                            selectedLeadingIconColor = Color.White,
                            containerColor = Color(0xFFF1F5F9),
                            labelColor = BentoNavy
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("pos_payment_online")
                    )
                }

                if (paymentType == "ONLINE") {
                    OutlinedTextField(
                        value = bankingAppName,
                        onValueChange = { bankingAppName = it },
                        label = { Text("Banking App / Platform Name *", fontWeight = FontWeight.SemiBold, color = BentoNavy) },
                        placeholder = { Text("e.g. bKash, Google Pay, Chase Wire, Nagad, Card") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("pos_banking_app_name_input"),
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
            }
        }

        // Step 4: Customizable Warranty Policy
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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Security, contentDescription = null, tint = BentoIndigo, modifier = Modifier.size(22.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "4. Warranty & Guarantee Policy",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = BentoNavy)
                    )
                }

                // Service Warranty Stepper (Years)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Service Warranty", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = BentoNavy)
                        Text("Full hardware repair support", fontSize = 12.sp, color = BentoTextSecondary)
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        IconButton(
                            onClick = { if (serviceWarrantyYears > 0.5) serviceWarrantyYears -= 0.5 },
                            modifier = Modifier
                                .size(36.dp)
                                .background(Color(0xFFF1F5F9), CircleShape)
                                .border(1.dp, BentoBorder, CircleShape)
                        ) {
                            Icon(Icons.Default.Remove, contentDescription = "Decrease", tint = BentoNavy, modifier = Modifier.size(18.dp))
                        }

                        Text(
                            text = if (serviceWarrantyYears == 1.0) "1 Year" else "$serviceWarrantyYears Years",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = BentoNavy,
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )

                        IconButton(
                            onClick = { serviceWarrantyYears += 0.5 },
                            modifier = Modifier
                                .size(36.dp)
                                .background(Color(0xFFF1F5F9), CircleShape)
                                .border(1.dp, BentoBorder, CircleShape)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Increase", tint = BentoNavy, modifier = Modifier.size(18.dp))
                        }
                    }
                }

                HorizontalDivider(color = BentoBorder)

                // Replacement Warranty Stepper (Days)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Replacement Warranty", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = BentoNavy)
                        Text("Direct unit exchange period", fontSize = 12.sp, color = BentoTextSecondary)
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        IconButton(
                            onClick = { if (replacementWarrantyDays > 0) replacementWarrantyDays -= 5 },
                            modifier = Modifier
                                .size(36.dp)
                                .background(Color(0xFFF1F5F9), CircleShape)
                                .border(1.dp, BentoBorder, CircleShape)
                        ) {
                            Icon(Icons.Default.Remove, contentDescription = "Decrease", tint = BentoNavy, modifier = Modifier.size(18.dp))
                        }

                        Text(
                            text = "$replacementWarrantyDays Days",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = BentoNavy,
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )

                        IconButton(
                            onClick = { replacementWarrantyDays += 5 },
                            modifier = Modifier
                                .size(36.dp)
                                .background(Color(0xFFF1F5F9), CircleShape)
                                .border(1.dp, BentoBorder, CircleShape)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Increase", tint = BentoNavy, modifier = Modifier.size(18.dp))
                        }
                    }
                }

                OutlinedTextField(
                    value = warrantyTerms,
                    onValueChange = { warrantyTerms = it },
                    label = { Text("Terms & Conditions on Invoice", fontWeight = FontWeight.SemiBold, color = BentoNavy) },
                    maxLines = 3,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
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
        }

        // Action Button: Complete Sale & Generate Bill
        Button(
            onClick = {
                val finalPrice = sellingPriceStr.toDoubleOrNull() ?: 0.0
                onProcessSale(
                    customerName,
                    customerPhone,
                    customerAddress,
                    finalPrice,
                    paymentType,
                    bankingAppName,
                    serviceWarrantyYears,
                    replacementWarrantyDays,
                    warrantyTerms
                )
            },
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = BentoNavy),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .testTag("pos_complete_sale_button")
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Complete Sale & Generate Bill",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(100.dp))
    }

    // Modal Sheet for Product Selection with Live Search
    if (showProductSearchSheet) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

        val filteredStock = inStockUnits.filter {
            if (productSearchQuery.isBlank()) true
            else {
                val q = productSearchQuery.trim().lowercase()
                it.modelName.lowercase().contains(q) ||
                it.brand.lowercase().contains(q) ||
                it.serialNumber.lowercase().contains(q)
            }
        }

        ModalBottomSheet(
            onDismissRequest = { showProductSearchSheet = false },
            sheetState = sheetState,
            containerColor = Color.White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Select Product to Sell",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = BentoNavy)
                    )
                    IconButton(onClick = { showProductSearchSheet = false }) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = BentoNavy)
                    }
                }

                OutlinedTextField(
                    value = productSearchQuery,
                    onValueChange = { productSearchQuery = it },
                    placeholder = { Text("Search by name, brand, or serial...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = BentoNavy) },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("pos_modal_search_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFFF8FAFC),
                        unfocusedContainerColor = Color(0xFFF8FAFC),
                        focusedBorderColor = BentoIndigo,
                        unfocusedBorderColor = BentoBorder,
                        focusedTextColor = BentoNavy,
                        unfocusedTextColor = BentoNavy
                    )
                )

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(380.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (filteredStock.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("No available units found", color = BentoTextSecondary)
                            }
                        }
                    } else {
                        items(filteredStock, key = { it.id }) { item ->
                            val branchName = branches.find { it.id == item.branchId }?.name ?: "Main Branch"
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = Color(0xFFF8FAFC),
                                border = BorderStroke(1.dp, BentoBorder),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        onSelectUnit(item)
                                        sellingPriceStr = if (item.suggestedSellingPrice > 0) item.suggestedSellingPrice.toString() else (item.purchasePrice * 1.2).toString()
                                        showProductSearchSheet = false
                                    }
                                    .testTag("select_stock_item_${item.serialNumber}")
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = "${item.brand} ${item.modelName}",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 15.sp,
                                                color = BentoNavy
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            ConditionBadge(condition = item.condition)
                                        }
                                        Text(
                                            text = "S/N: ${item.serialNumber} • $branchName",
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = BentoIndigo
                                        )
                                        Text(
                                            text = "${item.cpu} • ${item.ram} • ${item.storage}",
                                            fontSize = 12.sp,
                                            color = BentoTextSecondary
                                        )
                                    }

                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(
                                            text = "$currencySymbol ${numFormat.format(item.suggestedSellingPrice)}",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            color = BentoNavy
                                        )
                                        Text("In Stock", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = BentoEmeraldCardText)
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
