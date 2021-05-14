package com.example.knotsandcrosses.dialogs

interface GameDialogListener {
    fun onDialogCreateGame(player:String)
    fun onDialogJoinGame(player: String, gameId:String)
}
