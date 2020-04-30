package io.tripled.poker.web.filter

import com.auth0.jwt.JWT
import io.tripled.poker.web.filter.SecurityConstants.BEARER_PREFIX
import io.tripled.poker.web.filter.SecurityConstants.HEADER_STRING
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter
import java.util.*
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class JWTAuthorizationFilter(authManager: AuthenticationManager?) : BasicAuthenticationFilter(authManager) {
    override fun doFilterInternal(req: HttpServletRequest,
                                  res: HttpServletResponse,
                                  chain: FilterChain) {
        val header: String? = req.getHeader(HEADER_STRING)
        header?.let {
            authorizeJWTHeader(it)
        }
        chain.doFilter(req, res)
    }

    private fun authorizeJWTHeader(header: String) {
        if (header.startsWith(BEARER_PREFIX)) {
            SecurityContextHolder.getContext().authentication = getAuthentication(header)
        }
    }

    private fun getAuthentication(authorizationToken: String): UsernamePasswordAuthenticationToken? {
        val jwtToken = authorizationToken.replace(BEARER_PREFIX, "")
        /* TODO fix token validation, it's RSA256
    * JWT.require(Algorithm.HMAC512(SECRET.toByteArray()))
            .build()
            .verify(token.replace(TOKEN_PREFIX, ""))
            .subject
    * */
        val user = JWT.decode(jwtToken).getClaim("preferred_username").asString()
        return if (user == null) {
            null
        } else {
            UsernamePasswordAuthenticationToken(user, null, ArrayList())
        }
    }
}