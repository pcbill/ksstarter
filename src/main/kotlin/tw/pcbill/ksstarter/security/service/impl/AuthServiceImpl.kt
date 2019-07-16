package tw.pcbill.ksstarter.security.service.impl

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import tw.pcbill.ksstarter.model.User
import tw.pcbill.ksstarter.security.service.AuthService
import tw.pcbill.ksstarter.repository.UserRepository
import tw.pcbill.ksstarter.security.JwtTokenUtil
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import java.util.*
import java.util.Arrays.asList
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import tw.pcbill.ksstarter.security.JwtUser

@Service
class AuthServiceImpl : AuthService {

    @Autowired
    lateinit var authenticationManager: AuthenticationManager

    @Qualifier("userDetailServiceImpl")
    @Autowired
    lateinit var userDetailsService: UserDetailsService

    @Autowired
    lateinit var jwtTokenUtil: JwtTokenUtil

    @Autowired
    lateinit var userRepository: UserRepository

    @Value("\${jwt.tokenHead}")
    private val tokenHead: String? = null

    override fun register(userToAdd: User): User? {
        val username = userToAdd.username
        if (userRepository.findByUsername(username) != null) {
            return null
        }
        val encoder = BCryptPasswordEncoder()
        val rawPassword = userToAdd.password
        userToAdd.password = encoder.encode(rawPassword)
        userToAdd.lastPasswordResetDate = Date()
        userToAdd.roles = "ROLE_USER"
        return userRepository.save(userToAdd)
    }

    override fun login(username: String, password: String): String {
        val upToken = UsernamePasswordAuthenticationToken(username, password)
        val authentication = authenticationManager.authenticate(upToken)
        SecurityContextHolder.getContext().authentication = authentication

        val userDetails = userDetailsService.loadUserByUsername(username)
        return jwtTokenUtil.generateToken(userDetails)
    }

    override fun refresh(oldToken: String): String? {
        val token = oldToken.substring(tokenHead!!.length)
        val username = jwtTokenUtil.getUsernameFromToken(token)
        val user = userDetailsService.loadUserByUsername(username) as JwtUser

        return if (jwtTokenUtil.canTokenBeRefreshed(token, user.getLastPasswordResetDate())) {
            jwtTokenUtil.refreshToken(token)
        } else null
    }
}