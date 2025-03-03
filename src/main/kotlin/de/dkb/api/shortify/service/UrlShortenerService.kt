package de.dkb.api.shortify.service

import de.dkb.api.shortify.datasource.UrlMapping
import de.dkb.api.shortify.datasource.UrlMappingRepository
import de.dkb.api.shortify.exception.InvalidUrlException
import org.springframework.beans.factory.annotation.Value
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import java.math.BigInteger
import java.net.URI
import java.security.MessageDigest
import java.security.SecureRandom
import kotlin.math.absoluteValue
import kotlin.random.Random

@Service
class UrlShortenerService (
    private val urlMappingRepository: UrlMappingRepository
) {

    @Value("\${app.base-url}")  // Inject the base URL from configuration
    private lateinit var baseUrl: String

    fun shortenUrl(longUrl: String): String {
        if (!isValidUrl(longUrl)) {
            throw InvalidUrlException("Invalid URL format. Please provide a valid URL.")
        }

        // Check if the longUrl already exists
        val existingMapping = urlMappingRepository.findByLongUrl(longUrl)
        if (existingMapping != null) {
            return existingMapping.shortUrl
        }

        // Generate a random short code
        val hash = sha256(longUrl)
        val sha256Encoded = base62Encode(hash)
        val shortCode = random7Chars(sha256Encoded)
        val shortUrl = "$baseUrl/$shortCode"

        // Save to Database
        val urlMapping = UrlMapping(longUrl = longUrl, shortUrl = shortUrl)
        urlMappingRepository.save(urlMapping)

        return shortUrl
    }

    @Cacheable(value = ["shortUrls"], key = "#shortCode")
    fun getLongUrl(shortCode: String): String? {
        val shortUrl = "$baseUrl/$shortCode"
        return urlMappingRepository.findByShortUrl(shortUrl)?.longUrl
    }

    private fun isValidUrl(url: String): Boolean {
        val urlPattern = Regex("^(https?)://[a-zA-Z0-9.-]+(?:\\.[a-zA-Z]{2,})+.*$")

        if (!urlPattern.matches(url)) {
            return false
        }

        val uri = URI(url)
        return (uri.scheme == "http" || uri.scheme == "https") && !uri.host.isNullOrEmpty()
    }

    // Base62 characters
    private val base62Chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray()

    // Function to hash the URL using SHA-256
    private fun sha256(input: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(input.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }

    // Function to encode a hex string to Base62
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

    // Function to get a random 7-character substring
    private fun random7Chars(input: String): String {
        val random = Random
        val startIndex = random.nextInt(input.length - 7)
        return input.substring(startIndex, startIndex + 7)
    }
}