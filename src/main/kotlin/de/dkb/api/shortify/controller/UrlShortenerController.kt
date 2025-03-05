package de.dkb.api.shortify.controller

import com.fasterxml.jackson.annotation.Nulls
import de.dkb.api.shortify.service.UrlShortenerService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@RestController
@RequestMapping("")
class UrlShortenerController(private val urlShortenerService: UrlShortenerService) {

    private val logger: Logger = LoggerFactory.getLogger(UrlShortenerController::class.java)
    @PostMapping("/api/shorten")
    fun shortenUrl(@RequestBody request: ShortenUrlRequest): ShortenUrlResponse {
        logger.info("Incoming request shorten URL ${'$'}{request.longUrl}")
        val shortUrl = urlShortenerService.shortenUrl(request.longUrl)
        logger.info("Generated Short URL ${'$'}shortUrl for ${'$'}{request.longUrl}")
        return ShortenUrlResponse(shortUrl)
    }

    @GetMapping("/{shortCode}")
    fun urlLookup(@PathVariable shortCode: String): ResponseEntity<out Any> {
        logger.info("Looking up short code: ${'$'}shortCode")
        val longUrl = urlShortenerService.getLongUrl(shortCode)
        return if (longUrl.isNullOrEmpty()) {
            logger.warn("Short code ${'$'}shortCode not found")
            ResponseEntity.notFound().build()
        } else {
            logger.info("Found long URL: ${'$'}longUrl for short code: ${'$'}shortCode")
            ResponseEntity.ok(longUrl)
        }
    }

    data class ShortenUrlRequest(val longUrl: String)
    data class ShortenUrlResponse(val shortUrl: String)
}