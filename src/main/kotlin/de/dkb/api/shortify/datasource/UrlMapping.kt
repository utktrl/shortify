package de.dkb.api.shortify.datasource

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
data class UrlMapping(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false)
    val longUrl: String,

    @Column(nullable = false, unique = true)
    val shortUrl: String,

    @Column(nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now()
)