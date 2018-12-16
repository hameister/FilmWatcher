package org.hameister.filmwatcher.business

import org.hameister.filmwatcher.data.FilmRepository
import org.hameister.filmwatcher.domain.Film
import org.hameister.filmwatcher.domain.Provider
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class FilmService( val filmRepository: FilmRepository) {

    fun findAll():Flux<Film> = filmRepository.findAll()
    fun findById(id : Long) : Mono<Film> = filmRepository.findById(id)


    fun findAllWatchedOnDvd(): Flux<Film> {
        return filmRepository.findByProvider(Provider("DVD"))
    }

    fun findAllWatchedOnNetflix(): Flux<Film> {
        return filmRepository.findByProvider(Provider("Netflix"))
    }

    fun  findAllWatchedOn( provider:String) : Flux<Film>{
        when(provider){
            "DVD" -> return findAllWatchedOnDvd()
            "Netflix" -> return findAllWatchedOnNetflix()
        }
        return Flux.empty()
    }
}