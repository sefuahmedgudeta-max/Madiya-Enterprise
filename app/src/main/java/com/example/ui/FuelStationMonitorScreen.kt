package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EvStation
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import java.util.Locale
import com.example.data.IotSensorLog
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun FuelStationMonitorScreen(viewModel: DashboardViewModel) {
    val sensorLogs by viewModel.recentLogs.collectAsState(initial = emptyList())
    val coroutineScope = rememberCoroutineScope()
    var isRefreshing by remember { mutableStateOf(false) }
    
    // Recent fuel level or fallback
    val latestFuelLog = sensorLogs.firstOrNull { it.sensorType == "FUEL_LEVEL" }
    val latestFuel = latestFuelLog?.sensorValue ?: 0.0
    val stationName = latestFuelLog?.stationName ?: "Madiya Gas Station"

    // Recent GPS tracking distance or fallback
    val latestGps = sensorLogs.firstOrNull { it.sensorType == "GPS_TRACKER" }?.sensorValue ?: 0.0

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF17212B))
            .padding(16.dp)
    ) {
        // Station Title and Heading Card with Manual Refresh Button
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF242F3D)),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.EvStation,
                    contentDescription = "Station Monitor Icon",
                    tint = Color(0xFF5288C1),
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stationName,
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "⛽ Live Fuel Tank & GPS Tracker Telemetry",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.LightGray,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Button(
                    onClick = {
                        if (!isRefreshing) {
                            isRefreshing = true
                            viewModel.refreshSensorDataManually()
                            coroutineScope.launch {
                                delay(600)
                                isRefreshing = false
                            }
                        }
                    },
                    enabled = !isRefreshing,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF5288C1),
                        disabledContainerColor = Color(0xFF2E3D52)
                    ),
                    shape = RoundedCornerShape(50.dp),
                    modifier = Modifier.height(40.dp)
                ) {
                    if (isRefreshing) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Sakaahaa Jira...", color = Color.White, style = MaterialTheme.typography.labelLarge)
                    } else {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh Icon",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Sakaahi (Refresh)", color = Color.White, style = MaterialTheme.typography.labelLarge)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Dual Dashboard Information Cards Row (Fuel Level and GPS Finder Tracker)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF242F3D)),
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Unloked Tank Level", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = String.format(Locale.US, "%,.2f L", latestFuel),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4CAF50)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { (latestFuel / 10000.0).toFloat().coerceIn(0f, 1f) },
                        modifier = Modifier.fillMaxWidth().height(6.dp),
                        color = Color(0xFF4CAF50),
                        trackColor = Color.DarkGray
                    )
                }
            }

            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF242F3D)),
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Sefu Tracker GPS", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = String.format(Locale.US, "%.1f km", latestGps),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF00BCD4)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { (latestGps / 100.0).toFloat().coerceIn(0f, 1f) },
                        modifier = Modifier.fillMaxWidth().height(6.dp),
                        color = Color(0xFF00BCD4),
                        trackColor = Color.DarkGray
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Live Log Feed Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.GraphicEq,
                contentDescription = "Log Stream",
                tint = Color(0xFFE1B12C),
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "Telemetrii Haaraa Duubatti Kuusame (Historical logs)",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = Color.LightGray
            )
        }

        if (sensorLogs.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color(0xFF5288C1))
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(sensorLogs) { log ->
                    val isFuel = log.sensorType == "FUEL_LEVEL"
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1B2735)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = if (isFuel) Icons.Default.EvStation else Icons.Default.GpsFixed,
                                    contentDescription = log.sensorType,
                                    tint = if (isFuel) Color(0xFF4CAF50) else Color(0xFF00BCD4),
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isFuel) "FUEL TANK LEVEL" else "GPS FINDER TRACKER",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                            Text(
                                text = if (isFuel) {
                                    String.format(Locale.US, "%,.2f Liters", log.sensorValue)
                                } else {
                                    String.format(Locale.US, "%,.2f km", log.sensorValue)
                                },
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (isFuel) Color(0xFF4CAF50) else Color(0xFF00BCD4)
                            )
                        }
                    }
                }
            }
        }
    }
}
