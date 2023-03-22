/*
 * This Kotlin source file was generated by the Gradle 'init' task.
 */
package pt.isel

import pt.isel.autorouter.ArHttpRoute
import pt.isel.autorouter.autorouterDynamic
import pt.isel.autorouter.autorouterReflect
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

class AutoRouterTestForFootball {

    @Test
    fun get_players_via_reflection() {
        get_Players(
            FootballController().autorouterReflect().toList()
        )
    }

    @Test
    fun get_players_via_dynamic() {
        get_Players(
            ClassroomController().autorouterDynamic().toList()
        )
    }

    fun get_Players(routes: List<ArHttpRoute>) {
        val r = routes.first { it.path == "/teams/{team}" }
        val res = r.handler.handle(
            mapOf("team" to "Benfica"),
            emptyMap(),
            emptyMap()
        )
        assertContentEquals(
            listOf(
                Player(88, "Gonçalo Ramos", "Avançado"),
                Player(27, "Rafa", "Avançado"),
                Player(20, "João Mário", "Médio"),
                Player(66, "António Silva", "Defesa"),
                Player(6, "Pah", "Defesa")
            ),
            res.get() as List<Player>
        )
        println(Player(88, "Gonçalo Ramos", "Avançado").toString())
    }

    @Test
    fun get_players_with_name_containing_word_via_reflection() {
        get_players_with_name_containing_word(
            FootballController().autorouterReflect().toList()
        )
    }

    @Test
    fun get_players_with_name_containing_word_via_dynamic() {
        get_players_with_name_containing_word(
            FootballController().autorouterDynamic().toList()
        )
    }


    fun get_players_with_name_containing_word(routes: List<ArHttpRoute>) {
        val r = routes.first { it.path == "/teams/{team}" }
        val res = r.handler.handle(
            mapOf("team" to "Benfica"),
            mapOf("player" to "Ant"),
            emptyMap()
        )
        assertContentEquals(
            listOf(Player(66, "António Silva", "Defesa")),
            res.get() as List<Player>
        )
    }

    @Test
    fun add_player_via_reflection() {
        add_Player(
            FootballController().autorouterReflect().toList()
        )
    }

    fun add_Player(routes: List<ArHttpRoute>) {
        val r = routes.first { it.path == "/teams/{team}/player/{number}" }
        val res = r.handler.handle(
            mapOf(
                "team" to "Benfica",
                "number" to "19"
            ),
            emptyMap(),
            mapOf(
                "player" to Player(19, "Luís Falcão", "Avançado").toString()
            )
        )
        assertEquals(
            Player(19, "Luís Falcão", "Avançado"),
            res.get() as Player
        )
    }
}

