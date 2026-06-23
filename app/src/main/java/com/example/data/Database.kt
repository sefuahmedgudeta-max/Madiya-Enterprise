package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "cipher_keys")
data class CipherKeyEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val identifier: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Dao
interface CipherKeyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertKey(key: CipherKeyEntity)
    
    @Query("SELECT * FROM cipher_keys ORDER BY createdAt DESC")
    fun getAllKeys(): Flow<List<CipherKeyEntity>>
}

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val isCompleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "notes")
data class NoteEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val content: String,
    val colorHex: String, // Hex string or identifier for note card backgrounds
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val sender: String, // "user" or "assistant"
    val message: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "payments")
data class PaymentEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val amount: Double,
    val phoneNumber: String,
    val currency: String = "ETB",
    val status: String, // PENDING, SUCCESS, FAILED
    val txRef: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isSynced: Boolean = false // Offline Cache Support
)

@Dao
interface DashboardDao {
    // Tasks
    @Query("SELECT * FROM tasks ORDER BY createdAt DESC")
    fun getAllTasks(): Flow<List<TaskEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskEntity)

    @Update
    suspend fun updateTask(task: TaskEntity)

    @Delete
    suspend fun deleteTask(task: TaskEntity)

    @Query("DELETE FROM tasks")
    suspend fun deleteAllTasks()

    // Notes
    @Query("SELECT * FROM notes ORDER BY createdAt DESC")
    fun getAllNotes(): Flow<List<NoteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: NoteEntity)

    @Delete
    suspend fun deleteNote(note: NoteEntity)

    // Chat History
    @Query("SELECT * FROM chat_messages ORDER BY timestamp ASC")
    fun getAllChatMessages(): Flow<List<ChatMessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChatMessage(message: ChatMessageEntity)

    @Query("DELETE FROM chat_messages")
    suspend fun clearChatHistory()

    // Payments
    @Query("SELECT * FROM payments ORDER BY timestamp DESC")
    fun getAllPayments(): Flow<List<PaymentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPayment(payment: PaymentEntity)

    @Query("DELETE FROM payments")
    suspend fun clearPaymentHistory()
}

@Database(entities = [
    TaskEntity::class, NoteEntity::class, ChatMessageEntity::class, PaymentEntity::class, IotLog::class, IotSensorLog::class, ShiftEntity::class, FleetCardEntity::class, MaintenanceTicketEntity::class, UserEntity::class, FuelReconciliationEntity::class, LegalDocumentEntity::class, LoyaltyCardEntity::class, HourlySaleEntity::class, PumpAlertEntity::class, ShiftHandoverEntity::class, GeneratorLogEntity::class, WalletEntity::class, QualityLogEntity::class, MarketIntelligenceEntity::class, LprLogEntity::class, SystemHealthEntity::class, NfcTagEntity::class, InvestmentEntity::class, EvaporationLogEntity::class, PerimeterAlertEntity::class, SelfServiceTokenEntity::class, CenterStationEntity::class, UssdPaymentEntity::class, SolarLogEntity::class, TankerGpsEntity::class, RationingEntity::class, FraudCheckEntity::class, WaterInfiltrationEntity::class, PriorityVehicleEntity::class, TariffEntity::class, ShiftExpenseEntity::class, BankSettlementEntity::class, BulkOrderEntity::class, FuelQualityLogEntity::class, QueueEntryEntity::class, FarmerRationEntity::class, TransactionRollbackEntity::class, CipherKeyEntity::class
], version = 17, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun dashboardDao(): DashboardDao
    abstract fun iotLogDao(): IotLogDao
    abstract fun iotSensorDao(): IotSensorDao
    abstract fun shiftDao(): ShiftDao
    abstract fun fleetCardDao(): FleetCardDao
    abstract fun maintenanceTicketDao(): MaintenanceTicketDao
    abstract fun userDao(): UserDao
    abstract fun fuelReconciliationDao(): FuelReconciliationDao
    abstract fun legalDocumentDao(): LegalDocumentDao
    abstract fun loyaltyDao(): LoyaltyDao
    abstract fun hourlySaleDao(): HourlySaleDao
    abstract fun pumpAlertDao(): PumpAlertDao
    abstract fun shiftHandoverDao(): ShiftHandoverDao
    abstract fun generatorDao(): GeneratorDao
    abstract fun walletDao(): WalletDao
    abstract fun qualityDao(): QualityDao
    abstract fun marketDao(): MarketDao
    abstract fun lprDao(): LprDao
    abstract fun systemHealthDao(): SystemHealthDao
    abstract fun nfcDao(): NfcDao
    abstract fun investmentDao(): InvestmentDao
    abstract fun evaporationDao(): EvaporationDao
    abstract fun perimeterDao(): PerimeterDao
    abstract fun selfServiceDao(): SelfServiceDao
    abstract fun centerStationDao(): CenterStationDao
    abstract fun ussdPaymentDao(): UssdPaymentDao
    abstract fun solarDao(): SolarDao
    abstract fun tankerDao(): TankerDao
    abstract fun rationingDao(): RationingDao
    abstract fun fraudDao(): FraudDao
    abstract fun waterDao(): WaterDao
    abstract fun priorityVehicleDao(): PriorityVehicleDao
    abstract fun tariffDao(): TariffDao
    abstract fun shiftExpenseDao(): ShiftExpenseDao
    abstract fun settlementDao(): BankSettlementDao
    abstract fun bulkOrderDao(): BulkOrderDao
    abstract fun qualitySensorDao(): QualitySensorDao
    abstract fun queueDao(): QueueDao
    abstract fun farmerRationDao(): FarmerRationDao
    abstract fun transactionRollbackDao(): TransactionRollbackDao
    abstract fun cipherKeyDao(): CipherKeyDao
}

