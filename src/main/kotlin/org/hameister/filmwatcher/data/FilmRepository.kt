package org.hameister.filmwatcher.data

import org.hameister.filmwatcher.domain.Film
import org.hameister.filmwatcher.domain.Provider
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import reactor.core.publisher.Flux


interface FilmRepository: ReactiveMongoRepository<Film, Long> {
    fun findByProvider( provider: Provider):Flux<Film>
}



