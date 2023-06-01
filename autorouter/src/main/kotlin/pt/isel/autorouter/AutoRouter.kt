package pt.isel.autorouter

import pt.isel.autorouter.routers.AutoRouterDynamic
import pt.isel.autorouter.routers.AutoRouterReflect
import pt.isel.autorouter.server.JsonServer
import java.util.stream.Stream

fun Stream<ArHttpRoute>.jsonServer() = JsonServer(this)

fun Any.autorouterReflect() = AutoRouterReflect(this).autorouterReflect()

fun Any.autorouterDynamic() = AutoRouterDynamic(this).autorouterDynamic()