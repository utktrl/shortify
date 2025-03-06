package de.dkb.api.shortify.service

import de.dkb.api.shortify.datasource.UrlMapping
import de.dkb.api.shortify.datasource.UrlMappingRepository
import de.dkb.api.shortify.exception.InvalidUrlException
import de.dkb.api.shortify.exception.NotFoundException
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.*
import org.springframework.test.util.ReflectionTestUtils

class UrlShortenerServiceTest {

    private lateinit var urlMappingRepository: UrlMappingRepository
    private lateinit var urlShortenerService: UrlShortenerService

    @BeforeEach
    fun setup() {
        urlMappingRepository = mock(UrlMappingRepository::class.java)
        urlShortenerService = UrlShortenerService(urlMappingRepository)
        ReflectionTestUtils.setField(urlShortenerService, "baseUrl", "http://short.ly")
    }

    @Test
    fun `shortenUrl should return existing short URL if long URL already exists`() {
        val longUrl = "https://dkb.de"
        val shortUrl = "http://short.ly/abc1234"
        val existingMapping = UrlMapping(longUrl = longUrl, shortUrl = shortUrl)

        `when`(urlMappingRepository.findByLongUrl(longUrl)).thenReturn(existingMapping)

        val result = urlShortenerService.shortenUrl(longUrl)
        assertEquals(shortUrl, result)
        verify(urlMappingRepository, never()).save(any())
    }

    @Test
    fun `shortenUrl should throw InvalidUrlException for invalid URL`() {
        val invalidUrl = "invalid-url"
        assertThrows<InvalidUrlException> {
            urlShortenerService.shortenUrl(invalidUrl)
        }
    }

    @Test
    fun `shortenUrl should generate and store a new short URL`() {
        val longUrl = "https://galatasaray.org"
        `when`(urlMappingRepository.findByLongUrl(longUrl)).thenReturn(null)
        `when`(urlMappingRepository.save(any())).thenAnswer { it.arguments[0] }

        val result = urlShortenerService.shortenUrl(longUrl)
        assertTrue(result.startsWith("http://short.ly/"))
        verify(urlMappingRepository).save(any())
    }

    @Test
    fun `getLongUrl should return long URL for valid short code`() {
        val shortCode = "abc1234"
        val shortUrl = "http://short.ly/$shortCode"
        val longUrl = "https://galatasaray.org"
        val mapping = UrlMapping(longUrl = longUrl, shortUrl = shortUrl)

        `when`(urlMappingRepository.findByShortUrl(shortUrl)).thenReturn(mapping)

        val result = urlShortenerService.getLongUrl(shortCode)
        assertEquals(longUrl, result)
    }

    @Test
    fun `should throw InvalidUrlException when URL is invalid `() {
        val invalidUrl = "htpt://galatasaray.org"

        val exception = try {
            urlShortenerService.shortenUrl(invalidUrl)
            null
        } catch (ex: InvalidUrlException) {
            ex
        }

        assertEquals("Invalid URL format. Please provide a valid URL.", exception?.message)

    }

    @Test
    fun `should throw NotFoundException when short code does not exist`() {
        val shortCode = "nonexistent"
        `when`(urlMappingRepository.findByShortUrl("http://base.url/$shortCode")).thenReturn(null)

        val exception = try {
            urlShortenerService.getLongUrl(shortCode)
            null
        } catch (ex: NotFoundException) {
            ex
        }

        assertEquals("Short URL not found for code: nonexistent", exception?.message)
    }
}
