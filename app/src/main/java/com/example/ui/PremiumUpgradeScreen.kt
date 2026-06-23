package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun PremiumUpgradeScreen(viewModel: DashboardViewModel) {
    val isPremium by viewModel.isPremium.collectAsState()
    var phoneNumber by remember { mutableStateOf("") }

    if (isPremium) {
        val context = LocalContext.current
        var subTab by remember { mutableStateOf(0) }
        val inventoryList by viewModel.fuelInventoryList.collectAsState()
        val truckList by viewModel.truckList.collectAsState()

        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Surface(
                color = Color(0xFF1B2735),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "👑 Premium Safe Mode Activated",
                            color = Color(0xFFE1B12C),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                    }

                    // Sliding Premium Tabs
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 12.dp, end = 12.dp, bottom = 10.dp)
                            .background(Color(0xFF0F172A), RoundedCornerShape(8.dp))
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        val subLabels = listOf("Live Monitor", "Console", "Reports")
                        subLabels.forEachIndexed { idx, label ->
                            val isSelected = subTab == idx
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (isSelected) Color(0xFF5288C1) else Color.Transparent)
                                    .clickable { subTab = idx }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = label,
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) Color.White else Color(0xFF64748B)
                                )
                            }
                        }
                    }
                }
            }
            
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                when (subTab) {
                    0 -> FuelStationMonitorScreen(viewModel = viewModel)
                    1 -> MadiyaGasStationDashboard(
                        inventoryList = inventoryList,
                        truckList = truckList,
                        onTriggerDailySales = { viewModel.triggerDailySales(it) }
                    )
                    2 -> MadiyaAnalyticsScreen(
                        context = context,
                        inventoryList = inventoryList,
                        viewModel = viewModel
                    )
                }
            }
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Gara Premiumtti Olguddisi",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White
            )
            Text(
                text = "Kaffaltii xiqqoo (50 ETB) Telebirr ykn Chapa'n kaffaluun IoT Sensor hordoffii boba'aa fi AI Fact-Checker guutummaatti banadhu.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(vertical = 10.dp)
            )

            OutlinedTextField(
                value = phoneNumber,
                onValueChange = { phoneNumber = it },
                label = { Text("Lakkoofsa Bilbilaa (+251...)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = { viewModel.startPremiumUpgrade(phoneNumber) },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5288C1)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Amma Kaffali (Telebirr/Chapa)", color = Color.White)
            }
        }
    }
}
