package pt.isel

import pt.isel.autorouter.watchNewFilesContent
import java.io.File

fun main() {
    // ClassroomController().autorouterReflect().jsonServer().start(4000)
    // ClassroomController().autorouterDynamic().jsonServer().start(4000)

    val path = File("watched").toPath()
    val sequence = path.watchNewFilesContent()
//    sequence.forEach {
//        val iterator = it.iterator()
//        while (iterator.hasNext()) {
//            println(iterator.next())
//        }
//    }
    for (seq in sequence){
        for (line in seq){
            println(line)
        }
    }

}
