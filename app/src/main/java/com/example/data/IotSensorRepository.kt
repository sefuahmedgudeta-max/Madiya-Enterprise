package com.example.data

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlin.random.Random

class IotSensorRepository(private val iotSensorDao: IotSensorDao) {

    val recentLogs: Flow<List<IotSensorLog>> = iotSensorDao.getRecentLogs()
    
    suspend fun getUnsyncedLogs(): List<IotSensorLog> = iotSensorDao.getUnsyncedLogs()
    
    suspend fun markAsSynced(log: IotSensorLog) {
        iotSensorDao.updateLog(log.copy(isSynced = true))
    }

    // Sirna sensorii boba'aa fi tracking duubatti mock gochuuf (Background Simulation)
    suspend fun startBackgroundLogging() {
        while (true) {
            // Simulated Fuel Level (Fkn: Litera 5000 hanga 8000 jiru)
            val currentFuel = Random.nextDouble(5000.0, 8000.0)
            iotSensorDao.insertLog(
                IotSensorLog(sensorType = "FUEL_LEVEL", sensorValue = currentFuel)
            )

            // Simulated Tracking (Sefu Finder distance tracker)
            val currentDistance = Random.nextDouble(0.0, 100.0)
            iotSensorDao.insertLog(
                IotSensorLog(sensorType = "GPS_TRACKER", sensorValue = currentDistance)
            )

            delay(5000) // Sa'aatii 5 hundaatti ragaa haaraa kuusa
        }
    }
}
