package com.api.mulio_backend.config

import com.api.mulio_backend.repository.UserRepository
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class JwtUserDetailsService(private val userRepository: UserRepository) : UserDetailsService {

    override fun loadUserByUsername(username: String): UserDetails {
        // Fetch user from the database
        val user = userRepository.findByUsername(username)
            ?: throw UsernameNotFoundException("USER_NOT_FOUND '$username'.")

        // Convert the user's role to a GrantedAuthority
        val authorities: List<GrantedAuthority> = listOf(SimpleGrantedAuthority(user.role.name))

        // Return Spring Security User object with username, password, and authorities
        return org.springframework.security.core.userdetails.User(user.username, user.password, authorities)
    }
}