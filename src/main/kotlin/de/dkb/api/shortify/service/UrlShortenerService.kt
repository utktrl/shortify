package de.dkb.api.shortify.service

import de.dkb.api.shortify.datasource.UrlMapping
import de.dkb.api.shortify.datasource.UrlMappingRepository
import de.dkb.api.shortify.exception.InvalidUrlException
import de.dkb.api.shortify.exception.NotFoundException
import org.springframework.beans.factory.annotation.Value
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import java.math.BigInteger
import java.net.URI
import java.security.MessageDigest
import kotlin.random.Random

@Service
class UrlShortenerService (
    private val urlMappingRepository: UrlMappingRepository
) {



    @Value("\${app.base-url}")
    private lateinit var baseUrl: String

    /**
     * Shorten URL and save it to the Database
     */

    fun shortenUrl(longUrl: String): String {
        if (!isValidUrl(longUrl)) {
            throw InvalidUrlException("Invalid URL format. Please provide a valid URL.")
        }

        val existingMapping = urlMappingRepository.findByLongUrl(longUrl)
        if (existingMapping != null) {
            return existingMapping.shortUrl
        }

        val hash = sha256(longUrl)
        val sha256Encoded = base62Encode(hash)
        val shortCode = random7Chars(sha256Encoded)
        val shortUrl = "$baseUrl/$shortCode"

        val urlMapping = UrlMapping(longUrl = longUrl, shortUrl = shortUrl)
        urlMappingRepository.save(urlMapping)

        return shortUrl
    }

    /**
     * Get long URL, if necessary from the cache
     */

    @Cacheable(value = ["shortUrls"], key = "#shortCode")
    fun getLongUrl(shortCode: String): String {
        val shortUrl = "$baseUrl/$shortCode"
        return urlMappingRepository.findByShortUrl(shortUrl)?.longUrl
            ?: throw NotFoundException("Short URL not found for code: $shortCode")
    }

    private fun isValidUrl(url: String): Boolean {
        val urlPattern = Regex("^(https?)://[a-zA-Z0-9.-]+(?:\\.[a-zA-Z]{2,})+.*$")

        if (!urlPattern.matches(url)) {
            return false
        }

        val uri = URI(url)
        return (uri.scheme == "http" || uri.scheme == "https") && !uri.host.isNullOrEmpty()
    }

    private val base62Chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray()

    private fun sha256(input: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(input.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }

    private fun base62Encode(hex: String): String {
        var value = BigInteger(hex, 16)
        val result = StringBuilder()
        while (value > BigInteger.ZERO) {
            val remainder = value.mod(BigInteger.valueOf(62))
            result.append(base62Chars[remainder.toInt()])
            value = value.divide(BigInteger.valueOf(62))
        }
        return result.reverse().toString()
    }

    private fun random7Chars(input: String): String {
        val random = Random
        val startIndex = random.nextInt(input.length - 7)
        return input.substring(startIndex, startIndex + 7)
    }
}