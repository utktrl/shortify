package de.dkb.api.shortify.service

import de.dkb.api.shortify.datasource.UrlMapping
import de.dkb.api.shortify.datasource.UrlMappingRepository
import de.dkb.api.shortify.exception.InvalidUrlException
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import java.net.URI
import java.security.SecureRandom
import kotlin.math.absoluteValue

@Service
class UrlShortenerService (
    private val urlMappingRepository: UrlMappingRepository
) {

    private val random = SecureRandom() // Use SecureRandom for better randomness
    private val minShortCodeLength = 6 // Minimum length of the short code

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
        val shortCode = generateRandomShortCode()

        val shortUrl = "http://localhost:8080/$shortCode"

        // Save to Database
        val urlMapping = UrlMapping(longUrl = longUrl, shortUrl = shortUrl)
        urlMappingRepository.save(urlMapping)

        return shortUrl
    }

    @Cacheable(value = ["shortUrls"], key = "#shortCode")
    fun getLongUrl(shortCode: String): String? {
        val shortUrl = "http://localhost:8080/$shortCode"
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

    private fun generateRandomShortCode(): String {
        val characters = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
        val base = characters.length
        val shortCode = StringBuilder()

        // Generate a random number and encode it in base62
        var randomNumber = random.nextLong().absoluteValue // Ensure the number is positive
        while (randomNumber > 0) {
            shortCode.append(characters[(randomNumber % base).toInt()])
            randomNumber /= base
        }

        // If the short code is shorter than the minimum length, pad it with random characters
        while (shortCode.length < minShortCodeLength) {
            shortCode.append(characters[random.nextInt(base)])
        }

        return shortCode.toString()
    }
}