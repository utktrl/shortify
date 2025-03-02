package de.dkb.api.shortify.controller

import de.dkb.api.shortify.service.UrlShortenerService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
class UrlShortenerController(private val urlShortenerService: UrlShortenerService) {

    @PostMapping("/shorten")
    fun shortenUrl(@RequestBody request: ShortenUrlRequest): ShortenUrlResponse {
        val shortUrl = urlShortenerService.shortenUrl(request.longUrl)
        return ShortenUrlResponse(shortUrl)
    }

    data class ShortenUrlRequest(val longUrl: String)
    data class ShortenUrlResponse(val shortUrl: String)
}