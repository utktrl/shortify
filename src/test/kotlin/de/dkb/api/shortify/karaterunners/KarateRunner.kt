package de.dkb.api.shortify.karaterunners

import com.intuit.karate.junit5.Karate;

class KarateRunner {
	@Karate.Test
	fun testAll(): Karate {
		return Karate.run("classpath:karatetests/ShortenURL.feature")
	}
}