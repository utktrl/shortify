package de.dkb.api.shortify.controller

import de.dkb.api.shortify.service.UrlShortenerService
import org.springframework.web.servlet.view.RedirectView
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("")
class UrlShortenerController(private val urlShortenerService: UrlShortenerService) {

    @PostMapping("/api/shorten")
    fun shortenUrl(@RequestBody request: ShortenUrlRequest): ShortenUrlResponse {
        val shortUrl = urlShortenerService.shortenUrl(request.longUrl)
        return ShortenUrlResponse(shortUrl)
    }

    @GetMapping("/{shortCode}")
    fun urlLookup(@PathVariable shortCode: String): ResponseEntity<out Any> {
        val longUrl = urlShortenerService.getLongUrl(shortCode)
        return ResponseEntity.ok(longUrl)
    }

    data class ShortenUrlRequest(val longUrl: String)
    data class ShortenUrlResponse(val shortUrl: String)
}