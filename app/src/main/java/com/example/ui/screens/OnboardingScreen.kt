package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.PointOfSale
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.BentoBlueCard
import com.example.ui.theme.BentoBorder
import com.example.ui.theme.BentoEmeraldCard
import com.example.ui.theme.BentoEmeraldCardText
import com.example.ui.theme.BentoIndigo
import com.example.ui.theme.BentoNavy
import com.example.ui.theme.BentoTextPrimary
import com.example.ui.theme.BentoTextSecondary
import com.example.ui.theme.EmeraldSecondaryLight
import com.example.ui.theme.IndigoPrimaryDark
import com.example.ui.theme.IndigoPrimaryLight

@Composable
fun OnboardingScreen(
    onComplete: (shopName: String, ownerEmail: String, branch1: String, branch2: String, currency: String, prefill: Boolean) -> Unit
) {
    var shopName by remember { mutableStateOf("LOGIN GADGET") }
    var ownerEmail by remember { mutableStateOf("preamleelapranto@gmail.com") }
    var branch1Name by remember { mutableStateOf("Branch 1 - Main City Hub") }
    var branch2Name by remember { mutableStateOf("Branch 2 - Downtown Outlet") }
    var currencySymbol by remember { mutableStateOf("$") }
    var prefillSampleLaptops by remember { mutableStateOf(true) }
    var isGoogleConnected by remember { mutableStateOf(true) }

    val scrollState = rememberScrollState()

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .imePadding(),
        color = Color(0xFFF8F9FF)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // App Brand Logo & Title
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(BentoNavy, Color(0xFF1E293B))
                        )
                    )
                    .border(2.dp, BentoIndigo.copy(alpha = 0.5f), RoundedCornerShape(20.dp)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_app_logo),
                    contentDescription = "App Logo",
                    modifier = Modifier.size(64.dp)
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Welcome to ShopInventory POS",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    color = BentoNavy
                )
                Text(
                    text = "Smart Retail, Multi-Branch & Google Drive Database Sync",
                    style = MaterialTheme.typography.bodyMedium,
                    color = BentoTextSecondary,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            // Google Drive Connected Account Card
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, BentoBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CloudDone,
                                contentDescription = null,
                                tint = BentoEmeraldCardText,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Google Account & Drive Sync",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleSmall,
                                color = BentoNavy
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = BentoEmeraldCard
                        ) {
                            Text(
                                text = "Connected",
                                color = BentoEmeraldCardText,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Text(
                        text = "Your business database will automatically sync snapshots to Google Drive. Local copy ensures uninterrupted offline performance.",
                        fontSize = 12.sp,
                        color = BentoTextSecondary
                    )

                    OutlinedTextField(
                        value = ownerEmail,
                        onValueChange = { ownerEmail = it },
                        label = { Text("Shop's Google Account") },
                        leadingIcon = {
                            Icon(Icons.Default.AccountCircle, contentDescription = null, tint = BentoNavy)
                        },
                        singleLine = true,
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
                            .testTag("onboarding_google_email_input"),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }

            // Business Setup Card
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
                        Icon(
                            imageVector = Icons.Default.Storefront,
                            contentDescription = null,
                            tint = BentoIndigo,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Business Information",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium,
                            color = BentoNavy
                        )
                    }

                    OutlinedTextField(
                        value = shopName,
                        onValueChange = { shopName = it },
                        label = { Text("Business Shop Name *") },
                        placeholder = { Text("e.g. LOGIN GADGET") },
                        leadingIcon = {
                            Icon(Icons.Default.Business, contentDescription = null, tint = BentoNavy)
                        },
                        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
                        singleLine = true,
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
                            .testTag("onboarding_shop_name_input"),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = currencySymbol,
                            onValueChange = { currencySymbol = it },
                            label = { Text("Currency") },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color(0xFFF8FAFC),
                                unfocusedContainerColor = Color(0xFFF8FAFC),
                                focusedBorderColor = BentoIndigo,
                                unfocusedBorderColor = BentoBorder,
                                focusedTextColor = BentoNavy,
                                unfocusedTextColor = BentoNavy
                            ),
                            modifier = Modifier.weight(0.35f),
                            shape = RoundedCornerShape(12.dp)
                        )

                        OutlinedTextField(
                            value = branch1Name,
                            onValueChange = { branch1Name = it },
                            label = { Text("Primary Branch 1") },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color(0xFFF8FAFC),
                                unfocusedContainerColor = Color(0xFFF8FAFC),
                                focusedBorderColor = BentoIndigo,
                                unfocusedBorderColor = BentoBorder,
                                focusedTextColor = BentoNavy,
                                unfocusedTextColor = BentoNavy
                            ),
                            modifier = Modifier.weight(0.65f),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }

                    OutlinedTextField(
                        value = branch2Name,
                        onValueChange = { branch2Name = it },
                        label = { Text("Secondary Branch 2") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFFF8FAFC),
                            unfocusedContainerColor = Color(0xFFF8FAFC),
                            focusedBorderColor = BentoIndigo,
                            unfocusedBorderColor = BentoBorder,
                            focusedTextColor = BentoNavy,
                            unfocusedTextColor = BentoNavy
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }

            // Sample Inventory Prefill Option
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, BentoBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = prefillSampleLaptops,
                        onCheckedChange = { prefillSampleLaptops = it },
                        colors = CheckboxDefaults.colors(
                            checkedColor = BentoNavy,
                            uncheckedColor = BentoTextSecondary
                        ),
                        modifier = Modifier.testTag("onboarding_prefill_checkbox")
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "Preload 10 Laptop Inventory Units",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = BentoNavy
                        )
                        Text(
                            text = "Includes 3 Brand New, 3 Used, 3 Open Box, and 1 Special Unit with full specs & serial numbers.",
                            fontSize = 12.sp,
                            color = BentoTextSecondary
                        )
                    }
                }
            }

            // Launch Experience Button
            Button(
                onClick = {
                    if (shopName.isNotBlank()) {
                        onComplete(
                            shopName,
                            ownerEmail,
                            branch1Name,
                            branch2Name,
                            currencySymbol,
                            prefillSampleLaptops
                        )
                    }
                },
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .testTag("onboarding_submit_button"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BentoNavy
                )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Enter Shop Dashboard",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = null,
                        tint = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
