package org.hameister.filmwatcher.resource

import org.hameister.filmwatcher.business.FilmService
import org.hameister.filmwatcher.data.FilmRepository
import org.hameister.filmwatcher.domain.Film
import org.hameister.filmwatcher.domain.Provider
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
class FilmController(private val filmService: FilmService) {

    @GetMapping("/film")
    fun readAll(): Flux<Film> = filmService.findAll()

    @GetMapping("/film/{id}")
    fun readOne(@PathVariable id :Long): Mono<Film> = filmService.findById(id)


    @GetMapping("film/provider/{id}")
    fun findByProvider(@PathVariable id:String):Flux<Film> {
        return filmService.findAllWatchedOn(id)
    }
}