package tw.pcbill.ksstarter.security

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken


//@Component
public class JwtAuthenticationTokenFilter : OncePerRequestFilter() {

    @Qualifier("userDetailServiceImpl")
    @Autowired
    lateinit var userDetailsService: UserDetailsService

    @Autowired
    lateinit var jwtTokenUtil: JwtTokenUtil

    @Value("\${jwt.header}")
    private val tokenHeader: String = ""

    @Value("\${jwt.tokenHead}")
    private val tokenHead: String = ""

    override fun doFilterInternal(request: HttpServletRequest,
                                  response: HttpServletResponse,
                                  chain: FilterChain)
    {
        val authHeader = request.getHeader(this.tokenHeader)
        if (authHeader != null && authHeader.startsWith(tokenHead)) {
            val authToken = authHeader.substring(tokenHead.length) // The part after "Bearer "
            val username = jwtTokenUtil.getUsernameFromToken(authToken)

            logger.info("checking authentication " + username!!)

            if (SecurityContextHolder.getContext().authentication == null) {

                val userDetails = this.userDetailsService.loadUserByUsername(username)

                if (jwtTokenUtil.validateToken(authToken, userDetails)) {
                    val authentication = UsernamePasswordAuthenticationToken(userDetails, null, userDetails.authorities)
                    authentication.details = WebAuthenticationDetailsSource().buildDetails(request)

                    logger.info("authenticated user $username, setting security context")

                    SecurityContextHolder.getContext().authentication = authentication
                }
            }
        }

        chain.doFilter(request, response)
    }
}