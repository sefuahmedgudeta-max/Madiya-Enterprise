package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.draw.clip
import android.widget.Toast
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.FuelInventory
import com.example.data.MadiyaEnterpriseRepository
import com.example.data.TruckTracking
import java.util.Locale

@Composable
fun MadiyaGasStationDashboard(
    inventoryList: List<FuelInventory>,
    truckList: List<TruckTracking>,
    onTriggerDailySales: (Double) -> Unit
) {
    val viewModel: DashboardViewModel = viewModel()
    var salesInput by remember { mutableStateOf("") }
    val latestInventory = inventoryList.firstOrNull()
    val telebirrStatus by MadiyaEnterpriseRepository.telebirrTransactionStatus.collectAsState()
    val scrollState = rememberScrollState()

    val themes = remember {
        listOf(
            CipherKeyTheme(
                name = "Cosmic Amber",
                localizedName = "Amber Iccitii",
                primaryAccent = Color(0xFFF39C12),
                secondaryAccent = Color(0xFFE74C3C),
                surfaceColor = Color(0xFF1E1E1E),
                backgroundColor = Color(0xFF121212),
                textAccent = Color(0xFFFFB74D)
            ),
            CipherKeyTheme(
                name = "Ocean Blue",
                localizedName = "Garba Cuquliisa",
                primaryAccent = Color(0xFF00C9FF),
                secondaryAccent = Color(0xFF3498DB),
                surfaceColor = Color(0xFF131D2A),
                backgroundColor = Color(0xFF0A1118),
                textAccent = Color(0xFF58A6FF)
            ),
            CipherKeyTheme(
                name = "Forest Green",
                localizedName = "Bosona Magariisa",
                primaryAccent = Color(0xFF2ECC71),
                secondaryAccent = Color(0xFF27AE60),
                surfaceColor = Color(0xFF12231C),
                backgroundColor = Color(0xFF081410),
                textAccent = Color(0xFF81C784)
            )
        )
    }
    var selectedTheme by remember { mutableStateOf(themes[0]) }

    // Password generator states
    var passwordLength by remember { mutableFloatStateOf(16f) }
    var incUpper by remember { mutableStateOf(true) }
    var incLower by remember { mutableStateOf(true) }
    var incNumbers by remember { mutableStateOf(true) }
    var incSymbols by remember { mutableStateOf(true) }
    var generatedPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(true) }

    // Privacy Mode states
    var isPrivacyModeEnabled by remember { mutableStateOf(false) }
    var countdownSeconds by remember { mutableIntStateOf(60) }
    var isPasswordCleared by remember { mutableStateOf(false) }
    var timerResetToggle by remember { mutableIntStateOf(0) }
    var isCipherKeyUnlocked by remember { mutableStateOf(false) }

    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

    // Generate password initially or whenever options change
    LaunchedEffect(passwordLength, incUpper, incLower, incNumbers, incSymbols) {
        generatedPassword = generateSecurePassword(
            length = passwordLength.toInt(),
            includeUpper = incUpper,
            includeLower = incLower,
            includeNumbers = incNumbers,
            includeSymbols = incSymbols
        )
        isPasswordCleared = false
    }

    // Auto-clear countdown for Privacy Mode
    LaunchedEffect(generatedPassword, isPrivacyModeEnabled, timerResetToggle) {
        if (isPrivacyModeEnabled && generatedPassword.isNotEmpty()) {
            isPasswordCleared = false
            countdownSeconds = 60
            while (countdownSeconds > 0) {
                kotlinx.coroutines.delay(1000L)
                countdownSeconds--
            }
            isPasswordCleared = true
        } else {
            isPasswordCleared = false
        }
    }

    val strength = checkPasswordStrength(
        password = generatedPassword,
        length = passwordLength.toInt(),
        includeUpper = incUpper,
        includeLower = incLower,
        includeNumbers = incNumbers,
        includeSymbols = incSymbols
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(selectedTheme.backgroundColor)
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        // App Header Panel
        Card(
            colors = CardDefaults.cardColors(containerColor = selectedTheme.surfaceColor),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Business,
                    contentDescription = "Madiya Enterprise",
                    tint = selectedTheme.primaryAccent,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "⛽ MADIYA ENTERPRISE CONSOLE",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "System Real-time Monitoring & Tax Automation",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
            }
        }

        // THEME SELECTION OPTION (SETTINGS)
        Card(
            colors = CardDefaults.cardColors(containerColor = selectedTheme.surfaceColor),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Theme Icon",
                        tint = selectedTheme.primaryAccent,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "⚙️ Filannoo Halluu (Workspace Theme Setting)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = selectedTheme.primaryAccent
                    )
                }
                
                Text(
                    text = "Qurxummeessaa halluu galmee fi surtuu iccitii to'adhu.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    themes.forEach { theme ->
                        val isSelected = selectedTheme.name == theme.name
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) theme.primaryAccent else Color(0xFF252525))
                                .clickable { selectedTheme = theme }
                                .padding(vertical = 10.dp, horizontal = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = theme.name,
                                    color = if (isSelected) Color.Black else Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center
                                )
                                Text(
                                    text = theme.localizedName,
                                    color = if (isSelected) Color(0xFF222222) else Color.Gray,
                                    fontSize = 9.sp,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }
        }

        // 1. GALMEE FI HORDOFFII BOBA'AA (TANKARII)
        Card(
            colors = CardDefaults.cardColors(containerColor = selectedTheme.surfaceColor),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.LocalGasStation,
                        contentDescription = "Inventory Icon",
                        tint = Color.Cyan,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "📊 Haala Kuusaa Tankaarii (Live Logs)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.Cyan
                    )
                }
                
                Divider(color = Color.DarkGray, modifier = Modifier.padding(bottom = 8.dp))
                
                Text("Boba'aa Bilaaloon Dhufe: ${latestInventory?.bilaalooDhufe ?: 0.0} L", color = Color.White, style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(4.dp))
                Text("Kan Gurgurame: ${latestInventory?.kanGurgurame ?: 0.0} L", color = Color.White, style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(4.dp))
                Text("Hafii Kuusaa Keessa Jiru: ${latestInventory?.tankariiKeessaJiru ?: 0.0} L", color = Color.Green, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
            }
        }

        // 2. HORDOFFII KONKOLAATTOOTAA (GPS & TRUCK SENSOR)
        Card(
            colors = CardDefaults.cardColors(containerColor = selectedTheme.surfaceColor),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.LocalShipping,
                        contentDescription = "Truck Tracking Icon",
                        tint = Color.Yellow,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "🚚 Hordoffii Konkolaataa Boba'aa (Madiya Logistics)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.Yellow
                    )
                }
                
                Divider(color = Color.DarkGray, modifier = Modifier.padding(bottom = 8.dp))
                
                if (truckList.isEmpty()) {
                    Text("No active delivery trucks registered.", color = Color.Gray, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(vertical = 4.dp))
                } else {
                    truckList.forEach { truck ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF262626)),
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Konkolaataa: ${truck.truckId}",
                                    color = Color.White,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Hanga: ${truck.initialFuel} L | Status: ${truck.status}",
                                    color = Color.LightGray,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }
            }
        }

        // 3. GURGURTAA & GIBIRA TELEBIRR OTOMAATIKII
        Card(
            colors = CardDefaults.cardColors(containerColor = selectedTheme.surfaceColor),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.LocalAtm,
                        contentDescription = "Finance Icon",
                        tint = Color.Green,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "💰 Raawwii Gurgurtaa Madiya (Automatic Telebirr)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.Green
                    )
                }
                
                Divider(color = Color.DarkGray, modifier = Modifier.padding(bottom = 8.dp))
                
                OutlinedTextField(
                    value = salesInput,
                    onValueChange = { salesInput = it },
                    label = { Text("Gurgurtaa Guyyaa (ETB)", color = Color.Gray) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color.Green,
                        unfocusedBorderColor = Color.LightGray,
                        focusedLabelColor = Color.Green,
                        unfocusedLabelColor = Color.Gray
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Telebirr Status:",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.LightGray
                    )
                    Text(
                        text = telebirrStatus,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (telebirrStatus == "MILKAA'EERA") Color.Green else Color.Yellow
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = { 
                        val sales = salesInput.toDoubleOrNull() ?: 0.0
                        onTriggerDailySales(sales)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Gurgurtaa Reegisari Godhi", fontWeight = FontWeight.Bold)
                }
            }
        }

        // WATER DETECTION & NOZZLE ACTIVITY
        Card(
            colors = CardDefaults.cardColors(containerColor = selectedTheme.surfaceColor),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
                Text("🛡️ Sensorii Dabalataa", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    // Water
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(imageVector = Icons.Default.WaterDrop, contentDescription = null, tint = Color.Blue)
                        Text("Bishaan: 0.2L", style = MaterialTheme.typography.bodySmall)
                    }
                    // Nozzle
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = Color.Green)
                        Text("Nozzle: Idle", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }

        // SHIFT MANAGEMENT
        Card(
            colors = CardDefaults.cardColors(containerColor = selectedTheme.surfaceColor),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
                Text("👤 Shiftii Hojjettootaa", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = { /* TODO: Start/Stop shift */ },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Blue)
                ) {
                    Text("Shiftii Jalqabi/Cufi")
                }
            }
        }
        
        // AI FORECASTING
        Card(
            colors = CardDefaults.cardColors(containerColor = selectedTheme.surfaceColor),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
                Text("🔮 Raajii Gabaa (AI)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = { viewModel.getFuelForecast("Gurgurtaa fi ragaa raabsa boba'aa") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Magenta)
                ) {
                    Text("Tilmaama Argadhu")
                }
                val forecast by viewModel.forecast.collectAsState()
                forecast?.let {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = it, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                }
            }
        }
        
        // TELEBIRR QR SCAN SECTION
        Card(
            colors = CardDefaults.cardColors(containerColor = selectedTheme.surfaceColor),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.QrCodeScanner,
                        contentDescription = "Telebirr QR Icon",
                        tint = Color.Magenta,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "📱 Kaffaltii Telebirr (QR Scanner)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.Magenta
                    )
                }

                Divider(color = Color.DarkGray, modifier = Modifier.padding(bottom = 8.dp))

                Text(
                    text = "DISPENSER irraa QR koodii haa scan-godhu.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                Button(
                    onClick = {
                        // TODO: Implement actual QR scanning
                        Toast.makeText(context, "Scanning telebirr QR...", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Magenta),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(imageVector = Icons.Default.QrCodeScanner, contentDescription = "Scan")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Scanner-Godhi", fontWeight = FontWeight.Bold)
                }
            }
        }

        // 4. SECURE RANDOM PASSWORD GENERATION TOOL
        Spacer(modifier = Modifier.height(4.dp))
        Card(
            colors = CardDefaults.cardColors(containerColor = selectedTheme.surfaceColor),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Icon(
                        imageVector = if (isCipherKeyUnlocked) Icons.Default.LockOpen else Icons.Default.Lock,
                        contentDescription = "Lock Icon",
                        tint = selectedTheme.primaryAccent,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "🔑 CipherKey Pro Dashboard",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = selectedTheme.primaryAccent,
                        modifier = Modifier.weight(1f)
                    )
                    if (isCipherKeyUnlocked) {
                        IconButton(onClick = { isCipherKeyUnlocked = false }) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Lock Workspace",
                                tint = Color.LightGray,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                Divider(color = Color.DarkGray, modifier = Modifier.padding(bottom = 8.dp))

                if (!isCipherKeyUnlocked) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .background(Color(0xFF2C2C2C), RoundedCornerShape(32.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Fingerprint,
                                contentDescription = "Fingerprint lock indicator",
                                tint = selectedTheme.secondaryAccent,
                                modifier = Modifier.size(36.dp)
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        Text(
                            text = "CipherKey Pro Dashboard is Locked",
                            style = MaterialTheme.typography.titleSmall,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        
                        Text(
                            text = "Surtuu Iccitii argachuuf mirkaneessa biometrics dhiyeessi.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                        )
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        Button(
                            onClick = {
                                triggerBiometricAuth(
                                    context = context,
                                    onSuccess = {
                                        isCipherKeyUnlocked = true
                                        Toast.makeText(context, "Surtuun banameera! (Auth success)", Toast.LENGTH_SHORT).show()
                                    },
                                    onFailure = { err ->
                                        if (err == "FALLBACK") {
                                            Toast.makeText(context, "PIN Manual fayyadama.", Toast.LENGTH_SHORT).show()
                                        } else {
                                            Toast.makeText(context, "Biometrics fails: $err", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                )
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = selectedTheme.secondaryAccent),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(imageVector = Icons.Default.Fingerprint, contentDescription = "Biometrics Scan")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Dhiyeessi / Biometrics Scan", fontWeight = FontWeight.Bold, color = Color.White)
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        var showPinField by remember { mutableStateOf(false) }
                        var enteredPin by remember { mutableStateOf("") }
                        
                        if (!showPinField) {
                            TextButton(onClick = { showPinField = true }) {
                                Text("Gara PIN Maanuwaalii deemi (Manual PIN Fallback)", color = Color.Cyan, style = MaterialTheme.typography.bodySmall)
                            }
                        } else {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedTextField(
                                    value = enteredPin,
                                    onValueChange = { enteredPin = it },
                                    label = { Text("Galchi PIN (1234)", color = Color.Gray) },
                                    singleLine = true,
                                    visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation(),
                                    modifier = Modifier.weight(1f),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.LightGray,
                                        focusedBorderColor = selectedTheme.primaryAccent,
                                        unfocusedBorderColor = Color.DarkGray
                                    )
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Button(
                                    onClick = {
                                        if (enteredPin == "1234" || enteredPin.lowercase() == "demo") {
                                            isCipherKeyUnlocked = true
                                            Toast.makeText(context, "Surtuun Maanuwaalin banameera!", Toast.LENGTH_SHORT).show()
                                        } else {
                                            Toast.makeText(context, "PIN dogoggora! (PIN must be 1234)", Toast.LENGTH_SHORT).show()
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF34495E))
                                ) {
                                    Text("UnLock", color = Color.White)
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            Button(
                                onClick = {
                                    isCipherKeyUnlocked = true
                                    Toast.makeText(context, "Web Simulator Auth Success (Unlocked!)", Toast.LENGTH_LONG).show()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = selectedTheme.primaryAccent),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(imageVector = Icons.Default.TouchApp, contentDescription = "Simulator")
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Simulate Touch-Scan (Emulator Bypass)", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                } else {
                    Text(
                        text = "Gurgurtoota daldalaa fi ragaalee iccitii kee eeguuf password haala amansiisaa ta'een asitti uumi.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.LightGray,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    // Privacy Mode Toggle Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF252525), RoundedCornerShape(8.dp))
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                            .padding(bottom = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Privacy Lock icon",
                                tint = if (isPrivacyModeEnabled) Color(0xFF4CAF50) else Color.Gray,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "Tursiisa Iccitii (Privacy Mode)",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = if (isPrivacyModeEnabled && !isPasswordCleared) {
                                        "Iccitiin sekondi ${countdownSeconds}s booda ni dhabama"
                                    } else if (isPasswordCleared) {
                                        "Iccitiin eegameera (Cleared)"
                                    } else {
                                        "Sekondi 60 booda ofiin qulqulleessa"
                                    },
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (isPrivacyModeEnabled && !isPasswordCleared) selectedTheme.textAccent else Color.Gray
                                )
                            }
                        }
                        Switch(
                            checked = isPrivacyModeEnabled,
                            onCheckedChange = { isPrivacyModeEnabled = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = selectedTheme.primaryAccent,
                                checkedTrackColor = selectedTheme.primaryAccent.copy(alpha = 0.4f),
                                uncheckedThumbColor = Color.LightGray,
                                uncheckedTrackColor = Color.DarkGray
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Render Password Field
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF2C2C2C)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = if (isPasswordCleared) {
                                    "[Privacy Mode: Safe Cleared - Deebisi Uumi]"
                                } else if (passwordVisible) {
                                    generatedPassword
                                } else {
                                    "•".repeat(generatedPassword.length)
                                },
                                color = if (isPasswordCleared) Color.Gray else Color.White,
                                style = if (isPasswordCleared) MaterialTheme.typography.bodyMedium else MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.weight(1f)
                            )

                            Row {
                                IconButton(
                                    onClick = { 
                                        if (!isPasswordCleared) {
                                            passwordVisible = !passwordVisible 
                                            timerResetToggle++
                                        }
                                    },
                                    enabled = !isPasswordCleared
                                ) {
                                    Icon(
                                        imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                        contentDescription = "Toggle Visibility",
                                        tint = if (isPasswordCleared) Color.DarkGray else Color.LightGray
                                    )
                                }
                                IconButton(
                                    onClick = {
                                        if (isPasswordCleared) {
                                            Toast.makeText(context, "Surtuu haaraa uumi! (Password cleared! Regenerate first)", Toast.LENGTH_SHORT).show()
                                        } else if (generatedPassword.isNotEmpty()) {
                                            clipboardManager.setText(AnnotatedString(generatedPassword))
                                            Toast.makeText(context, "Iccitii Surtuu Coppied! (Word copied)", Toast.LENGTH_SHORT).show()
                                            timerResetToggle++
                                        }
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ContentCopy,
                                        contentDescription = "Content Copy",
                                        tint = if (isPasswordCleared) Color.DarkGray else selectedTheme.primaryAccent
                                    )
                                }
                            }
                        }
                    }

                    // Password Strength Indicator
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Cimina / Strength:", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                        Text(strength.label, style = MaterialTheme.typography.bodySmall, color = strength.color, fontWeight = FontWeight.Bold)
                    }

                    LinearProgressIndicator(
                        progress = { strength.progress },
                        color = strength.color,
                        trackColor = Color.DarkGray,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .padding(bottom = 16.dp)
                    )

                    // Length Selector
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Dheerina: ${passwordLength.toInt()} Characters",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Slider(
                        value = passwordLength,
                        onValueChange = { passwordLength = it },
                        valueRange = 8f..32f,
                        colors = SliderDefaults.colors(
                            thumbColor = selectedTheme.primaryAccent,
                            activeTrackColor = selectedTheme.primaryAccent,
                            inactiveTrackColor = Color.DarkGray
                        ),
                        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                    )

                    // Complexity Toggles
                    Text(
                        text = "Qulqullina Fillannoo (Complexity Options):",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = incUpper,
                                onCheckedChange = { if (it || incLower || incNumbers || incSymbols) incUpper = it },
                                colors = CheckboxDefaults.colors(checkedColor = selectedTheme.primaryAccent)
                            )
                            Text("A-Z", style = MaterialTheme.typography.bodySmall, color = Color.LightGray)
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = incLower,
                                onCheckedChange = { if (it || incUpper || incNumbers || incSymbols) incLower = it },
                                colors = CheckboxDefaults.colors(checkedColor = selectedTheme.primaryAccent)
                            )
                            Text("a-z", style = MaterialTheme.typography.bodySmall, color = Color.LightGray)
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = incNumbers,
                                onCheckedChange = { if (it || incUpper || incLower || incSymbols) incNumbers = it },
                                colors = CheckboxDefaults.colors(checkedColor = selectedTheme.primaryAccent)
                            )
                            Text("0-9", style = MaterialTheme.typography.bodySmall, color = Color.LightGray)
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = incSymbols,
                                onCheckedChange = { if (it || incUpper || incLower || incNumbers) incSymbols = it },
                                colors = CheckboxDefaults.colors(checkedColor = selectedTheme.primaryAccent)
                            )
                            Text("@#$*", style = MaterialTheme.typography.bodySmall, color = Color.LightGray)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            generatedPassword = generateSecurePassword(
                                length = passwordLength.toInt(),
                                includeUpper = incUpper,
                                includeLower = incLower,
                                includeNumbers = incNumbers,
                                includeSymbols = incSymbols
                            )
                            timerResetToggle++
                            isPasswordCleared = false
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = selectedTheme.primaryAccent.copy(alpha = 0.25f),
                            contentColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(imageVector = Icons.Default.Refresh, contentDescription = "Regenerate")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Deebisi Uumi (Regenerate)", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

private fun generateSecurePassword(
    length: Int,
    includeUpper: Boolean,
    includeLower: Boolean,
    includeNumbers: Boolean,
    includeSymbols: Boolean
): String {
    val upperChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
    val lowerChars = "abcdefghijklmnopqrstuvwxyz"
    val numberChars = "0123456789"
    val symbolChars = "!@#$%^&*()_+-=[]{}|;':\",./<>?"

    var allowedChars = ""
    if (includeUpper) allowedChars += upperChars
    if (includeLower) allowedChars += lowerChars
    if (includeNumbers) allowedChars += numberChars
    if (includeSymbols) allowedChars += symbolChars

    if (allowedChars.isEmpty()) {
        return ""
    }

    val secureRandom = java.security.SecureRandom()
    val password = java.lang.StringBuilder()
    
    // Ensure we satisfy at least one of each selected category
    val guaranteedList = mutableListOf<Char>()
    if (includeUpper) guaranteedList.add(upperChars[secureRandom.nextInt(upperChars.length)])
    if (includeLower) guaranteedList.add(lowerChars[secureRandom.nextInt(lowerChars.length)])
    if (includeNumbers) guaranteedList.add(numberChars[secureRandom.nextInt(numberChars.length)])
    if (includeSymbols) guaranteedList.add(symbolChars[secureRandom.nextInt(symbolChars.length)])

    for (i in 0 until (length - guaranteedList.size).coerceAtLeast(0)) {
        val nextIdx = secureRandom.nextInt(allowedChars.length)
        password.append(allowedChars[nextIdx])
    }

    // Insert guaranteed characters and shuffle
    guaranteedList.forEach { char ->
        if (password.length < length) {
            password.append(char)
        } else {
            val replaceIdx = secureRandom.nextInt(password.length)
            password.setCharAt(replaceIdx, char)
        }
    }

    val list = password.toString().toList().shuffled(java.util.Random(secureRandom.nextLong()))
    return list.joinToString("")
}

private enum class PasswordStrength(val label: String, val color: Color, val progress: Float) {
    WEAK("Saphamaa / Weak", Color(0xFFE57373), 0.33f),
    GOOD("Gaarii / Good", Color(0xFFFFB74D), 0.67f),
    STRONG("Cinaa / Strong", Color(0xFF4CAF50), 1.0f)
}

private fun checkPasswordStrength(
    password: String,
    length: Int,
    includeUpper: Boolean,
    includeLower: Boolean,
    includeNumbers: Boolean,
    includeSymbols: Boolean
): PasswordStrength {
    if (password.isEmpty()) return PasswordStrength.WEAK
    
    var score = 0
    if (length >= 12) score += 1
    if (length >= 18) score += 1
    
    var categories = 0
    if (includeUpper) categories += 1
    if (includeLower) categories += 1
    if (includeNumbers) categories += 1
    if (includeSymbols) categories += 1

    if (categories >= 3) score += 1
    if (categories == 4) score += 1

    return when {
        score <= 0 || categories <= 1 -> PasswordStrength.WEAK
        score <= 2 && categories >= 2 -> PasswordStrength.GOOD
        else -> PasswordStrength.STRONG
    }
}

private fun triggerBiometricAuth(
    context: android.content.Context,
    onSuccess: () -> Unit,
    onFailure: (String) -> Unit
) {
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
        val executor = androidx.core.content.ContextCompat.getMainExecutor(context)
        try {
            val biometricPrompt = android.hardware.biometrics.BiometricPrompt.Builder(context)
                .setTitle("CipherKey Pro")
                .setSubtitle("Teessoo Surtuu Iccitii (Credentials Bank)")
                .setDescription("Agarsiisi fingerprint ykn sawura kee galchan.")
                .setNegativeButton("Koodii PIN", executor) { _, _ ->
                    onFailure("FALLBACK")
                }
                .build()

            val cancellationSignal = android.os.CancellationSignal()
            biometricPrompt.authenticate(
                cancellationSignal,
                executor,
                object : android.hardware.biometrics.BiometricPrompt.AuthenticationCallback() {
                    override fun onAuthenticationSucceeded(result: android.hardware.biometrics.BiometricPrompt.AuthenticationResult?) {
                        super.onAuthenticationSucceeded(result)
                        onSuccess()
                    }

                    override fun onAuthenticationError(errorCode: Int, errString: CharSequence?) {
                        super.onAuthenticationError(errorCode, errString)
                        onFailure(errString?.toString() ?: "Error code: $errorCode")
                    }

                    override fun onAuthenticationFailed() {
                        super.onAuthenticationFailed()
                        onFailure("Tiraasii dhabameera (Fingerprint not recognized)")
                    }
                }
            )
        } catch (e: Exception) {
            onFailure("Biometric error: ${e.message}")
        }
    } else {
        onFailure("SDK level below 28")
    }
}

data class CipherKeyTheme(
    val name: String,
    val localizedName: String,
    val primaryAccent: Color,
    val secondaryAccent: Color,
    val surfaceColor: Color,
    val backgroundColor: Color,
    val textAccent: Color
)

