package org.hameister.filmwatcher.resource

import org.hameister.filmwatcher.data.FilmRepository
import org.hameister.filmwatcher.domain.Film
import org.hameister.filmwatcher.domain.Provider
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
class FilmController(private val filmRepository: FilmRepository) {

    @GetMapping("/film")
    fun readAll(): Flux<Film> = filmRepository.findAll()

    @GetMapping("/film/{id}")
    fun readOne(@PathVariable id :Long): Mono<Film> = filmRepository.findById(id)

    @GetMapping("/filmquery")
    fun testQuery(): Flux<Film> = filmRepository.findByProvider(Provider("Netflix"))
}