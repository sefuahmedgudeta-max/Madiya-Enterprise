package com.example.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object PremiumRepository {
    private val _isPremiumUser = MutableStateFlow(false)
    val isPremiumUser: StateFlow<Boolean> = _isPremiumUser

    // 1. Kaffaltii Telebirr/Chapa Jalqabsiisuu
    fun initiatePayment(phoneNumber: String, amount: Double): String {
        val txRef = "SAFUU-${System.currentTimeMillis()}"
        // Asitti Chapa ykn Telebirr API weblink uumama
        println("Kaffaltiin $amount ETB lakkoofsa $phoneNumber irratti ka'eera. TxRef: $txRef")
        return "https://checkout.chapa.co/checkout/payment/$txRef" // Kaffaltii uumame
    }

    // 2. Kaffaltiin erga raawwatee booda account mirkaneessuu
    fun verifyPaymentSuccess(txRef: String) {
        // Fakkeenyaaf yoo kaffaltiin milkaa'e account gara Premium tti jijjiira
        if (txRef.startsWith("SAFUU")) {
            _isPremiumUser.value = true
        }
    }

    fun demoteToFreeForDemo() {
        _isPremiumUser.value = false
    }
}
