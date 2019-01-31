package io.tripled.poker.graphql.context

import graphql.servlet.GraphQLContext
import graphql.servlet.GraphQLContextBuilder
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.websocket.Session
import javax.websocket.server.HandshakeRequest

/**
 * Basic [GraphQLContextBuilder] that creates custom [MyGraphQLContext].
 */
class MyGraphQLContextBuilder: GraphQLContextBuilder {
    override fun build(httpServletRequest: HttpServletRequest, httpServletResponse: HttpServletResponse): GraphQLContext {
        //val myValue = httpServletRequest.getHeader("MyHeader") ?: "context_from_servlet"
        return MyGraphQLContext(httpServletRequest)
    }

    override fun build(session: Session, handshakeRequest: HandshakeRequest): GraphQLContext {
        return MyGraphQLContext() /* add context here */
    }

    override fun build(): GraphQLContext = MyGraphQLContext()
}