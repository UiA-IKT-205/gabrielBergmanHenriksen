package com.example.knotsandcrosses

import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import com.example.knotsandcrosses.api.GameService
import com.example.knotsandcrosses.api.data.Game
import com.example.knotsandcrosses.databinding.ActivityMainBinding
import com.example.knotsandcrosses.dialogs.CreateGameDialog
import com.example.knotsandcrosses.dialogs.GameDialogListener
import com.example.knotsandcrosses.dialogs.JoinGameDialog
import android.content.Intent

class MainActivity : AppCompatActivity() , GameDialogListener {

    val TAG:String = "MainActivity"

    lateinit var binding:ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.startGameButton.setOnClickListener {
            createNewGame()
        }

        binding.joinGameButton.setOnClickListener {
            joinGame()
        }
    }

    private fun createNewGame(){
        val dlg = CreateGameDialog()
        dlg.show(supportFragmentManager,"CreateGameDialogFragment")
    }

    private fun joinGame(){
        val dlg = JoinGameDialog()
        dlg.show(supportFragmentManager,"CreateGameDialogFragment")
    }

    override fun onDialogCreateGame(player: String) {
        Log.d(TAG, player)
        GameManager.createGame(player)
        Thread.sleep(1_000)
        val intent = Intent(this, GameActivity::class.java).apply {
            putExtra("playerName", player)
        }
        startActivity(intent)
    }

    override fun onDialogJoinGame(player: String, gameId: String) {
        Log.d(TAG, "$player $gameId")
        GameManager.joinGame(player, gameId)
        Thread.sleep(1_000)
        val intent = Intent(this, GameActivity::class.java).apply {
            putExtra("playerName", player)
        }
        startActivity(intent)
    }

}