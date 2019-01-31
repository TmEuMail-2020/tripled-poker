package io.tripled.poker.graphql.context

import graphql.servlet.GraphQLContext
import javax.servlet.http.HttpServletRequest

/**
 * Simple [GraphQLContext] that holds extra value.
 */
class MyGraphQLContext(httpServletRequest: HttpServletRequest? = null): GraphQLContext(httpServletRequest)