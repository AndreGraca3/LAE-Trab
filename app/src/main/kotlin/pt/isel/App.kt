package pt.isel

import pt.isel.autorouter.autorouterDynamic
import pt.isel.autorouter.jsonServer
import pt.isel.autorouter.watchNewFilesContent
import pt.isel.controllers.Watch
import java.io.File
import java.nio.file.Path
import java.util.*

fun main() {
    // ClassroomController().autorouterReflect().jsonServer().start(4000)
    Watch().autorouterDynamic().jsonServer().start(4000)

    val path: Path =  File("watched").toPath()
    val sequence = path.watchNewFilesContent()

    for (fileContent in sequence) {
        for (line in fileContent) {
            println("<p>$line</p>")
        }
        println("------------------------")
    }
}
