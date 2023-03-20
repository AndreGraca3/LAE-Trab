package pt.isel

import com.fasterxml.jackson.annotation.JsonProperty

data class Player(
    @JsonProperty("number") val number: Int,
    @JsonProperty("name") val name: String,
    @JsonProperty("position") val position: String)