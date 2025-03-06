package de.dkb.api.shortify.controller

import de.dkb.api.shortify.service.UrlShortenerService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory


@RestController
@RequestMapping("")
class UrlShortenerController(private val urlShortenerService: UrlShortenerService) {

    private val logger: Logger = LoggerFactory.getLogger(UrlShortenerController::class.java)

    /**
     * Generates a shortURL for given longURL.
     * URL must be valid to process the shorten api
     * In order to create the short code, the first approach, mentioned on here was applied https://shorturl.at/SCg4U
     */
    @PostMapping("/api/shorten")
    fun shortenUrl(@RequestBody request: ShortenUrlRequest): ShortenUrlResponse {
        logger.info("Incoming request shorten URL ${'$'}{request.longUrl}")
        val shortUrl = urlShortenerService.shortenUrl(request.longUrl)
        logger.info("Generated Short URL ${'$'}shortUrl for ${'$'}{request.longUrl}")
        return ShortenUrlResponse(shortUrl)
    }

    /**
     * Returns the longURL for a given short URL
     * ShortURL must exist on the system.
     */

    @GetMapping("/{shortCode}")
    fun urlLookup(@PathVariable shortCode: String): ResponseEntity<String> {
        val longUrl = urlShortenerService.getLongUrl(shortCode)
        return ResponseEntity.ok(longUrl)
    }

    data class ShortenUrlRequest(val longUrl: String)
    data class ShortenUrlResponse(val shortUrl: String)
}