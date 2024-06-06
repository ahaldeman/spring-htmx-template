package dev.templates.springhtmxtemplate.user

import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service

@Service
class CustomUserDetailsService(
    private val userRepository: UserRepository
): UserDetailsService {
    override fun loadUserByUsername(username: String?): UserDetails {
        if (username == null) {
            throw Exception("Username cannot be null")
        }
        val user = userRepository.findByEmail(username) ?: throw Exception("Did not find user with email $username")
        return UserDetailsModel(user = user)
    }
}