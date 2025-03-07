package de.dkb.api.shortify.datasource

import jakarta.persistence.*
import java.time.LocalDateTime

/**
 * Database Tables for the long short URL mapping
 */
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
){

    constructor() : this(null, "", "", LocalDateTime.now())
}