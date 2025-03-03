package de.dkb.api.shortify.datasource

import org.springframework.data.jpa.repository.JpaRepository

interface UrlMappingRepository : JpaRepository<UrlMapping, Long> {
    fun findByLongUrl(longUrl: String): UrlMapping?
    fun findByShortUrl(shortUrl: String): UrlMapping?
}