package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.SaleRecord
import com.example.data.model.ShopProfile
import com.example.ui.components.ConditionBadge
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
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun InvoiceViewScreen(
    invoice: SaleRecord,
    shopProfile: ShopProfile?,
    onBackClick: () -> Unit,
    onNewSaleClick: () -> Unit
) {
    val context = LocalContext.current
    val currency = shopProfile?.currencySymbol ?: "$"
    val numFormat = NumberFormat.getNumberInstance(Locale.US).apply {
        maximumFractionDigits = 2
        minimumFractionDigits = 0
    }
    val dateFormat = SimpleDateFormat("EEEE, dd MMMM yyyy • hh:mm a", Locale.getDefault())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Spacer(modifier = Modifier.height(4.dp))

        // Navigation Top Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier.testTag("invoice_back_button")
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = BentoNavy)
            }

            Text(
                text = "Customer Invoice Receipt",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = BentoNavy
            )

            Row {
                IconButton(
                    onClick = {
                        shareInvoiceText(context, invoice, shopProfile?.shopName ?: "LOGIN GADGET", currency)
                    },
                    modifier = Modifier.testTag("invoice_share_button")
                ) {
                    Icon(Icons.Default.Share, contentDescription = "Share", tint = BentoNavy)
                }
                IconButton(
                    onClick = {
                        Toast.makeText(context, "Sending receipt to thermal/system printer...", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.testTag("invoice_print_button")
                ) {
                    Icon(Icons.Default.Print, contentDescription = "Print", tint = BentoNavy)
                }
            }
        }

        // Printable Digital Invoice Sheet
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, BentoBorder),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("printable_invoice_card")
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Shop Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = shopProfile?.shopName ?: "LOGIN GADGET",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold),
                            color = BentoNavy
                        )
                        Text(
                            text = invoice.branchName,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = BentoIndigo
                        )
                        Text(
                            text = "Email: ${shopProfile?.ownerEmail ?: "support@logingadget.com"}",
                            fontSize = 11.sp,
                            color = BentoTextSecondary
                        )
                    }

                    Image(
                        painter = painterResource(id = R.drawable.ic_app_logo),
                        contentDescription = "Shop Logo",
                        modifier = Modifier.size(48.dp)
                    )
                }

                HorizontalDivider(color = BentoBorder)

                // Invoice Number & Date Badge
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("INVOICE NUMBER", fontSize = 10.sp, color = BentoTextSecondary, fontWeight = FontWeight.Bold)
                        Text(
                            text = invoice.invoiceNumber,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = BentoNavy
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text("DATE & TIME", fontSize = 10.sp, color = BentoTextSecondary, fontWeight = FontWeight.Bold)
                        Text(
                            text = dateFormat.format(Date(invoice.saleDate)),
                            fontSize = 11.sp,
                            color = BentoNavy
                        )
                    }
                }

                // Customer Details Section
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFF8FAFC),
                    border = BorderStroke(1.dp, BentoBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text("BILLED TO CUSTOMER", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = BentoTextSecondary)
                        Text(
                            text = invoice.customerName,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = BentoNavy
                        )
                        Text(
                            text = "Phone: ${invoice.customerPhone}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = BentoNavy
                        )
                        if (invoice.customerAddress.isNotBlank()) {
                            Text(
                                text = "Address: ${invoice.customerAddress}",
                                fontSize = 11.sp,
                                color = BentoTextSecondary
                            )
                        }
                    }
                }

                // Item Details Table
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFF8FAFC),
                    border = BorderStroke(1.dp, BentoBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "${invoice.brand} ${invoice.modelName}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = BentoNavy
                                )
                                Text(
                                    text = "Category: ${invoice.category} • Color: ${invoice.color}",
                                    fontSize = 11.sp,
                                    color = BentoTextSecondary
                                )
                            }
                            ConditionBadge(condition = invoice.condition)
                        }

                        // Serial Number (Essential for warranty)
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = BentoBlueCard.copy(alpha = 0.6f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "SERIAL / IMEI: ", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = BentoNavy)
                                Text(
                                    text = invoice.serialNumber,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 12.sp,
                                    color = BentoNavy
                                )
                            }
                        }

                        // Specs summary
                        Text(
                            text = "Specs: CPU ${invoice.cpu} • GPU ${invoice.gpu} • RAM ${invoice.ram} • Storage ${invoice.storage}",
                            fontSize = 11.sp,
                            color = BentoTextSecondary
                        )
                    }
                }

                // Payment & Total
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("PAYMENT METHOD", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = BentoTextSecondary)
                        Text(
                            text = if (invoice.paymentType == "ONLINE") "Online Transfer (${invoice.bankingAppName})" else "Cash on Counter",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp,
                            color = BentoNavy
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text("TOTAL AMOUNT PAID", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = BentoTextSecondary)
                        Text(
                            text = "$currency ${numFormat.format(invoice.sellingPrice)}",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black,
                            color = BentoNavy
                        )
                    }
                }

                HorizontalDivider(color = BentoBorder)

                // Warranty Guarantee Certificate Block
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = BentoAmberCard.copy(alpha = 0.5f),
                    border = BorderStroke(1.dp, BentoAmberCardText.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Verified, contentDescription = null, tint = BentoAmberCardText, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "WARRANTY & REPLACEMENT POLICY",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = BentoAmberCardText
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text(
                                text = "• Service Warranty: ${if (invoice.serviceWarrantyYears == 1.0) "1 Year" else "${invoice.serviceWarrantyYears} Years"}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = BentoNavy
                            )
                            Text(
                                text = "• Replacement: ${invoice.replacementWarrantyDays} Days",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = BentoNavy
                            )
                        }

                        Text(
                            text = invoice.warrantyTerms,
                            fontSize = 11.sp,
                            color = BentoNavy
                        )
                    }
                }

                Text(
                    text = "Thank you for your business! Please keep this invoice for warranty validation.",
                    fontSize = 11.sp,
                    color = BentoTextSecondary,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        }

        // Action Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedButton(
                onClick = onBackClick,
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, BentoNavy)
            ) {
                Text("All Sales History", fontWeight = FontWeight.Bold, color = BentoNavy)
            }

            Button(
                onClick = onNewSaleClick,
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp)
                    .testTag("invoice_new_sale_button"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BentoNavy)
            ) {
                Text("New Sale (POS)", fontWeight = FontWeight.Bold, color = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(30.dp))
    }
}

