package com.mitsudrive.core.network.model

data class SiteUser(
    val id: Long,
    val email: String,
    val name: String,
    val phone: String,
    val token: String
)

data class SiteApiResponse(
    val status: String,
    val message: String? = null,
    val userId: Long? = null,
    val email: String? = null,
    val name: String? = null,
    val token: String? = null,
    val user: SiteUser? = null
)
