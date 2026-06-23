package com.example.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import com.example.data.FuelInventory
import com.example.data.MadiyaReportGenerator
import kotlinx.coroutines.launch
import java.io.File

@Composable
fun MadiyaAnalyticsScreen(
    context: Context,
    inventoryList: List<FuelInventory>,
    viewModel: DashboardViewModel
) {
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    var reportMessage by remember { mutableStateOf("") }

    fun triggerReport(type: String) {
        val resultPath = MadiyaReportGenerator.generateFuelReport(context, inventoryList, type)
        reportMessage = resultPath
        
        if (resultPath.isNotEmpty() && !resultPath.contains("dogoggorri")) {
            coroutineScope.launch {
                val actionResult = snackbarHostState.showSnackbar(
                    message = "📂 PDF uumameera! Gabaasa $type",
                    actionLabel = "View File",
                    duration = SnackbarDuration.Long
                )
                if (actionResult == SnackbarResult.ActionPerformed) {
                    openPdfFile(context, resultPath)
                }
            }
        } else {
            coroutineScope.launch {
                snackbarHostState.showSnackbar(
                    message = "⚠️ Gabaasa uumuu irratti dogoggorri uumame.",
                    duration = SnackbarDuration.Short
                )
            }
        }
    }

    Scaffold(
        containerColor = Color.Transparent,
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                snackbar = { data ->
                    Snackbar(
                        containerColor = Color(0xFF2E3D52),
                        contentColor = Color.White,
                        actionColor = Color(0xFF5288C1),
                        snackbarData = data
                    )
                }
            )
        },
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(paddingValues)
                .padding(vertical = 8.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("📊 Gabaasa fi Xiinxala (Reports & Analytics)", style = MaterialTheme.typography.titleMedium, color = Color.Cyan)
                Spacer(modifier = Modifier.height(10.dp))
                
                Text("Gabaasa bifa PDF tiin uumuun qaama dorgommiif ykn taaksiif salphaatti online ergi.", color = Color.LightGray, style = MaterialTheme.typography.bodySmall)
                Spacer(modifier = Modifier.height(15.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Button 1: Guyyaalichaa
                    Button(
                        onClick = { 
                            triggerReport("Guyyaa")
                        }, 
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5288C1))
                    ) {
                        Text("Guyyaa")
                    }

                    // Button 2: Torbanichaa
                    Button(
                        onClick = { 
                            triggerReport("Torban")
                        }, 
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5288C1))
                    ) {
                        Text("Torban")
                    }

                    // Button 3: Ji'ichaa
                    Button(
                        onClick = { 
                            triggerReport("Ji'a")
                        }, 
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5288C1))
                    ) {
                        Text("Ji'a")
                    }
                }

                if (reportMessage.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(15.dp))
                    Text(
                        text = if (reportMessage.contains("Madiya")) "📂 PDF uumameera! Bakka: $reportMessage" else reportMessage,
                        color = Color.Green,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

private fun openPdfFile(context: Context, filePath: String) {
    try {
        val file = File(filePath)
        if (!file.exists()) {
            Toast.makeText(context, "Failiin hin jiru (File does not exist)", Toast.LENGTH_SHORT).show()
            return
        }
        val uri: Uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/pdf")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        val chooser = Intent.createChooser(intent, "Gabaasa bifa PDF Mula'adhu (Open PDF Report)")
        chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(chooser)
    } catch (e: Exception) {
        e.printStackTrace()
        Toast.makeText(context, "Failii banuu irratti dogoggora: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
    }
}
