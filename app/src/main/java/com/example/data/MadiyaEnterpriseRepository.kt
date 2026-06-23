package com.example.data

import kotlinx.coroutines.flow.MutableStateFlow

data class FuelInventory(
    val bilaalooDhufe: Double,
    val kanGurgurame: Double,
    val tankariiKeessaJiru: Double,
    val timestamp: Long = System.currentTimeMillis()
)

data class TruckTracking(
    val truckId: String,
    val initialFuel: Double,
    val status: String
)

object MadiyaEnterpriseRepository {

    private val RESERVE_PERCENT = 0.02 // 2% Internal Reserve
    val telebirrTransactionStatus = MutableStateFlow("EGGUMSA")

    // Otomaatikiin gurgurtaa irraa reserve herreeguu fi kutuu
    fun processInternalTransaction(dailySalesAmount: Double): Double {
        val reserveToSave = dailySalesAmount * RESERVE_PERCENT
        
        // Simulation: Telebirr API Call dhimma kaffaltii
        // Real App keessatti API Telebirr itti hidhama
        println("TELEBIRR: Reserve $reserveToSave ETB gurgurtaa $dailySalesAmount irraa otomaatikiin citeera.")
        telebirrTransactionStatus.value = "MILKAA'EERA"
        
        return reserveToSave
    }

    // Ragaa Biiroo Raabsaa Boba'aa Madiyaaf erguu (Online Sync)
    fun syncDataWithMadiyaLogistics(inventory: FuelInventory) {
        // API Madiya Logisitics asitti waamama
        println("MADIYA SYNC: Ragaan tankarii (${inventory.tankariiKeessaJiru} L) Madiyaaf online darbeera.")
    }
}
