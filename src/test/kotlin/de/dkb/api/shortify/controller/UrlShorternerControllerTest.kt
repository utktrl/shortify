package de.dkb.api.shortify.controller

import com.linecorp.armeria.common.HttpStatus
import de.dkb.api.shortify.service.UrlShortenerService
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

import org.mockito.Mockito.`when`

@ExtendWith(MockitoExtension::class)
class UrlShortenerControllerTest {

    private val urlShortenerService = Mockito.mock(UrlShortenerService::class.java)
    private val controller = UrlShortenerController(urlShortenerService)

    @Test
    fun `should shorten URL successfully`() {
        val longUrl = "https://galatasaray.org"
        val shortUrl = "short.ly/abc123"
        `when`(urlShortenerService.shortenUrl(longUrl)).thenReturn(shortUrl)

        val request = UrlShortenerController.ShortenUrlRequest(longUrl)
        val response = controller.shortenUrl(request)

        assertEquals(shortUrl, response.shortUrl)
    }

    @Test
    fun `should return long URL when short code exists`() {
        val shortCode = "abc123"
        val longUrl = "https://dkb.de"
        `when`(urlShortenerService.getLongUrl(shortCode)).thenReturn(longUrl)

        val response = controller.urlLookup(shortCode)
        assertEquals(ResponseEntity.ok(longUrl), response)
    }

}

@RestController
@RequestMapping("")
class UrlShortenerController(private val urlShortenerService: UrlShortenerService) {

    private val logger: Logger = LoggerFactory.getLogger(UrlShortenerController::class.java)

    @PostMapping("/api/shorten")
    fun shortenUrl(@RequestBody request: ShortenUrlRequest): ShortenUrlResponse {
        logger.info("Received request to shorten URL: ${'$'}{request.longUrl}")
        val shortUrl = urlShortenerService.shortenUrl(request.longUrl)
        logger.info("Generated short URL: ${'$'}shortUrl for long URL: ${'$'}{request.longUrl}")
        return ShortenUrlResponse(shortUrl)
    }

    @GetMapping("/{shortCode}")
    fun urlLookup(@PathVariable shortCode: String): ResponseEntity<out Any> {
        logger.info("Looking up short code: ${'$'}shortCode")
        val longUrl = urlShortenerService.getLongUrl(shortCode)
        return if (longUrl != null) {
            logger.info("Found long URL: ${'$'}longUrl for short code: ${'$'}shortCode")
            ResponseEntity.ok(longUrl)
        } else {
            logger.warn("Short code ${'$'}shortCode not found")
            ResponseEntity.notFound().build()
        }
    }

    data class ShortenUrlRequest(val longUrl: String)
    data class ShortenUrlResponse(val shortUrl: String)
}
