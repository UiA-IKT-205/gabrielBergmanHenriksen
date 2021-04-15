package com.example.huskeliste1

import com.example.huskeliste1.data.TodoList
import android.util.Log
import android.content.Context
import com.google.firebase.ktx.Firebase
import com.google.firebase.firestore.ktx.firestore

class ListDepositoryManager {

    private lateinit var mutableList: MutableList<TodoList>

    var onLists: ((List<TodoList>) -> Unit)? = null

    fun load(context: Context) {

        mutableList = mutableListOf()

        val TAG = "ToDoListLists"
        val db = Firebase.firestore

        db.collection("ListGroups")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    Log.d(TAG, "${document.id} => ${document.data}")
                    addList(TodoList(document.id))
                }
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
            }

        onLists?.invoke(mutableList)
    }

    fun addList(list: TodoList) {
        mutableList.add(list)
        onLists?.invoke(mutableList)
    }

    fun removeList(list: TodoList) {
        mutableList.remove(list)
        onLists?.invoke(mutableList)
    }

    companion object {
        val instance = ListDepositoryManager()
    }

}