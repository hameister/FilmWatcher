package org.hameister.filmwatcher.domain

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDate

@Document
data class Film(@Id val id:Long, val name:String, val watchdate:LocalDate, val provider:Provider) {

    companion object {
        const val NOT_AVAILABLE = 0
        const  val  NOT_A_SERIE = "N/A"
    }

    var seriesNo : Int= NOT_AVAILABLE
    var episodeNo : Int= NOT_AVAILABLE
    var episodeName :String= NOT_A_SERIE

}