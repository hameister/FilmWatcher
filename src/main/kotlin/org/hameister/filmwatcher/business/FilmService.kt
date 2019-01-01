package org.hameister.filmwatcher.business

import org.bson.types.ObjectId
import org.hameister.filmwatcher.data.FilmRepository
import org.hameister.filmwatcher.domain.Film
import org.hameister.filmwatcher.domain.Provider
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.LocalDate

@Service
class FilmService(val filmRepository: FilmRepository) {

    fun findAll(): Flux<Film> = filmRepository.findAll()
    fun findById(id: ObjectId): Mono<Film> = filmRepository.findById(id)


    fun findAllWatchedOnDvd(): Flux<Film> {
        return filmRepository.findByProvider(Provider("DVD"))
    }

    fun findAllWatchedOnNetflix(): Flux<Film> {
        return filmRepository.findByProvider(Provider("Netflix"))
    }

    fun findAllWatchedOn(provider: String): Flux<Film> {
        when (provider) {
            "DVD" -> return findAllWatchedOnDvd()
            "Netflix" -> return findAllWatchedOnNetflix()
        }
        return Flux.empty()
    }

    fun importFilms(films :Flux<Film>):Flux<Film> {
        println("Import Films")
        return filmRepository.insert(films)
    }

    fun findByYear(year: Int): Flux<Film> {
        val start: LocalDate = LocalDate.of(year,1,1)
        val endDate: LocalDate= LocalDate.of(year,12,31)

        return filmRepository.findFilmByWatchdateBetween(start,endDate)
    }

    fun findByName(name: String): Flux<Film> {
        return filmRepository.findByName(name)
    }
}