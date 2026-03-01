package com.cibertec.clinicacitas

import java.security.MessageDigest
import java.security.SecureRandom

object SecurityUtils {

    // Genera una "sal" aleatoria para añadir a la contraseña antes de hashearla
    fun getSalt(): ByteArray {
        val random = SecureRandom()
        val salt = ByteArray(16)
        random.nextBytes(salt)
        return salt
    }

    // Hashea una contraseña usando el algoritmo SHA-256 junto con una "sal"
    fun hashPassword(password: String, salt: ByteArray): String {
        val md = MessageDigest.getInstance("SHA-256")
        md.update(salt)
        val hashedPassword = md.digest(password.toByteArray())
        return hashedPassword.toHexString()
    }

    // Convierte un array de bytes a su representación en texto hexadecimal
    private fun ByteArray.toHexString(): String {
        return joinToString("") { "%02x".format(it) }
    }
}
