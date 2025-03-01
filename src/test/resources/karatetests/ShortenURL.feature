Feature: Given the long URL, /api/shorten endpoint should return a shortened URL

  Background:
    * url baseURL

  Scenario: Shorten a valid URL
    Given path /api/shorten
    And request { longUrl: 'https://example.com/some/long/url' }
    When method POST
    Then status 200
    And match response.shortUrl == '#regex http://localhost:8080/[a-zA-Z0-9]+'
    And def shortUrl = response.shortUrl

  Scenario: Expand a shortened URL
    Given path shortUrl.split("/")[-1]
    When method GET
    Then status 301
    And match responseHeaders.Location == 'https://example.com/some/long/url'
