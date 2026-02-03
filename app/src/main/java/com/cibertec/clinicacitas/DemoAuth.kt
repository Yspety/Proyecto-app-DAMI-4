package com.cibertec.clinicacitas

/**
 * Autenticación con datos estáticos .
 */
object DemoAuth {

    private data class DemoUser(
        val username: String,
        val password: String,
        val role: UserRole
    )

    private val demoUsers: List<DemoUser> = listOf(
        DemoUser(username = "admin", password = "admin123", role = UserRole.ADMIN),
        DemoUser(username = "paciente", password = "paciente123", role = UserRole.PACIENTE)
    )

    fun authenticate(username: String, password: String): UserSession? {
        val match = demoUsers.firstOrNull {it.username == username && it.password == password }
        return match?.let { UserSession(username = it.username, role = it.role) }
    }
}
