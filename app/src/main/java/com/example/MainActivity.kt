package com.example

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.example.R
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.CipherKeyEntity
import androidx.compose.ui.res.stringResource
import android.os.Bundle

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.lifecycle.viewmodel.compose.viewModel
import android.app.Application
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.animation.core.*
import androidx.compose.ui.draw.alpha
import com.example.data.FuelInventory
import com.example.data.TruckTracking
import com.example.ui.*
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(colorScheme = darkColorScheme()) {
                Surface(modifier = Modifier.fillMaxSize(), color = Color(0xFF0D1117)) {
                    MainAppNavigation()
                }
            }
        }
    }
}

@Composable
fun MainAppNavigation() {
    val context = LocalContext.current
    val viewModel: DashboardViewModel = viewModel(
        factory = DashboardViewModelFactory(context.applicationContext as Application)
    )

    var step by remember { mutableStateOf(1) } // 1: Login, 2: Premium Workspace
    var phoneNumber by remember { mutableStateOf("") }
    var otpCode by remember { mutableStateOf("") }
    var selectedTab by remember { mutableStateOf(0) }

    val liveInventories by viewModel.fuelInventoryList.collectAsState()
    val liveTrucks by viewModel.truckList.collectAsState()

    val fuelLogs = remember {
        mutableStateListOf(
            FuelInventoryData("23/06/2026", 8000.0, 1200.0, 6800.0),
            FuelInventoryData("22/06/2026", 0.0, 1500.0, 5300.0),
            SpacerData("21/06/2026", 7500.0)
        )
    }

    if (step == 1) {
        // --- VISUAL LOGIN SCREEN ---
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(Brush.linearGradient(listOf(Color(0xFF00C9FF), Color(0xFF92FE9D))), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text("S", color = Color.White, fontSize = 36.sp, fontWeight = FontWeight.Bold)
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            Text(stringResource(R.string.login_title), fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = Color.White, letterSpacing = 2.sp)
            Text(stringResource(R.string.login_subtitle), fontSize = 14.sp, color = Color.Gray)
            
            Spacer(modifier = Modifier.height(40.dp))
            
            if (otpCode.isEmpty()) {
                OutlinedTextField(
                    value = phoneNumber,
                    onValueChange = { phoneNumber = it },
                    label = { Text(stringResource(R.string.login_phone)) },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFF00C9FF))
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { otpCode = "123456" },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00C9FF))
                ) {
                    Text(stringResource(R.string.login_otp_send), fontWeight = FontWeight.Bold, color = Color.Black)
                }
            } else {
                OutlinedTextField(
                    value = otpCode,
                    onValueChange = { otpCode = it },
                    label = { Text(stringResource(R.string.login_otp_code)) },
                    visualTransformation = PasswordVisualTransformation(),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { step = 2 },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF92FE9D))
                ) {
                    Text(stringResource(R.string.login_confirm), fontWeight = FontWeight.Bold, color = Color.Black)
                }
            }
        }
    } else {
        // --- PREMIUM WORKSPACE NAVIGATION ---
        Scaffold(
            bottomBar = {
                NavigationBar(containerColor = Color(0xFF161B22), tonalElevation = 8.dp) {
                    NavigationBarItem(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        icon = { Icon(Icons.Default.Home, contentDescription = "Madiya", displayName = "IoT") },
                        label = { Text(stringResource(R.string.tab_iot)) }
                    )
                    NavigationBarItem(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        icon = { Icon(Icons.Default.Star, contentDescription = "Reports", displayName = "Reports") },
                        label = { Text(stringResource(R.string.tab_reports)) }
                    )
                    NavigationBarItem(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        icon = { Icon(Icons.Default.LocalGasStation, contentDescription = "GasStation") },
                        label = { Text(stringResource(R.string.tab_console)) }
                    )
                    NavigationBarItem(
                        selected = selectedTab == 3,
                        onClick = { selectedTab = 3 },
                        icon = { Icon(Icons.Default.LocalShipping, contentDescription = "FleetOps") },
                        label = { Text(stringResource(R.string.tab_fleet)) }
                    )
                    NavigationBarItem(
                        selected = selectedTab == 4,
                        onClick = { selectedTab = 4 },
                        icon = { Icon(Icons.Default.Lock, contentDescription = "CipherKey") },
                        label = { Text(stringResource(R.string.tab_cipher)) }
                    )
                }
            }
        ) { paddingValues ->
            Box(modifier = Modifier.padding(paddingValues).fillMaxSize().background(Color(0xFF0D1117))) {
                when (selectedTab) {
                    0 -> AdvancedMadiyaIotTab(fuelLogs)
                    1 -> MadiyaReportTab(fuelLogs)
                    2 -> MadiyaGasStationDashboard(
                        inventoryList = liveInventories,
                        truckList = liveTrucks,
                        onTriggerDailySales = { sales -> viewModel.triggerDailySales(sales) }
                    )
                    3 -> MadiyaOperationsDashboard(viewModel)
                    4 -> CipherKeyProTab(viewModel)
                }
            }
        }
    }    
}

