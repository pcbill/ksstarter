package tw.pcbill.ksstarter.security

import com.fasterxml.jackson.annotation.JsonIgnore
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.util.*

class JwtUser(
        private val username: String,
        private val password: String,
        private val authorities: List<GrantedAuthority>,
        private val lastPasswordResetDate: Date
    ) : UserDetails {


    override fun getAuthorities(): List<GrantedAuthority> {
        return authorities
    }

    override fun getUsername(): String {
        return username
    }

    @JsonIgnore
    override fun getPassword(): String {
        return password
    }

    @JsonIgnore
    fun getLastPasswordResetDate(): Date {
        return lastPasswordResetDate
    }

    @JsonIgnore
    override fun isEnabled(): Boolean {
        return true // TODO: should not be hardcode
    }

    @JsonIgnore
    override fun isCredentialsNonExpired(): Boolean {
        return true // TODO: should not be hardcode
    }

    @JsonIgnore
    override fun isAccountNonExpired(): Boolean {
        return true // TODO: should not be hardcode
    }

    @JsonIgnore
    override fun isAccountNonLocked(): Boolean {
        return true; // TODO: should not be hardcode
    }
}