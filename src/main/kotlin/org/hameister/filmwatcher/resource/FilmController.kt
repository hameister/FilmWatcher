package org.hameister.filmwatcher.resource

import org.bson.types.ObjectId
import org.hameister.filmwatcher.business.FilmService
import org.hameister.filmwatcher.domain.Film
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
class FilmController(private val filmService: FilmService) {

    @GetMapping("/film")
    fun readAll(): Flux<Film> = filmService.findAll()

    @GetMapping("/film/{id}")
    fun readOne(@PathVariable id :ObjectId): Mono<Film> = filmService.findById(id)


    @GetMapping("film/provider/{id}")
    fun findByProvider(@PathVariable id:String):Flux<Film> {
        return filmService.findAllWatchedOn(id)
    }

    @GetMapping("film/year/{year}")
    fun findByYear(@PathVariable year:String):Flux<Film> {
        return filmService.findByYear(year.toInt())
    }

    @PostMapping("/film")
    fun importFilms(@RequestBody films:Flux<Film>): Flux<Film>{
        println("Controller import films")
        return filmService.importFilms(films)
    }
}