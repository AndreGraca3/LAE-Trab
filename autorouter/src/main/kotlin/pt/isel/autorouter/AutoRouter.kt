package pt.isel.autorouter

import java.util.stream.Stream

fun Stream<ArHttpRoute>.jsonServer() = JsonServer(this)

fun Any.autorouterReflect() = AutoRouterReflect(this).autorouterReflect()

fun Any.autorouterDynamic() = AutoRouterDynamic(this).autorouterDynamic()