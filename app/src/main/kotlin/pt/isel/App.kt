package pt.isel

import pt.isel.autorouter.watchNewFilesContent
import java.io.File

fun main() {
    // ClassroomController().autorouterReflect().jsonServer().start(4000)
    // ClassroomController().autorouterDynamic().jsonServer().start(4000)

    val path = File("watched").toPath()
    val sequence = path.watchNewFilesContent()

    sequence.onEach { println(">> ${it.toList()}") }.toList()
}
