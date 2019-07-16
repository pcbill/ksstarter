package tw.pcbill.ksstarter.security

import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.GrantedAuthority
import tw.pcbill.ksstarter.model.User
import java.util.Arrays.asList


class JwtUserFactory {
    companion object {
        @JvmStatic
        fun create(user: User): JwtUser {
            return JwtUser(
                    user.username,
                    user.password,
                    mapToGrantedAuthorities(asList(user.roles)),
                    user.lastPasswordResetDate
            )
        }

        private fun mapToGrantedAuthorities(authorities: List<String>): List<GrantedAuthority> {
            return authorities.map { SimpleGrantedAuthority(it) }
        }
    }
}