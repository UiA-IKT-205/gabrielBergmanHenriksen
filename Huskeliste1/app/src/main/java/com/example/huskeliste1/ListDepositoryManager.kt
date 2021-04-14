package com.example.huskeliste1

import com.example.huskeliste1.data.TodoList
import android.util.Log
import android.content.Context
import com.google.firebase.ktx.Firebase
import com.google.firebase.firestore.ktx.firestore

class ListDepositoryManager {

    private lateinit var multiList: MutableList<TodoList>

    var onLists: ((List<TodoList>) -> Unit)? = null
    var onListUpdate: ((list: TodoList) -> Unit)? = null

    fun load(context: Context) {

        multiList = mutableListOf()

        val TAG = "ToDoListLists"
        val db = Firebase.firestore

        db.collection("ListGroups")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    Log.d(TAG, "${document.id} => ${document.data}")
                    val bookFirebase = TodoList(document.id)
                    addList(bookFirebase)
                }
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
            }

        onLists?.invoke(multiList)
    }

    fun addList(list: TodoList) {
        multiList.add(list)
        onLists?.invoke(multiList)
    }

    fun removeList(list: TodoList) {
        multiList.remove(list)
        onLists?.invoke(multiList)
    }

    companion object {
        val instance = ListDepositoryManager()
    }

}