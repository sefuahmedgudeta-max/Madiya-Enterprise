package com.example.ui

import android.app.Application
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.Environment
import android.os.StatFs
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.example.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.File

data class SensorTelemetry(
    val compassAzimuth: Float = 0f,
    val pitch: Float = 0f,
    val roll: Float = 0f,
    val sensorActive: Boolean = false
)

data class GPSLocationData(
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val altitude: Double = 0.0,
    val accuracy: Float = 0f,
    val provider: String = "None",
    val hasPermission: Boolean = false
)

data class DeviceMetrics(
    val freeStorageGB: Double = 0.0,
    val totalStorageGB: Double = 0.0,
    val storagePercentUsed: Float = 0f,
    val batteryLevelPercent: Int = 100,
    val batteryTempCelsius: Float = 0f
)

sealed interface AIChatUiState {
    object Idle : AIChatUiState
    object Loading : AIChatUiState
    data class Success(val message: String) : AIChatUiState
    data class Error(val error: String) : AIChatUiState
}

data class PaymentRequest(
    val amount: Double,
    val phoneNumber: String,
    val currency: String = "ETB"
)

sealed interface PaymentUiState {
    object Idle : PaymentUiState
    object Processing : PaymentUiState
    data class Success(val orderRef: String, val amount: Double) : PaymentUiState
    data class Error(val message: String) : PaymentUiState
}

