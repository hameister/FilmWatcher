package org.hameister.filmwatcher.business

import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.hameister.filmwatcher.data.FilmRepository
import org.hameister.filmwatcher.domain.Film
import org.hameister.filmwatcher.domain.Provider
import org.junit.Test
import reactor.core.publisher.Flux

class FilmServiceTest {
    @Test
    fun findAllWatchedOnShouldFilterProviders() {
        // given
        val filmRepository:FilmRepository = mockk()
        val filmService:FilmService = FilmService(filmRepository)
        val film1 : Film= Film("test", Provider("DVD"))
        val film2 : Film= Film("test", Provider("DVD"))

        val filmFlux : Flux<Film> = Flux.just(film1, film2)

        // when
        every { filmService.findAllWatchedOn("DVD") } returns filmFlux
        val findAllWatchedOn = filmService.findAllWatchedOn("DVD")

        //then
        assertThat(findAllWatchedOn.toIterable().toList().size).isEqualTo(2)
    }

    @Test
    fun findByName() {
        // given
        val filmRepository:FilmRepository = mockk()
        val filmService:FilmService = FilmService(filmRepository)
        val film1 : Film= Film("test", Provider("DVD"))
        val film2 : Film= Film("test2", Provider("DVD"))

        val filmFlux : Flux<Film> = Flux.just(film1, film2)

        // when
        every { filmService.findByName("test") } returns filmFlux
        val findAllWatchedOn = filmService.findByName("test")

        //then
        assertThat(findAllWatchedOn.toIterable().toList().size).isEqualTo(2)
    }
}