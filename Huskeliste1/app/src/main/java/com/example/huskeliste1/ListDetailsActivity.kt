package com.example.huskeliste1

import android.content.Intent
import android.os.Bundle
import com.google.firebase.ktx.Firebase
import android.util.Log
import com.google.firebase.firestore.ktx.firestore
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_list_details.*
import com.example.huskeliste1.data.TodoList
import com.example.huskeliste1.data.Items
import com.example.huskeliste1.databinding.ActivityListDetailsBinding

var receivedListFormatted = ""
val TAG = "ListDetailsActivity"

class ListDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityListDetailsBinding
    lateinit var list:TodoList

    override fun onCreate(savedInstanceState: Bundle?) {
        val db = Firebase.firestore

        super.onCreate(savedInstanceState)
        binding = ActivityListDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.rvListItems.layoutManager = LinearLayoutManager(this)
        binding.rvListItems.adapter = ItemsCollectionAdapter(emptyList<Items>())

        ItemClass.instance.onItems = {
            (binding.rvListItems.adapter as ItemsCollectionAdapter).updateItemCollection(it)
        }

        ItemClass.instance.loadItems()

        if(ListHolder.PickedList != null){
            list = ListHolder.PickedList!!
            Log.i("Details view", list.toString())

            receivedListFormatted = list.toString().replace("TodoList(dbList=", "")

            db.collection("ListGroups")
                .document(receivedListFormatted.replace(")", ""))
                .collection(receivedListFormatted.replace(")", ""))
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        Log.d(TAG, "${document.id} => ${document.data}")
                        val dataForm = document.data.toString().replace("{done=", "")
                        val formattedDone = dataForm.replace("}", "")
                        println(formattedDone)
                        var itemFirebase: Items
                        if (formattedDone == "true") {
                            itemFirebase = Items(document.id, isChecked = true)
                        } else {
                            itemFirebase = Items(document.id, isChecked = false)
                        }

                        ItemClass.instance.addItem(itemFirebase)
                    }
                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Error getting documents: ", exception)
                }

            binding.listName.text = receivedListFormatted.replace(")", "")

        } else{
            setResult(RESULT_CANCELED, Intent().apply {})
            finish()
        }

        db.collection("Progress")
            .document(receivedListFormatted.replace(")", ""))
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e)
                    return@addSnapshotListener
                }

                if (snapshot != null && snapshot.exists()) {
                    Log.d(TAG, "Current data: ${snapshot.data}")
                    val progress = snapshot.data.toString().replace("{progress=", "")
                    val formattedProgress = progress.replace("}", "")
                    binding.pbTwo.progress = formattedProgress.toInt() // Updates progress bar value
                } else {
                    Log.d(TAG, "Current data: null")
                }
            }

        btnAddItem.setOnClickListener {
            val todoTitle = etNewItem.text.toString()

            if(todoTitle.isNotEmpty()) {
                var todo = Items(todoTitle, false)
                val receivedListFormatted = list.toString().replace("TodoList(dbList=", "")
                val todoy = hashMapOf(
                    "done" to false
                )

                db.collection("ListGroups")
                    .document(receivedListFormatted.replace(")", ""))
                    .collection(receivedListFormatted.replace(")", ""))
                    .document(todoTitle)
                    .set(todoy)
                    .addOnSuccessListener {
                        Log.d(TAG, "Added item: $todoTitle")
                        todo = Items(todoTitle, false)
                    }
                    .addOnFailureListener { e ->
                        Log.w(TAG, "Error when adding item", e)
                    }

                ItemClass.instance.addItem(todo)
                etNewItem.text.clear()
            }
        }

        btnDeleteItem.setOnClickListener {
            val receivedBookFormatted = list.toString().replace("TodoList(dbList=", "")

            ItemClass.instance.deleteChecked()

            db.collection("ListGroup")
                .document(receivedBookFormatted.replace(")", ""))
                .collection(receivedBookFormatted.replace(")", ""))
                .whereEqualTo("done", true)
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        db.collection("Items")
                            .document(receivedBookFormatted.replace(")", ""))
                            .collection(receivedBookFormatted.replace(")", ""))
                            .document(document.id)
                            .delete()
                            .addOnSuccessListener {
                                Log.d(TAG, "DocumentSnapshot successfully deleted!")

                                // Returns progress bar to 0
                                val doc = hashMapOf(
                                    "progress" to 0
                                )

                                db.collection("Progress")
                                    .document(receivedListFormatted.replace(")", ""))
                                    .set(doc)
                                    .addOnSuccessListener {
                                        Log.d(TAG, "Changed progress")
                                    }
                                    .addOnFailureListener {
                                        Log.w(TAG, "Failed to change progress!")
                                    }

                            }
                            .addOnFailureListener { e -> Log.w(TAG, "Error deleting document", e) }
                    }
                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Error getting documents: ", exception)
                }
        }
    }

    override fun onBackPressed() {
        finish()
    }
}