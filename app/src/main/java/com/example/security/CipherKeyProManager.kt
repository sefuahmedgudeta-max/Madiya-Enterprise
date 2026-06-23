package com.example.security

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

object CipherKeyProManager {
    private const val PREF_FILE_NAME = "secure_cipher_keys"
    private const val KEY_API_SECRET = "pro_encryption_key_v1"

    // Key iccitii uumuu fi hardware irratti cufuuf
    fun getEncryptedPreferences(context: Context): EncryptedSharedPreferences {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        return EncryptedSharedPreferences.create(
            context,
            PREF_FILE_NAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        ) as EncryptedSharedPreferences
    }

    // Cipher Key Pro kee save gochuuf
    fun saveCipherKey(context: Context, keySecret: String) {
        val sharedPreferences = getEncryptedPreferences(context)
        sharedPreferences.edit().putString(KEY_API_SECRET, keySecret).apply()
    }

    // Yommuu SMS verification ykn IoT Data fiduun barbaadamu key sana dubbisuuf
    fun getCipherKey(context: Context): String? {
        val sharedPreferences = getEncryptedPreferences(context)
        return sharedPreferences.getString(KEY_API_SECRET, null)
    }
}
