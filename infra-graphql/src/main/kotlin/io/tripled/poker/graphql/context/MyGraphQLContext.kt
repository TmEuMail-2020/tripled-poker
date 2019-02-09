package io.tripled.poker.graphql.context

import graphql.servlet.GraphQLContext
import javax.servlet.http.HttpServletRequest

class MyGraphQLContext(httpServletRequest: HttpServletRequest? = null): GraphQLContext(httpServletRequest)