@Entity(tableName = "iot_telemetry_logs")
data class IotLog(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val sensorType: String, // "FUEL_LEVEL", "TRACKER_GPS"
    val sensorValue: Double
)

@Dao
interface IotLogDao {
    @Insert
    suspend fun insertLog(log: IotLog)

    @Query("SELECT * FROM iot_telemetry_logs ORDER BY timestamp DESC LIMIT 50")
    fun getRecentLogs(): Flow<List<IotLog>>
}

@Entity(tableName = "iot_sensor_logs")
data class IotSensorLog(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val sensorType: String, // "FUEL_LEVEL" ykn "GPS_TRACKER"
    val sensorValue: Double, // Fkn: Litera boba'aa ykn fageenya tracking
    val stationName: String = "Madiya Gas Station", // Maqaa buufata keetii
    val isSynced: Boolean = false // Offline Cache Support
)

@Entity(tableName = "shift_logs")
data class ShiftEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val employeeId: String,
    val startTime: Long,
    val endTime: Long? = null,
    val totalSales: Double = 0.0,
    val fuelSold: Double = 0.0
)

@Entity(tableName = "fleet_cards")
data class FleetCardEntity(
    @PrimaryKey val cardId: String,
    val vehicleId: String,
    val limitAmount: Double,
    val usedAmount: Double
)

@Entity(tableName = "maintenance_tickets")
data class MaintenanceTicketEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val sensorId: String,
    val issueType: String,
    val status: String // "OPEN", "IN_PROGRESS", "CLOSED"
)

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val userId: String,
    val name: String,
    val role: String // "MANAGER", "SUPERVISOR", "CASHIER"
)

@Entity(tableName = "fuel_reconciliation")
data class FuelReconciliationEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val tankReading: Double,
    val expectedSales: Double,
    val actualSales: Double,
    val variance: Double
)

@Entity(tableName = "legal_documents")
data class LegalDocumentEntity(
    @PrimaryKey val id: String,
    val name: String,
    val expiryDate: Long
)

@Entity(tableName = "loyalty_cards")
data class LoyaltyCardEntity(@PrimaryKey val userId: String, val points: Int)

