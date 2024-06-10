package dev.templates.springhtmxtemplate.user

import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
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

    fun getCurrentUser(): User {
        val userDetails = SecurityContextHolder.getContext().authentication.principal as UserDetailsModel
        return userRepository.findByEmail(userDetails.username) ?: throw Exception("Could not find user ${userDetails.username}")
    }
}