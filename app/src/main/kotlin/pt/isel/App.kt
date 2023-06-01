package pt.isel

import pt.isel.autorouter.autorouterDynamic
import pt.isel.autorouter.jsonServer
import pt.isel.controllers.Watch

fun main() {
    // ClassroomController().autorouterReflect().jsonServer().start(4000)
    Watch().autorouterDynamic().jsonServer().start(4000)
}
