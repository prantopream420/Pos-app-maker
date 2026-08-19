package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SaleRecord
import com.example.ui.components.ConditionBadge
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun SalesHistoryScreen(
    salesRecords: List<SaleRecord>,
    currencySymbol: String,
    onSelectInvoice: (SaleRecord) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var filterPaymentType by remember { mutableStateOf("ALL") } // "ALL", "ONLINE", "OFFLINE"

    val numFormat = NumberFormat.getNumberInstance(Locale.US).apply {
        maximumFractionDigits = 2
        minimumFractionDigits = 0
    }
    val dateFormat = SimpleDateFormat("dd MMM yyyy • hh:mm a", Locale.getDefault())

    val filteredList = salesRecords.filter { record ->
        val matchesQuery = if (searchQuery.isBlank()) true else {
            val q = searchQuery.trim().lowercase()
            record.customerName.lowercase().contains(q) ||
            record.customerPhone.lowercase().contains(q) ||
            record.invoiceNumber.lowercase().contains(q) ||
            record.modelName.lowercase().contains(q) ||
            record.serialNumber.lowercase().contains(q)
        }

        val matchesPayment = when (filterPaymentType) {
            "ONLINE" -> record.paymentType == "ONLINE"
            "OFFLINE" -> record.paymentType == "OFFLINE"
            else -> true
        }

        matchesQuery && matchesPayment
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 14.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "Sales Ledger & Invoices",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    color = BentoNavy
                )
                Text(
                    text = "${salesRecords.size} total sales recorded",
                    style = MaterialTheme.typography.bodySmall,
                    color = BentoTextSecondary
                )
            }
        }

        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search customer name, phone, invoice #, serial...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = BentoNavy) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear", tint = BentoNavy)
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFFF8FAFC),
                    unfocusedContainerColor = Color(0xFFF8FAFC),
                    focusedBorderColor = BentoIndigo,
                    unfocusedBorderColor = BentoBorder,
                    focusedTextColor = BentoNavy,
                    unfocusedTextColor = BentoNavy
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("sales_history_search_input")
            )
        }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = filterPaymentType == "ALL",
                    onClick = { filterPaymentType = "ALL" },
                    label = { Text("All Payments", fontSize = 12.sp, fontWeight = if (filterPaymentType == "ALL") FontWeight.Bold else FontWeight.Normal) },
                    shape = RoundedCornerShape(10.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = BentoNavy,
                        selectedLabelColor = Color.White,
                        containerColor = Color(0xFFF1F5F9),
                        labelColor = BentoNavy
                    )
                )
                FilterChip(
                    selected = filterPaymentType == "ONLINE",
                    onClick = { filterPaymentType = "ONLINE" },
                    label = { Text("Online / App", fontSize = 12.sp, fontWeight = if (filterPaymentType == "ONLINE") FontWeight.Bold else FontWeight.Normal) },
                    shape = RoundedCornerShape(10.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = BentoNavy,
                        selectedLabelColor = Color.White,
                        containerColor = Color(0xFFF1F5F9),
                        labelColor = BentoNavy
                    )
                )
                FilterChip(
                    selected = filterPaymentType == "OFFLINE",
                    onClick = { filterPaymentType = "OFFLINE" },
                    label = { Text("Cash Offline", fontSize = 12.sp, fontWeight = if (filterPaymentType == "OFFLINE") FontWeight.Bold else FontWeight.Normal) },
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

        if (filteredList.isEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, BentoBorder)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ReceiptLong,
                            contentDescription = null,
                            tint = BentoIndigo,
                            modifier = Modifier.size(48.dp)
                        )
                        Text(
                            text = "No sales records found",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium,
                            color = BentoNavy
                        )
                        Text(
                            text = "Sales transactions created in the POS Billing section will be permanently recorded here.",
                            fontSize = 12.sp,
                            color = BentoTextSecondary
                        )
                    }
                }
            }
        } else {
            items(filteredList, key = { it.id }) { sale ->
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, BentoBorder),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelectInvoice(sale) }
                        .testTag("sale_invoice_item_${sale.invoiceNumber}")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = sale.invoiceNumber,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = BentoIndigo
                                )
                                ConditionBadge(condition = sale.condition)
                            }

                            Text(
                                text = "${sale.brand} ${sale.modelName}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = BentoNavy
                            )

                            Text(
                                text = "Customer: ${sale.customerName} (${sale.customerPhone})",
                                fontSize = 12.sp,
                                color = BentoNavy
                            )

                            Text(
                                text = "S/N: ${sale.serialNumber} • ${sale.branchName}",
                                fontSize = 11.sp,
                                color = BentoTextSecondary
                            )

                            Text(
                                text = dateFormat.format(Date(sale.saleDate)),
                                fontSize = 11.sp,
                                color = BentoTextSecondary
                            )
                        }

                        Column(
                            horizontalAlignment = Alignment.End,
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "$currencySymbol ${numFormat.format(sale.sellingPrice)}",
                                fontWeight = FontWeight.Black,
                                fontSize = 16.sp,
                                color = BentoNavy
                            )

                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = BentoEmeraldCard,
                                border = BorderStroke(1.dp, BentoEmeraldCardText.copy(alpha = 0.3f))
                            ) {
                                Text(
                                    text = "+$currencySymbol ${numFormat.format(sale.profit)} profit",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = BentoEmeraldCardText,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color(0xFFF1F5F9)
                            ) {
                                Text(
                                    text = if (sale.paymentType == "ONLINE") sale.bankingAppName.ifEmpty { "Online" } else "Cash",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = BentoNavy,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
