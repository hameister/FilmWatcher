package org.hameister.filmwatcher.business

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.hameister.filmwatcher.data.FilmRepository
import org.hameister.filmwatcher.domain.Film
import java.io.File
import java.time.Duration

class JsonImporter(val filemane: String) {
    fun importFilms(repository: FilmRepository) {
        val mapper = jacksonObjectMapper()
        mapper.registerKotlinModule()
        mapper.registerModule(JavaTimeModule())

        val jsonString: String = File(filemane).readText(Charsets.UTF_8)
        val films: List<Film> = mapper.readValue<List<Film>>(jsonString)

        repository.insert(films).blockLast(Duration.ofSeconds(2))
    }
}