package com.xu_store.uniform.security

import com.xu_store.uniform.model.User
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

class CustomUserDetails(private val user: User) : UserDetails {

    val userId: Long = user.id ?: error("Authenticated user must have a persisted id")

    fun getRole(): String = user.role.toString()  // or user.role.toString() if it's not an enum

    override fun getAuthorities(): Collection<GrantedAuthority> {
        return listOf(SimpleGrantedAuthority("ROLE_${user.role}"))
    }

    // Return the hashed password stored in the 'passwordHash' column
    override fun getPassword(): String = user.passwordHash

    // Use the email as the username
    override fun getUsername(): String = user.email

    override fun isAccountNonExpired(): Boolean = true

    override fun isAccountNonLocked(): Boolean = true

    override fun isCredentialsNonExpired(): Boolean = true

    override fun isEnabled(): Boolean = true
}
