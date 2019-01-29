package io.tripled.poker.graphql.context

import graphql.servlet.GraphQLContext
import javax.security.auth.Subject
import javax.servlet.http.HttpServletRequest
import javax.websocket.server.HandshakeRequest

/**
 * Simple [GraphQLContext] that holds extra value.
 */
class MyGraphQLContext(
        val myCustomValue: String,
        val httpServletRequest: HttpServletRequest? = null,
        handshakeRequest: HandshakeRequest? = null,
        subject: Subject? = null): GraphQLContext(httpServletRequest)