@Entity(tableName = "hourly_sales")
data class HourlySaleEntity(@PrimaryKey(autoGenerate = true) val id: Int = 0, val timestamp: Long, val amount: Double)

@Entity(tableName = "pump_alerts")
data class PumpAlertEntity(@PrimaryKey(autoGenerate = true) val id: Int = 0, val pumpId: String, val alertType: String, val status: String)

@Entity(tableName = "shift_handovers")
data class ShiftHandoverEntity(@PrimaryKey(autoGenerate = true) val id: Int = 0, val fromUser: String, val toUser: String, val balance: Double, val notes: String)

@Entity(tableName = "generator_logs")
data class GeneratorLogEntity(@PrimaryKey(autoGenerate = true) val id: Int = 0, val status: String, val fuelLevel: Double)

@Entity(tableName = "wallets")
data class WalletEntity(@PrimaryKey val userId: String, val balance: Double)

@Entity(tableName = "quality_logs")
data class QualityLogEntity(@PrimaryKey(autoGenerate = true) val id: Int = 0, val octaneLevel: Double)

@Entity(tableName = "market_intel")
data class MarketIntelligenceEntity(@PrimaryKey val id: Int = 0, val competitorName: String, val price: Double)

@Entity(tableName = "lpr_logs")
data class LprLogEntity(@PrimaryKey(autoGenerate = true) val id: Int = 0, val plateNumber: String, val timestamp: Long)

@Entity(tableName = "system_health")
data class SystemHealthEntity(@PrimaryKey val id: Int = 0, val status: String, val lastReboot: Long)

@Entity(tableName = "nfc_tags")
data class NfcTagEntity(@PrimaryKey val tagId: String, val customerName: String)

@Entity(tableName = "investments")
data class InvestmentEntity(@PrimaryKey(autoGenerate = true) val id: Int = 0, val assetName: String, val cost: Double, val date: Long)

@Entity(tableName = "evaporation_logs")
data class EvaporationLogEntity(@PrimaryKey(autoGenerate = true) val id: Int = 0, val shrinkageAmount: Double, val timestamp: Long)

@Entity(tableName = "perimeter_alerts")
data class PerimeterAlertEntity(@PrimaryKey(autoGenerate = true) val id: Int = 0, val alertType: String, val timestamp: Long)

@Entity(tableName = "self_service_tokens")
data class SelfServiceTokenEntity(@PrimaryKey val phone: String, val token: String)

@Entity(tableName = "center_stations")
data class CenterStationEntity(@PrimaryKey val stationId: String, val stationName: String, val revenue: Double)

@Entity(tableName = "ussd_payments")
data class UssdPaymentEntity(@PrimaryKey(autoGenerate = true) val id: Int = 0, val ref: String, val amount: Double)

@Entity(tableName = "solar_logs")
data class SolarLogEntity(@PrimaryKey(autoGenerate = true) val id: Int = 0, val batteryLevel: Int, val source: String)

@Entity(tableName = "tanker_gps")
data class TankerGpsEntity(@PrimaryKey val tankerId: String, val lastLat: Double, val lastLon: Double, val isSealed: Boolean)

@Entity(tableName = "rationing_logs")
data class RationingEntity(@PrimaryKey val stationId: String, val dailyLimit: Double, val stockRemaining: Double)

@Entity(tableName = "fraud_checks")
data class FraudCheckEntity(@PrimaryKey(autoGenerate = true) val id: Int = 0, val txnId: String, val amount: Double, val isVerified: Boolean)

@Entity(tableName = "water_infiltration")
data class WaterInfiltrationEntity(@PrimaryKey(autoGenerate = true) val id: Int = 0, val waterLevel: Double, val siltLevel: Double, val timestamp: Long)

@Entity(tableName = "priority_vehicles")
data class PriorityVehicleEntity(@PrimaryKey(autoGenerate=true) val id: Int = 0, val plateNumber: String, val vehicleType: String, val priority: Int)

@Entity(tableName = "tariffs")
data class TariffEntity(@PrimaryKey val id: Int = 0, val price: Double, val stationId: String)

