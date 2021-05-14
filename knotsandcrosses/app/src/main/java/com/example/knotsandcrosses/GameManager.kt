package com.example.knotsandcrosses

import android.content.Intent
import android.util.Log
import com.example.knotsandcrosses.App.Companion.context
import com.example.knotsandcrosses.api.GameService
import com.example.knotsandcrosses.api.data.Game
import com.example.knotsandcrosses.api.data.GameState

typealias GameManagerCallback = (game:Game?) -> Unit

object GameManager {

    private const val TAG:String = "GameManager"

    val StartingGameState:GameState = mutableListOf(mutableListOf("0","0","0"), mutableListOf("0","0","0"), mutableListOf("0","0","0"))

    fun createGame(player:String){
        GameService.createGame(player, StartingGameState) { game: Game?, err: Int? ->
            if(err != null){
                Log.d(TAG, "Error creating game, code: $err")
            } else {
                val intent = Intent(context, GameActivity::class.java)
                intent.putExtra("game", game)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK;
                context.startActivity(intent)
            }
        }
    }

    fun joinGame(player:String, gameId:String){
        GameService.joinGame(player, gameId) { game: Game?, err: Int? ->
            if(err != null){
                Log.d(TAG, "Error joining game, code : $err")
            } else {
                val intent = Intent(context, GameActivity::class.java)
                intent.putExtra("game", game)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK;
                context.startActivity(intent)
            }
        }
    }

    fun pollGame(gameId:String, callback:GameManagerCallback){
        GameService.pollGame(gameId) { game: Game?, err: Int? ->
            if(err != null){
                Log.d(TAG, "Error fetching game, code : $err")
            } else {
                callback(game)
            }
        }

    }

    fun updateGame(gameId:String, state:GameState){
        GameService.updateGame(gameId, state) { _, err: Int? ->
            if(err != null){
                Log.d(TAG, "Error updating game, code : $err")
            }
        }
    }
}