private fun shareInvoiceText(context: Context, invoice: SaleRecord, shopName: String, currency: String) {
    val text = buildString {
        appendLine("===============================")
        appendLine("       $shopName INVOICE       ")
        appendLine("===============================")
        appendLine("Invoice No: ${invoice.invoiceNumber}")
        appendLine("Branch: ${invoice.branchName}")
        appendLine("Date: ${SimpleDateFormat("dd-MMM-yyyy hh:mm a", Locale.getDefault()).format(Date(invoice.saleDate))}")
        appendLine("-------------------------------")
        appendLine("Customer: ${invoice.customerName}")
        appendLine("Phone: ${invoice.customerPhone}")
        if (invoice.customerAddress.isNotBlank()) appendLine("Address: ${invoice.customerAddress}")
        appendLine("-------------------------------")
        appendLine("Item: ${invoice.brand} ${invoice.modelName} (${invoice.condition})")
        appendLine("Serial No: ${invoice.serialNumber}")
        appendLine("Specs: ${invoice.cpu}, ${invoice.ram}, ${invoice.storage}")
        appendLine("-------------------------------")
        appendLine("Payment: ${if (invoice.paymentType == "ONLINE") "Online (${invoice.bankingAppName})" else "Cash"}")
        appendLine("Total Paid: $currency ${invoice.sellingPrice}")
        appendLine("-------------------------------")
        appendLine("WARRANTY GUARANTEE:")
        appendLine("• Service Warranty: ${invoice.serviceWarrantyYears} Years")
        appendLine("• Replacement: ${invoice.replacementWarrantyDays} Days")
        appendLine("Terms: ${invoice.warrantyTerms}")
        appendLine("===============================")
    }

    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, "$shopName Invoice - ${invoice.invoiceNumber}")
        putExtra(Intent.EXTRA_TEXT, text)
    }
    context.startActivity(Intent.createChooser(intent, "Share Invoice Bill"))
}
