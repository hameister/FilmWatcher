package org.hameister.filmwatcher.domain

import com.fasterxml.jackson.annotation.JsonFormat
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Duration
import java.time.LocalDate

@Document
data class Film(val name: String, val provider: Provider) {


    companion object {
        const val NOT_AVAILABLE = 0
        const val NOT_A_SERIE = "N/A"
    }
    @get:JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "d.M.yyyy")
    var watchdate:LocalDate?=null

    @Id  var  id: ObjectId = ObjectId()

    var duration: Duration = Duration.ZERO
    var seriesNo: Int = NOT_AVAILABLE
    var episodeNo: Int = NOT_AVAILABLE
    var episodeName: String = NOT_A_SERIE

}