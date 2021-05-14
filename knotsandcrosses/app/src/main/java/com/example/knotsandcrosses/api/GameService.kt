package com.example.knotsandcrosses.api


import android.util.Log
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.example.knotsandcrosses.App
import com.example.knotsandcrosses.R
import com.example.knotsandcrosses.api.data.Game
import com.example.knotsandcrosses.api.data.GameState
import org.json.JSONObject
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.json.JSONArray


typealias GameServiceCallback = (state:Game?, errorCode:Int? ) -> Unit

/*  NOTE:
    Using object expression to make GameService a Singleton.
    Why? Because there should only be one active GameService ever.
 */

object GameService {

    /// NOTE: Do not want to have App.context all over the code. Also it is nice if we later want to support different contexts
    private val context = App.context

    /// NOTE: God practice to use a que for performing requests.
    private val requestQue:RequestQueue = Volley.newRequestQueue(context)

    const val TAG:String = "GameService"

    /// NOTE: One posible way of constructing a list of API url. You want to construct the urls so that you can support different environments (i.e. Debug, Test, Prod etc)
    private enum class APIEndpoints(val url:String) {
        CREATE_GAME("%1s%2s%3s".format(context.getString(R.string.protocol), context.getString(R.string.domain),context.getString(R.string.base_path)))
    }


    fun createGame(playerId:String, state:GameState, callback:GameServiceCallback) {

        val url = APIEndpoints.CREATE_GAME.url

        val requestData = JSONObject()
        requestData.put("player", playerId)
        requestData.put("state", state)
        Log.d(TAG, state.toString())

        val request = object : JsonObjectRequest(Request.Method.POST, url, requestData,
            {
                // Success game created.
                Log.d(TAG, it.toString())
                it.put("state", JSONArray(Json.decodeFromString<MutableList<MutableList<Char>>>(it.get("state").toString())))
                Log.d(TAG, it.toString())
                val game = Gson().fromJson(it.toString(0), Game::class.java)
                callback(game,null)
            }, {
                // Error creating new game.
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
        requestData.put("gameId",gameId)

        val request = object : JsonObjectRequest(Method.POST, url, requestData,
            {
                val players = Json.decodeFromString<MutableList<String>>(it.get("players").toString())
                val gameId:String = it.get("gameId").toString()
                val stateString:String = it.get("state").toString()
                    .replace("[^\"][0XO][^\"]".toRegex()) { it -> it.value[0]+"\""+it.value[1]+"\""+it.value[2]}
                val state = Json.decodeFromString<MutableList<MutableList<String>>>(stateString)

                val game = Game(players, gameId, state)
                Log.d(TAG, "Joining : $game")
                callback(game,null)
            }, {
                // Error joining game.
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
                Log.d(TAG,"Updating : $game")
                callback(game,null)
            }, {
                // Error updating game.
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
        val request = object : JsonObjectRequest(Method.GET,url, requestData,
            {
                val players = Json.decodeFromString<MutableList<String>>(it.get("players").toString())
                val stateString:String = it.get("state").toString()
                    .replace("[^\"][0XO][^\"]".toRegex()) { it -> it.value[0]+"\""+it.value[1]+"\""+it.value[2]}
                Log.d(TAG, stateString)
                val state = Json.decodeFromString<MutableList<MutableList<String>>>(stateString)

                val game = Game(players, gameId, state)
                Log.d(TAG,"pollGame : $game")
                Log.d("HUIHDASU", game.toString())
                callback(game,null)
            }, {
                // Error refreshing game.
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