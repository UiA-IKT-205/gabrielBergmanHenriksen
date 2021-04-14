package com.example.huskeliste1

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.example.huskeliste1.data.TodoList
import com.example.huskeliste1.databinding.ActivityMainBinding

class ListHolder{
    companion object{
        var PickedList:TodoList? = null
    }
}

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.ListGroupView.layoutManager = LinearLayoutManager(this)
        binding.ListGroupView.adapter = ListCollectionAdapter(emptyList<TodoList>(), this::onListClicked)

        ListDepositoryManager.instance.onLists = {
            (binding.ListGroupView.adapter as ListCollectionAdapter).updateCollection(it)
        }
        ListDepositoryManager.instance.load(this)

        binding.btSave.setOnClickListener {
            val dbList = binding.title.text.toString()
            binding.title.setText("")
            addList(dbList)

            val ipm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            ipm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
        }
    }

    private fun addList(dbList: String) {
        val list = TodoList(dbList)
        val db = Firebase.firestore

        val ex = hashMapOf(
            "exists" to 1
        )

        db.collection("ListGroups").document(dbList)
            .set(ex)
            .addOnSuccessListener {
                Log.d(TAG, "Exists ref added with ID: $dbList")
                db.collection("ListGroups").document(dbList)
                    .set(list)
                    .addOnSuccessListener {
                        Log.d(TAG, "List added with ID: $list")
                    }
                    .addOnFailureListener { e ->
                        Log.w(TAG, "Error adding List", e)
                    }
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding exists ref", e)
            }
        ListDepositoryManager.instance.addList(list)
    }

    private fun onListClicked(list: TodoList): Unit {
        ListHolder.PickedList = list
        val intent =Intent(this, ListDetailsActivity::class.java)
        startActivity(intent)
    }
}