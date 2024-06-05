package dev.templates.springhtmxtemplate.user

import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component

@Component
class CustomAuthenticationProvider(
    private val customUserDetailsService: CustomUserDetailsService
): AuthenticationProvider {
    override fun authenticate(authentication: Authentication?): Authentication {
        if (authentication == null) {
            throw Exception("Authentication cannot be null")
        }
        val userDetails = customUserDetailsService.loadUserByUsername(authentication.name)
        return UsernamePasswordAuthenticationToken(userDetails.password, userDetails.password)
    }

    override fun supports(authentication: Class<*>?): Boolean {
        if (authentication == null) {
            return false
        }
        return UsernamePasswordAuthenticationToken::class.java.isAssignableFrom(authentication)
    }
}