package com.example.knotsandcrosses.api


import android.util.Log
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.example.knotsandcrosses.App
import com.example.knotsandcrosses.R
import com.example.knotsandcrosses.api.data.Game
import com.example.knotsandcrosses.api.data.GameState
import org.json.JSONObject
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.json.JSONArray


typealias GameServiceCallback = (state:Game?, errorCode:Int? ) -> Unit

object GameService {

    private val context = App.context
    private const val TAG:String = "GameService"

    private val requestQue:RequestQueue = Volley.newRequestQueue(context)

    private enum class APIEndpoints(val url:String) {
        CREATE_GAME("%1s%2s%3s".format(context.getString(R.string.protocol), context.getString(R.string.domain), context.getString(R.string.base_path)))
    }

    fun createGame(playerId:String, state:GameState, callback:GameServiceCallback) {

        val url = APIEndpoints.CREATE_GAME.url

        val requestData = JSONObject()
        requestData.put("player", playerId)
        requestData.put("state", state)

        val request = object : JsonObjectRequest(Request.Method.POST, url, requestData,
            {
                it.put("state", JSONArray(Json.decodeFromString<MutableList<MutableList<Char>>>(it.get("state").toString())))
                val game = Gson().fromJson(it.toString(0), Game::class.java)
                callback(game,null)
            }, {
                // Error creating new game
                callback(null, it.networkResponse.statusCode)
            } ) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Content-Type"] = "application/json"
                headers["Game-Service-Key"] = context.getString(R.string.game_service_key)
                return headers
            }
        }

        requestQue.add(request)
    }

    fun joinGame(playerId:String, gameId:String, callback: GameServiceCallback){
        val url = APIEndpoints.CREATE_GAME.url+"/"+gameId+"/join"
        val requestData = JSONObject()
        requestData.put("player", playerId)
        requestData.put("gameId", gameId)

        val request = object : JsonObjectRequest(Method.POST, url, requestData,
            {
                val players = Json.decodeFromString<MutableList<String>>(it.get("players").toString())
                val gameId:String = it.get("gameId").toString()
                val stateString:String = it.get("state").toString()
                    .replace("[^\"][0XO][^\"]".toRegex()) { it -> it.value[0]+"\""+it.value[1]+"\""+it.value[2]}
                val state = Json.decodeFromString<MutableList<MutableList<String>>>(stateString)
                val game = Game(players, gameId, state)
                Log.d(TAG, "joinGame: $game")
                callback(game,null)
            }, {
                // Error joining game
                callback(null, it.networkResponse.statusCode)
            } ) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Content-Type"] = "application/json"
                headers["Game-Service-Key"] = context.getString(R.string.game_service_key)
                return headers
            }
        }
        requestQue.add(request)
    }

    fun updateGame(gameId: String, gameState:GameState, callback: GameServiceCallback){
        val url = APIEndpoints.CREATE_GAME.url+"/"+gameId+"/update"
        val requestData = JSONObject()
        requestData.put("gameId", gameId)
        requestData.put("state", gameState)

        val request = object : JsonObjectRequest(Method.POST, url, requestData,
            {
                val players = Json.decodeFromString<MutableList<String>>(it.get("players").toString())
                val gameId:String = it.get("gameId").toString()
                val stateString:String = gameState.toString()
                    .replace("[^\"][0XO][^\"]".toRegex()) { it -> it.value[0]+"\""+it.value[1]+"\""+it.value[2]}
                val state = Json.decodeFromString<MutableList<MutableList<String>>>(stateString)
                val game = Game(players, gameId, state)
                Log.d(TAG,"updateGame: $game")
                callback(game,null)
            }, {
                // Error updating game
                callback(null, it.networkResponse.statusCode)
            } ) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Content-Type"] = "application/json"
                headers["Game-Service-Key"] = context.getString(R.string.game_service_key)
                return headers
            }
        }
        requestQue.add(request)
    }

    fun pollGame(gameId:String, callback:GameServiceCallback){
        val url = APIEndpoints.CREATE_GAME.url+"/"+gameId+"/poll"
        val requestData = JSONObject()
        val request = object : JsonObjectRequest(Method.GET, url, requestData,
            {
                val players = Json.decodeFromString<MutableList<String>>(it.get("players").toString())
                val stateString:String = it.get("state").toString()
                    .replace("[^\"][0XO][^\"]".toRegex()) { it -> it.value[0]+"\""+it.value[1]+"\""+it.value[2]}
                val state = Json.decodeFromString<MutableList<MutableList<String>>>(stateString)
                val game = Game(players, gameId, state)
                Log.d(TAG,"pollGame: $game")
                callback(game,null)
            }, {
                // Error fetching game
                callback(null, it.networkResponse.statusCode)
            } ) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Content-Type"] = "application/json"
                headers["Game-Service-Key"] = context.getString(R.string.game_service_key)
                return headers
            }
        }
        requestQue.add(request)
    }

}