package com.example.knotsandcrosses

import com.example.knotsandcrosses.databinding.ActivityMainBinding
import com.example.knotsandcrosses.dialogs.CreateGameDialog
import com.example.knotsandcrosses.dialogs.GameDialogListener
import com.example.knotsandcrosses.dialogs.JoinGameDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.content.Intent

class MainActivity : AppCompatActivity() , GameDialogListener {

    private val TAG:String = "MainActivity"

    lateinit var binding:ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.startGameButton.setOnClickListener { createNewGame() }
        binding.joinGameButton.setOnClickListener { joinGame() }
    }

    private fun createNewGame(){
        val dlg = CreateGameDialog()
        dlg.show(supportFragmentManager,"CreateGameDialogFragment")
    }

    private fun joinGame(){
        val dlg = JoinGameDialog()
        dlg.show(supportFragmentManager,"JoinGameDialogFragment")
    }

    override fun onDialogCreateGame(player: String) {
        Log.d(TAG, player)
        GameManager.createGame(player)
        val intent = Intent(this, GameActivity::class.java).apply {
            putExtra("playerName", player)
        }
        startActivity(intent)
    }

    override fun onDialogJoinGame(player: String, gameId: String) {
        Log.d(TAG, "$player $gameId")
        GameManager.joinGame(player, gameId)
        val intent = Intent(this, GameActivity::class.java).apply {
            putExtra("playerName", player)
        }
        startActivity(intent)
    }

}