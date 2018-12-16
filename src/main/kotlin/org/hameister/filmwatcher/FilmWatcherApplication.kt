package org.hameister.filmwatcher

import org.hameister.filmwatcher.data.FilmRepository
import org.hameister.filmwatcher.domain.Film
import org.hameister.filmwatcher.domain.Provider
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import java.time.Duration
import java.time.LocalDate

@SpringBootApplication
class FilmWatcherApplication {

    @Bean
    fun init(repository: FilmRepository)= CommandLineRunner {

        repository.insert(
                arrayListOf(
                        Film(1, "Star Wars", LocalDate.now(), Provider("DVD")),
                        Film(2, "Terminator", LocalDate.now(), Provider("Netflix")),
                        Film(3, "Zurück in die Zukunft", LocalDate.of(2018,11,5), Provider("DVD")))

        ).blockLast(Duration.ofSeconds(2))
    }

}

fun main(args: Array<String>) {
    runApplication<FilmWatcherApplication>(*args)
}

