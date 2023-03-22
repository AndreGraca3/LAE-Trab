package pt.isel

import pt.isel.autorouter.ArVerb
import pt.isel.autorouter.annotations.ArBody
import pt.isel.autorouter.annotations.ArQuery
import pt.isel.autorouter.annotations.ArRoute
import pt.isel.autorouter.annotations.AutoRoute
import java.util.*

class ourController {

    val teams = mutableMapOf(
        "Benfica" to listOf(
            Player(88,"Gonçalo Ramos","Avançado"),
            Player(27,"Rafa","Avançado"),
            Player(20,"João Mário","Médio"),
            Player(66,"António Silva","Defesa"),
            Player(6,"Bah","Defesa")
        ),
        "Sporting" to listOf(
            Player(11,"Nuno Santos","Avançado"),
            Player(17,"Francisco Trincão","Avançado"),
            Player(20,"Paulinho","Avançado"),
            Player(13,"Luís Neto","Defesa"),
            Player(1,"António Adán","Guarda-Redes")
        )
    )

    /**
     * Example:
     *  http://localhost:4000/teams/Benfica?player=Ant
     */
    @Synchronized
    @AutoRoute(path = "/teams/{team}")
    fun searchPlayer(@ArRoute team : String,@ArQuery player : String?): Optional<List<Player>>{
        if(teams[team] == null) return Optional.empty()
        if(player == null) return Optional.of(teams[team]!!)
        return Optional.of(teams[team]!!.filter { it.name.contains(player) })
    }

    /**
     * Example:
     *  http://localhost:4000/teams/Benfica/player/19
     *  body : { number : 19 , name : "Luís Falcão" , Position : "Avançado" }
     */
    @Synchronized
    @AutoRoute(path = "/teams/{team}/player/{number}", method = ArVerb.PUT)
    fun addPlayer(@ArRoute team : String,@ArRoute number : Int,@ArBody player : Player) : Optional<Player> {
        if(number != player.number || teams[team] == null) return Optional.empty()
        teams[team] = teams[team]!!.filter{ it.number != number} + player
        return Optional.of(player)
    }

    @Synchronized
    @AutoRoute(path = "/teams/{team}/player/{number}", method = ArVerb.DELETE)
    fun removePlayer(team : String, number : Int) : Optional<Player> {
        if(teams[team] == null || teams[team]?.filter { it.number == number } == null) return Optional.empty()
        val player = teams[team]?.firstOrNull { it.number == number } ?: return Optional.empty()
        teams[team] = teams[team]!!.filter { it.number != number}
        return Optional.of(player)
    }

}