@Entity(tableName = "shift_expenses")
data class ShiftExpenseEntity(@PrimaryKey(autoGenerate = true) val id: Int = 0, val shiftId: String, val amount: Double, val reason: String)

@Entity(tableName = "bank_settlements")
data class BankSettlementEntity(@PrimaryKey(autoGenerate = true) val id: Int = 0, val bankName: String, val amount: Double, val txId: String)

@Entity(tableName = "bulk_orders")
data class BulkOrderEntity(@PrimaryKey(autoGenerate = true) val id: Int = 0, val depotId: String, val quantity: Double, val status: String)

@Entity(tableName = "fuel_quality_logs")
data class FuelQualityLogEntity(@PrimaryKey(autoGenerate = true) val id: Int = 0, val density: Double, val qualityStatus: String, val timestamp: Long)

@Entity(tableName = "queue_entries")
data class QueueEntryEntity(@PrimaryKey(autoGenerate = true) val id: Int = 0, val token: String, val pumpId: String)

@Entity(tableName = "farmer_rations")
data class FarmerRationEntity(@PrimaryKey val farmerId: String, val jerrycanLimit: Double, val authorizedBy: String)

@Entity(tableName = "transaction_rollbacks")
data class TransactionRollbackEntity(@PrimaryKey val txnId: String, val amount: Double, val wasRolledBack: Boolean)



@Dao
interface IotSensorDao {
    @Insert
    suspend fun insertLog(log: IotSensorLog)

    // Ragaa dhumaa 50 kan yeroo dhihoo fiduuf
    @Query("SELECT * FROM iot_sensor_logs ORDER BY timestamp DESC LIMIT 50")
    fun getRecentLogs(): Flow<List<IotSensorLog>>
    
    // Sadarkaa boba'aa isa dhumaa qofa fiduuf
    @Query("SELECT * FROM iot_sensor_logs WHERE sensorType = :type ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLatestSensorValue(type: String): IotSensorLog?
    
    @Query("SELECT * FROM iot_sensor_logs WHERE isSynced = 0")
    suspend fun getUnsyncedLogs(): List<IotSensorLog>

    @Update
    suspend fun updateLog(log: IotSensorLog)
}

@Dao
interface ShiftDao {
    @Insert
    suspend fun insertShift(shift: ShiftEntity)

    @Query("SELECT * FROM shift_logs WHERE endTime IS NULL ORDER BY startTime DESC LIMIT 1")
    suspend fun getActiveShift(): ShiftEntity?

    @Update
    suspend fun updateShift(shift: ShiftEntity)
}

@Dao
interface FleetCardDao {
    @Insert
    suspend fun insertFleetCard(card: FleetCardEntity)

    @Update
    suspend fun updateFleetCard(card: FleetCardEntity)
}

@Dao
interface MaintenanceTicketDao {
    @Insert
    suspend fun insertTicket(ticket: MaintenanceTicketEntity)

    @Query("SELECT * FROM maintenance_tickets WHERE status != 'CLOSED'")
    suspend fun getOpenTickets(): List<MaintenanceTicketEntity>

    @Update
    suspend fun updateTicket(ticket: MaintenanceTicketEntity)
}

@Dao
interface UserDao {
    @Insert
    suspend fun insertUser(user: UserEntity)
    
    @Query("SELECT * FROM users WHERE userId = :userId")
    suspend fun getUser(userId: String): UserEntity?
}

@Dao
interface FuelReconciliationDao {
    @Insert
    suspend fun insertReconciliation(recon: FuelReconciliationEntity)
}

@Dao
interface LegalDocumentDao {
    @Insert
    suspend fun insertDocument(document: LegalDocumentEntity)

    @Query("SELECT * FROM legal_documents")
    suspend fun getAllDocuments(): List<LegalDocumentEntity>
}

@Dao
interface LoyaltyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updatePoints(card: LoyaltyCardEntity)
    @Query("SELECT * FROM loyalty_cards WHERE userId = :userId")
    suspend fun getLoyaltyCard(userId: String): LoyaltyCardEntity?
}

