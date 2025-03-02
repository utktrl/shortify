package de.dkb.api.shortify.datasource

import jakarta.persistence.*

@Entity
data class UrlMapping(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false)
    val longUrl: String,

    @Column(nullable = false, unique = true)
    val shortUrl: String
)