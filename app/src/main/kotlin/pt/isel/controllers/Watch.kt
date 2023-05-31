package pt.isel.controllers

import pt.isel.autorouter.annotations.AutoRoute
import pt.isel.autorouter.returnType
import pt.isel.autorouter.watchNewFilesContent
import java.io.File
import java.nio.file.Path
import java.util.*

class Watch {

    private val path: Path =  File("watched").toPath()

    @Synchronized
    @AutoRoute(path = "/watch", returnType = returnType.SEQUENCE)
    fun watch(): Optional<Sequence<Sequence<String>>> {
        val sequence = path.watchNewFilesContent()
        return Optional.of(sequence)
    }
}