package com.example.data

import com.example.BuildConfig
import kotlinx.coroutines.flow.Flow

class DashboardRepository(
    private val dao: DashboardDao,
    private val iotDao: IotLogDao,
    val iotSensorDao: IotSensorDao,
    private val cipherKeyDao: CipherKeyDao
) {

    // Cipher Keys
    val allCipherKeys: Flow<List<CipherKeyEntity>> = cipherKeyDao.getAllKeys()

    suspend fun insertCipherKey(key: CipherKeyEntity) {
        cipherKeyDao.insertKey(key)
    }

    // IoT Sensor Logs
    val recentIotSensorLogs: Flow<List<IotSensorLog>> = iotSensorDao.getRecentLogs()

    suspend fun insertIotSensorLog(log: IotSensorLog) {
        iotSensorDao.insertLog(log)
    }

    suspend fun getLatestSensorValue(type: String): IotSensorLog? {
        return iotSensorDao.getLatestSensorValue(type)
    }

    // IoT Logs
    val recentIotLogs: Flow<List<IotLog>> = iotDao.getRecentLogs()

    suspend fun insertIotLog(log: IotLog) {
        iotDao.insertLog(log)
    }

    // Tasks and Habits
    val allTasks: Flow<List<TaskEntity>> = dao.getAllTasks()

    suspend fun insertTask(task: TaskEntity) {
        dao.insertTask(task)
    }

    suspend fun updateTask(task: TaskEntity) {
        dao.updateTask(task)
    }

    suspend fun deleteTask(task: TaskEntity) {
        dao.deleteTask(task)
    }

    suspend fun clearAllTasks() {
        dao.deleteAllTasks()
    }

    // Notes
    val allNotes: Flow<List<NoteEntity>> = dao.getAllNotes()

    suspend fun insertNote(note: NoteEntity) {
        dao.insertNote(note)
    }

    suspend fun deleteNote(note: NoteEntity) {
        dao.deleteNote(note)
    }

    // Chat History
    val allChatMessages: Flow<List<ChatMessageEntity>> = dao.getAllChatMessages()

    suspend fun insertChatMessage(message: ChatMessageEntity) {
        dao.insertChatMessage(message)
    }

    suspend fun clearChatHistory() {
        dao.clearChatHistory()
    }

    // Payments
    val allPayments: Flow<List<PaymentEntity>> = dao.getAllPayments()

    suspend fun insertPayment(payment: PaymentEntity) {
        dao.insertPayment(payment)
    }

    suspend fun clearPaymentHistory() {
        dao.clearPaymentHistory()
    }

    // Call external Gemini API via REST
    suspend fun getGeminiResponse(
        history: List<ChatMessageEntity>,
        currentMessage: String,
        systemInstruction: String
    ): String {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY" || apiKey == "MY_NEW_API_KEY_DEFAULT_VALUE") {
            throw IllegalStateException("API Key is missing or placeholder. Please configure your GEMINI_API_KEY in the Secrets Panel.")
        }

        // Map ChatMessageEntity to Gemini format
        val contents = history.map { msg ->
            val roleName = if (msg.sender == "user") "user" else "model"
            Content(
                parts = listOf(Part(text = msg.message)),
                role = roleName
            )
        } + Content(
            parts = listOf(Part(text = currentMessage)),
            role = "user"
        )

        val request = GenerateContentRequest(
            contents = contents,
            generationConfig = GenerationConfig(temperature = 0.7f),
            systemInstruction = Content(
                parts = listOf(Part(text = systemInstruction))
            )
        )

        val response = RetrofitClient.service.generateContent(apiKey, request)
        val replyText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
        return replyText ?: throw Exception("Received empty response candidate from Gemini content generator.")
    }

    // Analyze security data
    suspend fun analyzeSecurity(logsData: String): String {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY" || apiKey == "MY_NEW_API_KEY_DEFAULT_VALUE") {
            throw IllegalStateException("API Key is missing or placeholder.")
        }

        val prompt = "Analyze the following fuel inventory and sensor logs for anomalies, potential theft, or technical failures. Provide a concise security and compliance report based on these logs: $logsData"
        val request = GenerateContentRequest(
            contents = listOf(Content(parts = listOf(Part(text = prompt)), role = "user")),
            generationConfig = GenerationConfig(temperature = 0.2f),
            systemInstruction = Content(parts = listOf(Part(text = "You are a fuel station security compliance expert. Analyze logs and alert if anomalies are found.")))
        )

        val response = RetrofitClient.service.generateContent(apiKey, request)
        return response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: "No analysis available."
    }
}
