package org.hameister.filmwatcher

import org.hameister.filmwatcher.business.JsonImporter
import org.hameister.filmwatcher.data.FilmRepository
import org.hameister.filmwatcher.domain.Film
import org.hameister.filmwatcher.domain.Provider
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import java.io.File
import java.time.Duration
import java.time.LocalDate

@SpringBootApplication
class FilmWatcherApplication {

    @Bean
    fun init(repository: FilmRepository) = CommandLineRunner {
        val jsonFile:File= File("/Users/hameister/Documents/github/FilmWatcher/src/main/resources/films.json")

        if (jsonFile.exists()) {
            val importer = JsonImporter(jsonFile.absolutePath)
            importer.importFilms(repository)

            val numberOfFilms = repository.findAll().toIterable().toList().size
            println(numberOfFilms)
        }
    }

}

fun main(args: Array<String>) {
    runApplication<FilmWatcherApplication>(*args)
}

