package tw.pcbill.ksstarter.security

import org.springframework.stereotype.Component
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.beans.factory.annotation.Value
import java.util.*
import io.jsonwebtoken.Claims
import tw.pcbill.ksstarter.security.JwtUser
import org.springframework.security.core.userdetails.UserDetails
import java.util.HashMap




@Component
class JwtTokenUtil {

    private val CLAIM_KEY_USERNAME = "sub"
    private val CLAIM_KEY_CREATED = "created"

    @Value("\${jwt.secret}")
    private val secret: String? = null

    @Value("\${jwt.expiration}")
    private val expiration: Long = 5 * 60 //seconds

    fun generateToken(userDetails: UserDetails): String {
        val claims = HashMap<String, Any>()
        claims[CLAIM_KEY_USERNAME] = userDetails.username
        claims[CLAIM_KEY_CREATED] = Date()
        return generateToken(claims)
    }

    fun generateToken(claims: Map<String, Any>): String {
        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(generateExpirationDate())
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact()
    }

    private fun generateExpirationDate(): Date {
        return Date(System.currentTimeMillis() + expiration * 1000)
    }

    fun getUsernameFromToken(token: String): String? {
        var username: String?
        try {
            val claims = getClaimsFromToken(token)
            username = claims?.getSubject()
        } catch (e: Exception) {
            username = null
        }

        return username
    }

    private fun getClaimsFromToken(token: String): Claims? {
        var claims: Claims?
        try {
            claims = Jwts.parser()
                    .setSigningKey(secret)
                    .parseClaimsJws(token)
                    .body
        } catch (e: Exception) {
            claims = null
        }

        return claims
    }

    fun validateToken(token: String, userDetails: UserDetails): Boolean {
        val user = userDetails as JwtUser
        val username = getUsernameFromToken(token)
        val created = getCreatedDateFromToken(token)
        //final Date expiration = getExpirationDateFromToken(token);
        return (username == user.username
                && !isTokenExpired(token)
                && !isCreatedBeforeLastPasswordReset(created, user.getLastPasswordResetDate()))
    }

    private fun getCreatedDateFromToken(token: String): Date? {
        var created: Date?
        try {
            val claims = getClaimsFromToken(token)
            created = Date(claims?.get(CLAIM_KEY_CREATED) as Long)
        } catch (e: Exception) {
            created = null
        }

        return created
    }

    private fun isTokenExpired(token: String): Boolean {
        val expiration = getExpirationDateFromToken(token)
        if (expiration == null) { return true }
        return expiration.before(Date())
    }

    private fun isCreatedBeforeLastPasswordReset(created: Date?, lastPasswordReset: Date?): Boolean {
        return lastPasswordReset != null && created != null && created.before(lastPasswordReset)
    }

    private fun getExpirationDateFromToken(token: String): Date? {
        var expiration: Date?
        try {
            val claims = getClaimsFromToken(token)
            expiration = claims?.getExpiration()
        } catch (e: Exception) {
            expiration = null
        }

        return expiration
    }

    fun canTokenBeRefreshed(token: String, lastPasswordReset: Date): Boolean {
        val created = getCreatedDateFromToken(token)
        return !isCreatedBeforeLastPasswordReset(created, lastPasswordReset) && !isTokenExpired(token)
    }

    fun refreshToken(token: String): String? {
        var refreshedToken: String? = ""
        try {
            val claims = getClaimsFromToken(token)
            if (claims != null) {
                claims[CLAIM_KEY_CREATED] = Date()
                refreshedToken = generateToken(claims)
            }
        } catch (e: Exception) {
            refreshedToken = null
        }

        return refreshedToken
    }
}