class DashboardViewModel(
    application: Application,
    private val repository: DashboardRepository
) : AndroidViewModel(application), SensorEventListener {

    // --- State flows ---
    val tasks: StateFlow<List<TaskEntity>> = repository.allTasks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val notes: StateFlow<List<NoteEntity>> = repository.allNotes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())


    val payments: StateFlow<List<PaymentEntity>> = repository.allPayments
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val cipherKeys: StateFlow<List<CipherKeyEntity>> = repository.allCipherKeys
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val iotLogs: StateFlow<List<IotLog>> = repository.recentIotLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val iotSensorRepository = IotSensorRepository(repository.iotSensorDao)

    val recentLogs: StateFlow<List<IotSensorLog>> = iotSensorRepository.recentLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val fuelInventoryList: StateFlow<List<FuelInventory>> = recentLogs.map { logs ->
        logs.filter { it.sensorType == "FUEL_LEVEL" }.map { log ->
            val tankLeft = log.sensorValue
            val dhufe = 8000.0
            val gurgurame = (dhufe - tankLeft).coerceAtLeast(0.0)
            FuelInventory(
                bilaalooDhufe = dhufe,
                kanGurgurame = gurgurame,
                tankariiKeessaJiru = tankLeft,
                timestamp = log.timestamp
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val truckList: StateFlow<List<TruckTracking>> = recentLogs.map { logs ->
        val gpsLogs = logs.filter { it.sensorType == "GPS_TRACKER" }
        if (gpsLogs.isEmpty()) {
            listOf(
                TruckTracking("TRK-A1", 4500.0, "DELEVERY_ON_ROUTE"),
                TruckTracking("TRK-B2", 3200.0, "COMPLETED")
            )
        } else {
            gpsLogs.mapIndexed { index, gpsLog ->
                val id = "TRK-0${index + 1}"
                val status = if (gpsLog.sensorValue < 30.0) "COMPLETED" else if (gpsLog.sensorValue < 70.0) "APPROACHING" else "EN_ROUTE"
                TruckTracking(
                    truckId = id,
                    initialFuel = (5000.0 - (gpsLog.sensorValue * 15)).coerceAtLeast(500.0),
                    status = status
                )
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())


    private val _aiChatState = MutableStateFlow<AIChatUiState>(AIChatUiState.Idle)
    val aiChatState: StateFlow<AIChatUiState> = _aiChatState.asStateFlow()

    private val _paymentState = MutableStateFlow<PaymentUiState>(PaymentUiState.Idle)
    val paymentState: StateFlow<PaymentUiState> = _paymentState.asStateFlow()

    // Ragaa kaffaltii maamilaa hordofuuf
    val isPremium: StateFlow<Boolean> = PremiumRepository.isPremiumUser

    fun startPremiumUpgrade(phoneNumber: String) {
        viewModelScope.launch {
            // Kaffaltii Birrii 50 (kaffaltii xiqqoo) Telebirr/Chapa'n gaafachuu
            val checkoutUrl = PremiumRepository.initiatePayment(phoneNumber, 50.0)
            
            // Replit ykn Real App keessatti weblink kaffaltii kana maamilaaf banna
            println("Maamila gara kaffaltiitti fidi: $checkoutUrl")
            
            // Fakkeenyaaf (Simulation) kaffaltiin daqiiqaa booda milkaa'eera jedhee akka eegu gochuuf:
            PremiumRepository.verifyPaymentSuccess("SAFUU-MOCK-123")
        }
    }

    fun upgradeToPremium() {
        PremiumRepository.verifyPaymentSuccess("SAFUU-UPGRADE")
    }

    fun resetPremiumForDemo() {
        PremiumRepository.demoteToFreeForDemo()
    }

    // --- Device Telemetry States ---
    private val _sensorTelemetry = MutableStateFlow(SensorTelemetry())
    val sensorTelemetry: StateFlow<SensorTelemetry> = _sensorTelemetry.asStateFlow()

    private val _locationData = MutableStateFlow(GPSLocationData())
    val locationData: StateFlow<GPSLocationData> = _locationData.asStateFlow()

    private val _deviceMetrics = MutableStateFlow(DeviceMetrics())
    val deviceMetrics: StateFlow<DeviceMetrics> = _deviceMetrics.asStateFlow()

    // Hardware Sensors
    private val sensorManager = application.getSystemService(Context.SENSOR_SERVICE) as? SensorManager
    private val accelerometer = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    private val magnetometer = sensorManager?.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)

    private val gravityData = FloatArray(3)
    private val magneticData = FloatArray(3)
    private var hasGravity = false
    private var hasMagnetic = false

    // Location Manager
    private val locationManager = application.getSystemService(Context.LOCATION_SERVICE) as? LocationManager

    private val locationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            _locationData.update {
                it.copy(
                    latitude = location.latitude,
                    longitude = location.longitude,
                    altitude = location.altitude,
                    accuracy = location.accuracy,
                    provider = location.provider ?: "Device GPS"
                )
            }
            insertIotLog("TRACKER_GPS", location.latitude)
        }
        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}
        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
    }

    init {
        // Start live measurements
        registerSensors()
        updateDeviceStorageMetrics()
        
        // Start simulated IoT sensor background logging
        viewModelScope.launch {
            iotSensorRepository.startBackgroundLogging()
        }

        // AUTO-SYNC ON START
        viewModelScope.launch {
            syncUnsyncedData()
        }
    }

    private suspend fun syncUnsyncedData() {
        // 1. Sync Payments
        val unsyncedPayments = repository.allPayments.first().filter { !it.isSynced }
        for (payment in unsyncedPayments) {
            // Re-attempt API call
            try {
                PremiumRepository.verifyPaymentSuccess(payment.txRef)
                repository.insertPayment(payment.copy(isSynced = true))
            } catch (e: Exception) {
                // Keep trying later
            }
        }
        
        // 2. Sync IoT Logs
        val unsyncedLogs = iotSensorRepository.getUnsyncedLogs()
        for (log in unsyncedLogs) {
            // Sync to server (simulated)
            iotSensorRepository.markAsSynced(log)
        }
    }


    private val _forecast = MutableStateFlow<String?>(null)
    val forecast: StateFlow<String?> = _forecast

    fun getFuelForecast(data: String) {
        viewModelScope.launch {
            _forecast.value = "Tilmaamaa jira..."
            _forecast.value = ForecastingService.getFuelForecast(data)
        }
    }

    fun refreshSensorDataManually() {
        viewModelScope.launch {
            val currentFuel = kotlin.random.Random.nextDouble(5000.0, 8000.0)
            repository.insertIotSensorLog(
                IotSensorLog(sensorType = "FUEL_LEVEL", sensorValue = currentFuel)
            )

            val currentDistance = kotlin.random.Random.nextDouble(0.0, 100.0)
            repository.insertIotSensorLog(
                IotSensorLog(sensorType = "GPS_TRACKER", sensorValue = currentDistance)
            )
        }
    }

    fun triggerDailySales(salesAmount: Double) {
        viewModelScope.launch {
            MadiyaEnterpriseRepository.processInternalTransaction(salesAmount)
            // also trigger an insert to live logging with simulated lower fuel corresponding to sales
            val latestLogs = iotSensorRepository.recentLogs.firstOrNull() ?: emptyList()
            val currentFuelVal = latestLogs.firstOrNull { it.sensorType == "FUEL_LEVEL" }?.sensorValue ?: 6500.0
            val reductionVal = (salesAmount / 90.0).coerceAtMost(3000.0) // approx 1L per 90 ETB
            val newFuelValue = (currentFuelVal - reductionVal).coerceAtLeast(100.0)
            repository.insertIotSensorLog(
                IotSensorLog(sensorType = "FUEL_LEVEL", sensorValue = newFuelValue)
            )
        }
    }

    // --- Database Operations ---
    fun addTask(title: String) {
        if (title.isBlank()) return
        viewModelScope.launch {
            repository.insertTask(TaskEntity(title = title, isCompleted = false))
        }
    }

    fun toggleTask(task: TaskEntity) {
        viewModelScope.launch {
            repository.updateTask(task.copy(isCompleted = !task.isCompleted))
        }
    }

    fun deleteTask(task: TaskEntity) {
        viewModelScope.launch {
            repository.deleteTask(task)
        }
    }

    fun clearAllCompletedTasks() {
        viewModelScope.launch {
            tasks.value.filter { it.isCompleted }.forEach {
                repository.deleteTask(it)
            }
        }
    }

    fun addNote(title: String, content: String, colorHex: String) {
        if (title.isBlank() && content.isBlank()) return
        viewModelScope.launch {
            repository.insertNote(NoteEntity(title = title, content = content, colorHex = colorHex))
        }
    }

    fun deleteNote(note: NoteEntity) {
        viewModelScope.launch {
            repository.deleteNote(note)
        }
    }

    // --- Security Analysis States ---
    private val _securityAnalysisState = MutableStateFlow<AIChatUiState>(AIChatUiState.Idle)
    val securityAnalysisState: StateFlow<AIChatUiState> = _securityAnalysisState.asStateFlow()

    fun runSecurityAnalysis() {
        viewModelScope.launch {
            _securityAnalysisState.value = AIChatUiState.Loading

            try {
                // Fetch recent logs to analyze
                val logs = iotSensorRepository.recentLogs.firstOrNull() ?: emptyList()
                val logsData = logs.joinToString("\n") { "${it.timestamp}: ${it.sensorType} = ${it.sensorValue}" }

                val analysis = repository.analyzeSecurity(logsData)
                _securityAnalysisState.value = AIChatUiState.Success(analysis)
            } catch (e: Exception) {
                _securityAnalysisState.value = AIChatUiState.Error(e.message ?: "Analysis failed.")
            }
        }
    }



    fun insertIotLog(sensorType: String, sensorValue: Double) {
        viewModelScope.launch {
            repository.insertIotLog(
                IotLog(
                    sensorType = sensorType,
                    sensorValue = sensorValue
                )
            )
        }
    }

    // --- Telebirr & Chapa Payments ---
    fun initiateTelebirrPayment(request: PaymentRequest) {
        if (request.phoneNumber.isBlank()) {
            _paymentState.value = PaymentUiState.Error("Lakkoofsa bilbilaa guutuun dirqama!")
            return
        }
        if (request.amount <= 0.0) {
            _paymentState.value = PaymentUiState.Error("Maallaqni kaffalamu 0 irra caaluu qaba!")
            return
        }

        viewModelScope.launch {
            _paymentState.value = PaymentUiState.Processing
            
            // Initiate payment via PremiumRepository
            val checkoutUrl = PremiumRepository.initiatePayment(request.phoneNumber, request.amount)
            val txRef = checkoutUrl.substringAfterLast("/")
            
            val pendingPayment = PaymentEntity(
                amount = request.amount,
                phoneNumber = request.phoneNumber,
                currency = request.currency,
                status = "PENDING",
                txRef = txRef,
                isSynced = false
            )
            repository.insertPayment(pendingPayment)
            
            // Simulate network processing with Chapa/Telebirr APIs
            kotlinx.coroutines.delay(2000)
            
            // Verify payment on PremiumRepository
            try {
                PremiumRepository.verifyPaymentSuccess(txRef)
                val successPayment = pendingPayment.copy(status = "SUCCESS", isSynced = true)
                repository.insertPayment(successPayment)
                _paymentState.value = PaymentUiState.Success(txRef, request.amount)
            } catch (e: Exception) {
                // Keep as PENDING/isSynced=false
                _paymentState.value = PaymentUiState.Error("Kaffaltiin hin milkaa'e...")
            }
        }
    }

    fun resetPaymentUi() {
        _paymentState.value = PaymentUiState.Idle
    }

    fun clearPaymentHistory() {
        viewModelScope.launch {
            repository.clearPaymentHistory()
        }
    }

    fun insertKey(identifier: String) {
        viewModelScope.launch {
            repository.insertCipherKey(CipherKeyEntity(identifier = identifier))
        }
    }


    // --- Location and Telemetry updates ---
    fun setLocationPermissionGranted(granted: Boolean) {
        _locationData.update { it.copy(hasPermission = granted) }
        if (granted) {
            requestLiveLocationUpdates()
        }
    }

    private fun requestLiveLocationUpdates() {
        try {
            locationManager?.let { mgr ->
                var provider = LocationManager.NETWORK_PROVIDER
                if (mgr.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    provider = LocationManager.GPS_PROVIDER
                }
                
                // Get last known location for quick startup preview
                val lastKnown = mgr.getLastKnownLocation(provider)
                if (lastKnown != null) {
                    _locationData.update {
                        it.copy(
                            latitude = lastKnown.latitude,
                            longitude = lastKnown.longitude,
                            altitude = lastKnown.altitude,
                            accuracy = lastKnown.accuracy,
                            provider = lastKnown.provider ?: "Last Known"
                        )
                    }
                }

                // Register listener with dynamic providers on main dispatchers
                mgr.requestLocationUpdates(provider, 2000L, 1f, locationListener)
            }
        } catch (e: SecurityException) {
            _locationData.update { it.copy(provider = "Permission Denied") }
        } catch (e: Exception) {
            _locationData.update { it.copy(provider = "Unavailable") }
        }
    }

    // --- Sensors telemetry logic ---
    private fun registerSensors() {
        sensorManager?.let { manager ->
            accelerometer?.let { manager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI) }
            magnetometer?.let { manager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI) }
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null) return
        when (event.sensor.type) {
            Sensor.TYPE_ACCELEROMETER -> {
                System.arraycopy(event.values, 0, gravityData, 0, 3)
                hasGravity = true
            }
            Sensor.TYPE_MAGNETIC_FIELD -> {
                System.arraycopy(event.values, 0, magneticData, 0, 3)
                hasMagnetic = true
            }
        }

        if (hasGravity && hasMagnetic) {
            val rotationMatrix = FloatArray(9)
            val inclineMatrix = FloatArray(9)
            if (SensorManager.getRotationMatrix(rotationMatrix, inclineMatrix, gravityData, magneticData)) {
                val orientations = FloatArray(3)
                SensorManager.getOrientation(rotationMatrix, orientations)
                
                val azimuthRad = orientations[0]
                val pitchRad = orientations[1]
                val rollRad = orientations[2]

                // Convert radians to degrees
                var azimuthDeg = Math.toDegrees(azimuthRad.toDouble()).toFloat()
                if (azimuthDeg < 0) azimuthDeg += 360f

                val pitchDeg = Math.toDegrees(pitchRad.toDouble()).toFloat()
                val rollDeg = Math.toDegrees(rollRad.toDouble()).toFloat()

                _sensorTelemetry.update {
                    SensorTelemetry(
                        compassAzimuth = azimuthDeg,
                        pitch = pitchDeg,
                        roll = rollDeg,
                        sensorActive = true
                    )
                }
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    // --- Update battery metrics directly from MainActivity callback ---
    fun updateBatteryMetrics(level: Int, temperature: Float) {
        _deviceMetrics.update {
            it.copy(
                batteryLevelPercent = level,
                batteryTempCelsius = temperature
            )
        }
    }

    // --- Device storage queries ---
    fun updateDeviceStorageMetrics() {
        viewModelScope.launch {
            try {
                val path = Environment.getDataDirectory()
                val stat = StatFs(path.path)
                val blockSize = stat.blockSizeLong
                val totalBlocks = stat.blockCountLong
                val availableBlocks = stat.availableBlocksLong

                val totalStorageBytes = totalBlocks * blockSize
                val availableStorageBytes = availableBlocks * blockSize
                val usedStorageBytes = totalStorageBytes - availableStorageBytes

                val totalGB = totalStorageBytes.toDouble() / (1024 * 1024 * 1024)
                val freeGB = availableStorageBytes.toDouble() / (1024 * 1024 * 1024)
                val percentUsed = if (totalStorageBytes > 0) {
                    (usedStorageBytes.toFloat() / totalStorageBytes.toFloat()) * 100f
                } else 0f

                _deviceMetrics.update {
                    it.copy(
                        freeStorageGB = freeGB,
                        totalStorageGB = totalGB,
                        storagePercentUsed = percentUsed
                    )
                }
            } catch (e: Exception) {
                // Safe fallback ignore
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        // Prevent memory leaks
        sensorManager?.unregisterListener(this)
        try {
            locationManager?.removeUpdates(locationListener)
        } catch (e: Exception) {}
    }
}

class DashboardViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val database = Room.databaseBuilder(
            application.applicationContext,
            AppDatabase::class.java,
            "ai_assistant_dashboard_db"
        )
            .fallbackToDestructiveMigration()
            .build()
        
        val repository = DashboardRepository(
            database.dashboardDao(),
            database.iotLogDao(),
            database.iotSensorDao(),
            database.cipherKeyDao()
        )
        @Suppress("UNCHECKED_CAST")
        return DashboardViewModel(application, repository) as T
    }
}
