package dev.templates.springhtmxtemplate.user

import org.springframework.security.core.Authentication
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userRepository: UserRepository,
    private val bCryptPasswordEncoder: BCryptPasswordEncoder
) {
    fun save(user: User) {
        val encodedPassword = bCryptPasswordEncoder.encode(user.password)
        userRepository.save(user.copy(password = encodedPassword))
    }

    fun findByEmail(email: String): User? {
        return userRepository.findByEmail(email = email)
    }

    fun getCurrentUser(): User {
        return User()
    }
}