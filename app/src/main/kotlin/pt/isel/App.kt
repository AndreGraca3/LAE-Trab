package pt.isel

import pt.isel.autorouter.autorouterDynamic
import pt.isel.autorouter.jsonServer
import pt.isel.autorouter.watchNewFilesContent
import pt.isel.controllers.ClassroomController
import pt.isel.controllers.Watch
import java.io.File

fun main() {
    // ClassroomController().autorouterReflect().jsonServer().start(4000)
    Watch().autorouterDynamic().jsonServer().start(4000)

    val path = File("watched").toPath()
    val sequence = path.watchNewFilesContent()

    sequence.onEach { println(">> ${it.toList()}") }.toList()
}