@Composable
fun CipherKeyProTab(viewModel: DashboardViewModel) {
    val keys by viewModel.cipherKeys.collectAsStateWithLifecycle()
    var searchQuery by remember { mutableStateOf("") }
    
    val filteredKeys = remember(keys, searchQuery) {
        if (searchQuery.isBlank()) keys
        else keys.filter { it.identifier.contains(searchQuery, ignoreCase = true) }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Saved Cipher Keys", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Search Keys") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(filteredKeys) { key ->
                Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFF161B22))) {
                    Text(text = key.identifier, color = Color.White, modifier = Modifier.padding(16.dp))
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { 
            viewModel.insertKey("Key " + System.currentTimeMillis())
        }, modifier = Modifier.fillMaxWidth()) {
            Text("Generate & Save New Key")
        }
    }
}

 
// ---------------- TAB 2: ADVANCED IOT VISUAL DASHBOARD ----------------
@Composable
fun AdvancedMadiyaIotTab(fuelLogs: List<FuelInventoryData>) {
    val latest = fuelLogs.first()
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("⛽ Madiya Station Live IoT", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
        Text("To'annoo tankarii fi boba'aa dhufe", fontSize = 14.sp, color = Color.Gray)
        
        Spacer(modifier = Modifier.height(20.dp))

        // Visual Gauge Metrics Card
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF161B22)),
            modifier = Modifier.fillMaxWidth().border(1.dp, Color(0xFF30363D), RoundedCornerShape(20.dp))
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Kuusaa Tankaarii Ammaa", color = Color.LightGray, fontWeight = FontWeight.Medium)
                    Box(modifier = Modifier.background(Color(0xFF238636), RoundedCornerShape(6.dp)).padding(horizontal = 6.dp, vertical = 2.dp)) {
                        Text("LIVE", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                val isLowFuel = latest.tankarii < 1000.0
                if (isLowFuel) {
                    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
                    val alpha by infiniteTransition.animateFloat(
                        initialValue = 0.5f,
                        targetValue = 1f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(1000, easing = FastOutLinearInEasing),
                            repeatMode = RepeatMode.Reverse
                        ),
                        label = "alpha"
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 8.dp).alpha(alpha)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Low Fuel Warning",
                            tint = Color.Red,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Akkaataa: Boba'aa dhumuuf!", color = Color.Red, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                }
                Text(
                    String.format(Locale.US, "%,.2f Liters", latest.tankarii),
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (isLowFuel) Color.Red else Color(0xFF58A6FF)
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                LinearProgressIndicator(
                    progress = (latest.tankarii / 10000.0).toFloat(),
                    modifier = Modifier.fillMaxWidth().height(8.dp).clip(CircleShape),
                    color = Color(0xFF238636),
                    trackColor = Color(0xFF21262D)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        Text("📈 Haala Boba'aa (Consumption Trend)", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.LightGray)
        Spacer(modifier = Modifier.height(10.dp))
        
        // High-Fidelity Custom Canvas Trend Graph
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .background(Color(0xFF161B22), shape = RoundedCornerShape(16.dp))
                .border(1.dp, Color(0xFF30363D), RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val width = size.width
                val height = size.height
                
                // Draw decorative trend lines simulating d3-recharts grid lines
                drawLine(Color(0xFF21262D), Offset(0f, height/2), Offset(width, height/2), strokeWidth = 2f)
                
                val points = listOf(
                    Offset(0f, height * 0.3f),
                    Offset(width * 0.4f, height * 0.5f),
                    Offset(width * 0.8f, height * 0.2f),
                    Offset(width, height * 0.4f)
                )
                
                for (i in 0 until points.size - 1) {
                    drawLine(
                        brush = Brush.linearGradient(listOf(Color(0xFF00C9FF), Color(0xFF92FE9D))),
                        start = points[i],
                        end = points[i + 1],
                        strokeWidth = 6f
                    )
                }
            }
        }
    }
}

// ---------------- TAB 3: MADIYA INTERNAL REPORTS ----------------
@Composable
fun MadiyaReportTab(fuelLogs: List<FuelInventoryData>) {
    var salesInput by remember { mutableStateOf("") }
    var reportMessage by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("📊 Gabaasa Madiya", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
        Text("To'annoo kaffaltii otomaatikii", fontSize = 14.sp, color = Color.Gray)
        
        Spacer(modifier = Modifier.height(20.dp))

        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF161B22)),
            modifier = Modifier.fillMaxWidth().border(1.dp, Color(0xFF30363D), RoundedCornerShape(16.dp))
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                OutlinedTextField(
                    value = salesInput,
                    onValueChange = { salesInput = it },
                    label = { Text("Gurgurtaa Guyyaa (ETB)") },
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Button(
                    onClick = {
                        val sales = salesInput.toDoubleOrNull() ?: 0.0
                        val reserve = sales * 0.02
                        reportMessage = "⚡ TELEBIRR AUTOMATIC:\nReserve 2% (${reserve} ETB) gurgurtaa irraa citee herrega Madiyaatti online sync ta'eera!"
                    },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF238636))
                ) {
                    Text("Reegisari Godhi", fontWeight = FontWeight.Bold)
                }
            }
        }

        if (reportMessage.isNotEmpty()) {
            Spacer(modifier = Modifier.height(20.dp))
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1F2937)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = reportMessage, 
                    color = Color(0xFF58A6FF), 
                    modifier = Modifier.padding(16.dp),
                    fontSize = 14.sp,
                    textAlign = TextAlign.Start
                )
            }
        }
    }
}

// Data Model References
data class FuelInventoryData(val date: String, val dhufe: Double, val gurgurame: Double, val tankarii: Double)
@Composable
fun Icon(icon: androidx.compose.ui.graphics.vector.ImageVector, contentDescription: String, displayName: String) = Icon(icon, contentDescription)
fun SpacerData(date: String, tankarii: Double): FuelInventoryData = FuelInventoryData(date, 0.0, 0.0, tankarii)

