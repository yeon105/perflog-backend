package com.perflog

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaAuditing

@EnableJpaAuditing
@SpringBootApplication
class PerflogApplication

fun main(args: Array<String>) {
    runApplication<PerflogApplication>(*args)
}
