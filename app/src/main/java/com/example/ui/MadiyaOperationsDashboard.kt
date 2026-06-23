package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import android.widget.Toast

@Composable
fun MadiyaOperationsDashboard(
    viewModel: DashboardViewModel
) {
    val scrollState = rememberScrollState()
    val context = LocalContext.current
    val cardColor = Color(0xFF1E1E1E)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212))
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        Text(
            text = "⚙️ MADIYA ADVANCED OPS",
            style = MaterialTheme.typography.headlineSmall,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // 1. Pump Leak Alerts
        Card(colors = CardDefaults.cardColors(containerColor = cardColor), shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("🚨 Pump Leak & Pressure Alerts", style = MaterialTheme.typography.titleMedium, color = Color.White)
                Text("Pressure normal. All pumps secured.", style = MaterialTheme.typography.bodySmall, color = Color.Green)
            }
        }

        // 2. Loyalty Rewards
        Card(colors = CardDefaults.cardColors(containerColor = cardColor), shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("✨ Loyalty & Cashback", style = MaterialTheme.typography.titleMedium, color = Color.White)
                Text("Active members: 450. Points issued today: 1200.", style = MaterialTheme.typography.bodySmall, color = Color.Cyan)
            }
        }

        // 3. Hourly Sales Analytics
        Card(colors = CardDefaults.cardColors(containerColor = cardColor), shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("📊 Hourly Sales Analytics", style = MaterialTheme.typography.titleMedium, color = Color.White)
                Text("Peak: 14:00 (500L/hr). Total: 3200L.", style = MaterialTheme.typography.bodySmall, color = Color.Yellow)
            }
        }

        // 4. Shift Handover
        Card(colors = CardDefaults.cardColors(containerColor = cardColor), shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("🤝 Shift Handover Logs", style = MaterialTheme.typography.titleMedium, color = Color.White)
                Button(onClick = { Toast.makeText(context, "Handover Logged Successfully", Toast.LENGTH_SHORT).show() }) {
                    Text("Complete Handover")
                }
            }
        }

        // 5. Smart Grid & Generators
        Card(colors = CardDefaults.cardColors(containerColor = cardColor), shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("⚡ Smart Grid & Generator", style = MaterialTheme.typography.titleMedium, color = Color.White)
                Text("Grid status: Stable.", style = MaterialTheme.typography.bodySmall, color = Color.Magenta)
            }
        }

        // 6. Pre-paid Wallet
        Card(colors = CardDefaults.cardColors(containerColor = cardColor), shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("💳 Fuel Wallet", style = MaterialTheme.typography.titleMedium, color = Color.White)
                Text("Company Wallet: 50,000 ETB.", style = MaterialTheme.typography.bodySmall, color = Color.Yellow)
            }
        }

        // 7. Octane Quality
        Card(colors = CardDefaults.cardColors(containerColor = cardColor), shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("🧪 Octane Quality Monitoring", style = MaterialTheme.typography.titleMedium, color = Color.White)
                Text("Current Level: 95 (Premium).", style = MaterialTheme.typography.bodySmall, color = Color.Green)
            }
        }

        // 9. AI Camera & LPR
        Card(colors = CardDefaults.cardColors(containerColor = cardColor), shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("📷 AI Camera & LPR", style = MaterialTheme.typography.titleMedium, color = Color.White)
                Text("Monitoring plates...", style = MaterialTheme.typography.bodySmall, color = Color.Green)
            }
        }

        // 10. Self-Healing
        Card(colors = CardDefaults.cardColors(containerColor = cardColor), shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("🔄 IoT Self-Healing", style = MaterialTheme.typography.titleMedium, color = Color.White)
                Text("System Status: Operational.", style = MaterialTheme.typography.bodySmall, color = Color.Cyan)
            }
        }

        // 11. Tap & Go
        Card(colors = CardDefaults.cardColors(containerColor = cardColor), shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("📲 Tap & Go (NFC)", style = MaterialTheme.typography.titleMedium, color = Color.White)
                Text("Tag Reader ready.", style = MaterialTheme.typography.bodySmall, color = Color.Yellow)
            }
        }

        // 12. ROI Analytics
        Card(colors = CardDefaults.cardColors(containerColor = cardColor), shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("📈 ROI & Analytics", style = MaterialTheme.typography.titleMedium, color = Color.White)
                Text("Projected ROI: 18% (2025-2029).", style = MaterialTheme.typography.bodySmall, color = Color.Magenta)
            }
        }

        // 13. Evaporation
        Card(colors = CardDefaults.cardColors(containerColor = cardColor), shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("📉 Shrinkage & Evaporation", style = MaterialTheme.typography.titleMedium, color = Color.White)
                Text("AI Modeling: 0.2% variance detected.", style = MaterialTheme.typography.bodySmall, color = Color.Red)
            }
        }

        // 14. Perimeter Security
        Card(colors = CardDefaults.cardColors(containerColor = cardColor), shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("🛡️ Perimeter Intrusion & Fire", style = MaterialTheme.typography.titleMedium, color = Color.White)
                Text("Security Status: Armed & Active.", style = MaterialTheme.typography.bodySmall, color = Color.Green)
            }
        }

        // 15. Self-Service Terminal
        Card(colors = CardDefaults.cardColors(containerColor = cardColor), shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("📟 Customer Self-Service", style = MaterialTheme.typography.titleMedium, color = Color.White)
                Button(onClick = { Toast.makeText(context, "Activating Self-Service...", Toast.LENGTH_SHORT).show() }) {
                    Text("Enable Terminal")
                }
            }
        }

        // 16. Multi-Station Dashboard
        Card(colors = CardDefaults.cardColors(containerColor = cardColor), shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("🌐 Multi-Station Cloud", style = MaterialTheme.typography.titleMedium, color = Color.White)
                Text("All stations synchronized.", style = MaterialTheme.typography.bodySmall, color = Color.Cyan)
            }
        }

        // 17. USSD Payment
        Card(colors = CardDefaults.cardColors(containerColor = cardColor), shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("📶 Offline USSD Payment", style = MaterialTheme.typography.titleMedium, color = Color.White)
                Text("USSD Service: Active.", style = MaterialTheme.typography.bodySmall, color = Color.Yellow)
            }
        }

        // 18. Solar Power
        Card(colors = CardDefaults.cardColors(containerColor = cardColor), shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("☀️ Solar Power Hybrid", style = MaterialTheme.typography.titleMedium, color = Color.White)
                Text("Solar Battery: 85%.", style = MaterialTheme.typography.bodySmall, color = Color.Green)
            }
        }

        // 19. Tanker GPS
        Card(colors = CardDefaults.cardColors(containerColor = cardColor), shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("📍 Tanker Gps Tracking", style = MaterialTheme.typography.titleMedium, color = Color.White)
                Text("Seal Status: Locked.", style = MaterialTheme.typography.bodySmall, color = Color.Cyan)
            }
        }

        // 20. Rationing Analytics
        Card(colors = CardDefaults.cardColors(containerColor = cardColor), shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("⚖️ Shortage & Rationing", style = MaterialTheme.typography.titleMedium, color = Color.White)
                Text("Stock: 3 days left under rationing.", style = MaterialTheme.typography.bodySmall, color = Color.Red)
            }
        }
        
        // 21. Anti-Fraud Verif
        Card(colors = CardDefaults.cardColors(containerColor = cardColor), shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("🛡️ Anti-Fraud Verification", style = MaterialTheme.typography.titleMedium, color = Color.White)
                Text("Last transaction verified.", style = MaterialTheme.typography.bodySmall, color = Color.Green)
            }
        }
        
        // 22. Water Infiltration
        Card(colors = CardDefaults.cardColors(containerColor = cardColor), shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("💧 Tanker Water & Silt Alert", style = MaterialTheme.typography.titleMedium, color = Color.White)
                Text("Quality monitor: Optimal.", style = MaterialTheme.typography.bodySmall, color = Color.Cyan)
            }
        }
        
        // 23. Offline SQLite Status
        Card(colors = CardDefaults.cardColors(containerColor = cardColor), shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("💾 Offline Local Storage", style = MaterialTheme.typography.titleMedium, color = Color.White)
                Text("Sync status: 0 pending records.", style = MaterialTheme.typography.bodySmall, color = Color.LightGray)
            }
        }
        
        // 24. Emergency Priority
        Card(colors = CardDefaults.cardColors(containerColor = cardColor), shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("🚑 Priority Vehicle Access", style = MaterialTheme.typography.titleMedium, color = Color.White)
                Text("Active priority: 1 (Ambulance).", style = MaterialTheme.typography.bodySmall, color = Color.Yellow)
            }
        }
        
        // 25. Dynamic Tariff Sync
        Card(colors = CardDefaults.cardColors(containerColor = cardColor), shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("🏷️ Local Tariff Sync", style = MaterialTheme.typography.titleMedium, color = Color.White)
                Text("Price synced: 70.00 ETB/L.", style = MaterialTheme.typography.bodySmall, color = Color.Cyan)
            }
        }
        
        // 26. Shift Expense Ledger
        Card(colors = CardDefaults.cardColors(containerColor = cardColor), shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("📊 Shift Expense Ledger", style = MaterialTheme.typography.titleMedium, color = Color.White)
                Text("Discrepancy: 0.00 ETB.", style = MaterialTheme.typography.bodySmall, color = Color.Green)
            }
        }
        
        // 27. Multi-bank Settlement
        Card(colors = CardDefaults.cardColors(containerColor = cardColor), shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("🏦 Multi-Bank Settlement", style = MaterialTheme.typography.titleMedium, color = Color.White)
                Text("Settled: Telebirr, CBE, Awash.", style = MaterialTheme.typography.bodySmall, color = Color.Yellow)
            }
        }
        
        // 28. Bulk Order Tracker
        Card(colors = CardDefaults.cardColors(containerColor = cardColor), shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("🚛 Bulk Order Tracker", style = MaterialTheme.typography.titleMedium, color = Color.White)
                Text("Status: Order #AB123 In Transit.", style = MaterialTheme.typography.bodySmall, color = Color.Magenta)
            }
        }

        // 29. Fuel Quality Sensor
        Card(colors = CardDefaults.cardColors(containerColor = cardColor), shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("🧪 Fuel Quality & Density", style = MaterialTheme.typography.titleMedium, color = Color.White)
                Text("Densiity Status: Stable (98%).", style = MaterialTheme.typography.bodySmall, color = Color.Cyan)
            }
        }

        // 30. Smart Queue Management
        Card(colors = CardDefaults.cardColors(containerColor = cardColor), shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("🚗 Smart Queue Priority", style = MaterialTheme.typography.titleMedium, color = Color.White)
                Text("Tokens issued: 12. Next: Token #A13.", style = MaterialTheme.typography.bodySmall, color = Color.Yellow)
            }
        }

        // 31. Agricultural Rationing
        Card(colors = CardDefaults.cardColors(containerColor = cardColor), shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("🚜 Farmer Fuel Rationing", style = MaterialTheme.typography.titleMedium, color = Color.White)
                Text("Daily ration quota active.", style = MaterialTheme.typography.bodySmall, color = Color.Green)
            }
        }

        // 32. POS Rollback Service
        Card(colors = CardDefaults.cardColors(containerColor = cardColor), shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("🔄 Transaction Rollback", style = MaterialTheme.typography.titleMedium, color = Color.White)
                Text("POS connectivity: Online.", style = MaterialTheme.typography.bodySmall, color = Color.LightGray)
            }
        }

        // 33. AI Security & Compliance
        Card(colors = CardDefaults.cardColors(containerColor = cardColor), shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("🛡️ Gemini Security Analysis", style = MaterialTheme.typography.titleMedium, color = Color.White)
                
                val securityState by viewModel.securityAnalysisState.collectAsState()
                
                Button(onClick = { viewModel.runSecurityAnalysis() }) {
                    Text("Run Analysis")
                }
                
                when (val state = securityState) {
                    is AIChatUiState.Loading -> Text("Analyzing logs...", color = Color.Yellow)
                    is AIChatUiState.Success -> Text(state.message, color = Color.Green, style = MaterialTheme.typography.bodySmall)
                    is AIChatUiState.Error -> Text("Error: ${state.error}", color = Color.Red, style = MaterialTheme.typography.bodySmall)
                    else -> Text("Ready to analyze.", color = Color.LightGray, style = MaterialTheme.typography.bodySmall)
                }
            }
        }

    }
}
