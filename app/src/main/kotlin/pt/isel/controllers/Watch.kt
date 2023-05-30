package pt.isel.controllers

import pt.isel.autorouter.annotations.ArSequenceResponse
import pt.isel.autorouter.annotations.AutoRoute
import java.io.File
import java.nio.file.Path
import java.util.*

class Watch {

    private val path: Path =  File("watched").toPath()

    @Synchronized
    @AutoRoute(path = "/watch")
    @ArSequenceResponse
    fun watch(): Optional<String> {
        return Optional.of("Isto é uma String!");
    }
}