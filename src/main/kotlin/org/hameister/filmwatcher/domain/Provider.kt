package org.hameister.filmwatcher.domain

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue

data class Provider(@JsonValue val provider: String){
    private companion object {
        @JsonCreator
        @JvmStatic
        fun valueOf(value: String) = Provider(value)
    }
}