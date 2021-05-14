package com.example.knotsandcrosses

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import com.example.knotsandcrosses.GameManager.StartingGameState
import com.example.knotsandcrosses.api.data.Game
import com.example.knotsandcrosses.api.data.GameState
import com.example.knotsandcrosses.databinding.ActivityGameBinding

class GameActivity : AppCompatActivity() {

    private lateinit var binding:ActivityGameBinding

    private var playerSign: String = "X"
    private var opponentSign: String = "O"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var game: Game? = intent.getParcelableExtra("game")

        game?.let {
            loadState(it.state)
        }
        game?.let {
            loadPlayerNames(it)
        }
        buttonInitializer(game)
        binding.gameIdText.text = game?.gameId.toString()

        val mainHandler = Handler(Looper.getMainLooper())

        mainHandler.post(object : Runnable {
            override fun run() {
                if (game?.gameId != null) {
                    GameManager.pollGame(game?.gameId.toString()) { newGame: Game? ->
                        if (game?.players != newGame?.players && newGame != null)
                            with(binding) {
                                playerName1Text.text = newGame.players[0]
                                playerName2Text.text = newGame.players[1]
                            }

                        if (game?.state != newGame?.state && newGame != null) {
                            game = newGame
                            game?.let {
                                loadState(it.state)
                            }
                            buttonInitializer(game)
                            game?.let {
                                checkGameEnd(it.state)
                            }
                        }
                    }
                }
                mainHandler.postDelayed(this, 200)
            }
        })
    }

    private fun buttonInitializer(game: Game?) {
        binding.square00.setOnClickListener { makeMove(game, 0, 0) }
        binding.square01.setOnClickListener { makeMove(game, 0, 1) }
        binding.square02.setOnClickListener { makeMove(game, 0, 2) }
        binding.square10.setOnClickListener { makeMove(game, 1, 0) }
        binding.square11.setOnClickListener { makeMove(game, 1, 1) }
        binding.square12.setOnClickListener { makeMove(game, 1, 2) }
        binding.square20.setOnClickListener { makeMove(game, 2, 0) }
        binding.square21.setOnClickListener { makeMove(game, 2, 1) }
        binding.square22.setOnClickListener { makeMove(game, 2, 2) }
    }

    private fun makeMove(game: Game?, row: Int, column: Int) {
        val gameBoard = game?.state.toString()
        val gameXTurn = gameBoard.count{c -> c == 'X'} <= gameBoard.count{c -> c == 'O'}
        if ((playerSign == "X" && gameXTurn) || (playerSign == "O" && !gameXTurn)) {
            if (game != null && game.state[row][column] == "0") {
                game.state[row][column] = playerSign
                game.state.let {
                    GameManager.updateGame(game.gameId, it)
                }
            }
        }
        game?.let { loadState(it.state) }
        game?.let { checkGameEnd(it.state) }
    }

    fun loadState(state: GameState) {
        val displayState:GameState = StartingGameState
        for (row in displayState.indices) {
            for (column in displayState[row].indices) {
                displayState[row][column] = state[row][column]
                if (displayState[row][column] == "0") {
                    displayState[row][column] = ""
                }
            }
        }
        binding.square00.text = displayState[0][0]
        binding.square01.text = displayState[0][1]
        binding.square02.text = displayState[0][2]
        binding.square10.text = displayState[1][0]
        binding.square11.text = displayState[1][1]
        binding.square12.text = displayState[1][2]
        binding.square20.text = displayState[2][0]
        binding.square21.text = displayState[2][1]
        binding.square22.text = displayState[2][2]
    }

    fun loadPlayerNames(game: Game) {
        playerSign = "X"
        opponentSign = "O"
        binding.playerName1Text.text = game.players[0]
        if (game.players.size > 1) {
            playerSign = "O"
            opponentSign = "X"
            binding.playerName2Text.text = game.players[1]
        }
    }

    fun checkGameEnd(state: GameState) {
        for (row in state.indices) {
            if (state[row][0] != "0") {
                if (state[row][0] == state[row][1] && state[row][1] == state[row][2]) {
                    gameEnd(state[row][0])
                }
            }
        }
        for (column in state[0].indices) {
            if (state[0][column] != "0") {
                if (state[0][column] == state[1][column] && state[1][column] == state[2][column]) {
                    gameEnd(state[0][column])
                }
            }
        }
        if (state[1][1] != "0") {
            if (state[0][0] == state[1][1] && state[1][1] == state[2][2]) {
                gameEnd(state[1][1])
            } else if (state[0][0] == state[1][1] && state[1][1] == state[2][2]) {
                gameEnd(state[1][1])
            }
        }
        if ("0" !in state.flatten()) {
            gameEnd("Game ended in Tie")
        }
    }

    private fun gameEnd(winCondition:String) {
        val intent = Intent(this, GameEndActivity::class.java).apply {
            putExtra("winningPlayer", winCondition)
        }
        startActivity(intent)
    }

}