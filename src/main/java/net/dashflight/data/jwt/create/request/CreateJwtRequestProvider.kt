package net.dashflight.data.jwt.create.request

interface CreateJwtRequestProvider {
    /**
     * Creates a jwt request for the specified userId
     */
    fun create(userId: String): CreateJwtRequest
}