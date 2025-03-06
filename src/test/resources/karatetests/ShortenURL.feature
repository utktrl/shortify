Feature: Given the long URL, /api/shorten endpoint should return a shortened URL

  Background:
    * url baseURL

  Scenario: Shorten a valid URL and then get it back

    ## Generate Short URL
    Given path '/api/shorten'
    And request { longUrl: 'https://dkb.de/some/long/url' }
    When method POST
    Then status 200
    And assert response.shortUrl.length > 0

    ## Get Long URL
    Given url response.shortUrl
    When method GET
    Then status 200
    And match response == 'https://dkb.de/some/long/url'

  Scenario Outline: Try to Shorten an Invalid URL <reasonForInvalidity>

    Given path '/api/shorten'
    And request { longUrl: '<invalidUrl>' }
    When method POST
    Then status 400
    And match response.message == 'Invalid URL format. Please provide a valid URL.'

    Examples:
      | invalidURL | reasonForInvalidity |
      | www.example.com | No protocol  |
      | http:///example.com | Unsupported protocol   |
      | http://example.com/<script> | Extra slashes after the protocol. |
      | http://.com | Domain name is incomplete or malformed. |
      | http://example .com | Contains spaces, which are not allowed in URLs. |
