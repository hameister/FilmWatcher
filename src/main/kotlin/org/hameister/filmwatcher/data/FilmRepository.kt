package org.hameister.filmwatcher.data

import org.bson.types.ObjectId
import org.hameister.filmwatcher.domain.Film
import org.hameister.filmwatcher.domain.Provider
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import reactor.core.publisher.Flux
import java.time.LocalDate


interface FilmRepository: ReactiveMongoRepository<Film, ObjectId> {
    fun findByProvider( provider: Provider):Flux<Film>
    fun  findFilmByWatchdateBetween(start:LocalDate, end:LocalDate): Flux<Film>
}



