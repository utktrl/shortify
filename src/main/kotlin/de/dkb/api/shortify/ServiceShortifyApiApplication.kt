package de.dkb.api.shortify

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ServiceShortifyApiApplication

fun main(args: Array<String>) {
	runApplication<ServiceShortifyApiApplication>(*args)
}