@Dao
interface HourlySaleDao {
    @Insert
    suspend fun insertSale(sale: HourlySaleEntity)
    @Query("SELECT * FROM hourly_sales")
    suspend fun getSales(): List<HourlySaleEntity>
}

@Dao
interface PumpAlertDao {
    @Insert
    suspend fun insertAlert(alert: PumpAlertEntity)
    @Query("SELECT * FROM pump_alerts WHERE status = 'OPEN'")
    suspend fun getActiveAlerts(): List<PumpAlertEntity>
}

@Dao
interface ShiftHandoverDao {
    @Insert
    suspend fun insertHandover(handover: ShiftHandoverEntity)
}

@Dao
interface GeneratorDao {
    @Insert
    suspend fun insertLog(log: GeneratorLogEntity)
}

@Dao
interface WalletDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateBalance(wallet: WalletEntity)
    @Query("SELECT * FROM wallets WHERE userId = :userId")
    suspend fun getWallet(userId: String): WalletEntity?
}

@Dao
interface QualityDao {
    @Insert
    suspend fun insertLog(log: QualityLogEntity)
}

@Dao
interface MarketDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateMarketPrice(intel: MarketIntelligenceEntity)
    @Query("SELECT * FROM market_intel")
    suspend fun getMarketData(): List<MarketIntelligenceEntity>
}

@Dao
interface LprDao {
    @Insert
    suspend fun insertLog(log: LprLogEntity)
}

@Dao
interface SystemHealthDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateStatus(health: SystemHealthEntity)
}

@Dao
interface NfcDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun registerTag(tag: NfcTagEntity)
}

@Dao
interface InvestmentDao {
    @Insert
    suspend fun insertInvestment(investment: InvestmentEntity)
}

@Dao
interface EvaporationDao {
    @Insert
    suspend fun insertLog(log: EvaporationLogEntity)
}

@Dao
interface PerimeterDao {
    @Insert
    suspend fun insertAlert(alert: PerimeterAlertEntity)
}

@Dao
interface SelfServiceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addToken(token: SelfServiceTokenEntity)
}

@Dao
interface CenterStationDao {
    @Query("SELECT * FROM center_stations")
    suspend fun getAllStations(): List<CenterStationEntity>
}

@Dao
interface UssdPaymentDao {
    @Insert
    suspend fun insertPayment(payment: UssdPaymentEntity)
}

@Dao
interface SolarDao {
    @Insert
    suspend fun insertLog(log: SolarLogEntity)
}

@Dao
interface TankerDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateTanker(tanker: TankerGpsEntity)
}

@Dao
interface RationingDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateRation(ration: RationingEntity)
}

@Dao
interface FraudDao {
    @Insert
    suspend fun insertFraudCheck(check: FraudCheckEntity)
}

@Dao
interface WaterDao {
    @Insert
    suspend fun insertLog(log: WaterInfiltrationEntity)
}

@Dao
interface PriorityVehicleDao {
    @Insert
    suspend fun insertVehicle(vehicle: PriorityVehicleEntity)
}

@Dao
interface TariffDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateTariff(tariff: TariffEntity)
}

@Dao
interface ShiftExpenseDao {
    @Insert
    suspend fun insertExpense(expense: ShiftExpenseEntity)
}

@Dao
interface BankSettlementDao {
    @Insert
    suspend fun insertSettlement(settlement: BankSettlementEntity)
}

@Dao
interface BulkOrderDao {
    @Insert
    suspend fun insertOrder(order: BulkOrderEntity)
}

@Dao
interface QualitySensorDao {
    @Insert
    suspend fun insertLog(log: FuelQualityLogEntity)
}

@Dao
interface QueueDao {
    @Insert
    suspend fun addEntry(entry: QueueEntryEntity)
}

@Dao
interface FarmerRationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFarmerRation(ration: FarmerRationEntity)
}

@Dao
interface TransactionRollbackDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRollback(rollback: TransactionRollbackEntity)
}


