package com.example.knotsandcrosses
import com.example.knotsandcrosses.api.GameService
import com.example.knotsandcrosses.api.data.Game
import com.example.knotsandcrosses.GameManager.state
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {

    var gameState:Game? = null
    val firstPlayer:String = "Pizza"
    val secondPlayer:String = "Pasta"
    val initState = mutableListOf(mutableListOf("0","0","0"), mutableListOf("0","0","0"), mutableListOf("0","0","0"))

    @Test
    fun createGame(){
        GameService.createGame(firstPlayer, initState){ state:Game?, err:Int? ->
            gameState = state
            assertNotNull(state)
            assertNotNull(state?.gameId)
            assertEquals(firstPlayer, state?.players?.get(0))
        }
    }


    /*fun JoinGame(){
        GameService.joinGame(secondPlayer, gameState.gameId) {state:Game?, err:Int? ->
            gameState = state
            assertEquals(firstPlayer, state?.players?.get(0))
        }
    }*/


    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }
}