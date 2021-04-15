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

var listOnFirebase = ""
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

        if (ListHolder.PickedList == null) {
            setResult(RESULT_CANCELED, Intent().apply {})
            finish()
        }
        list = ListHolder.PickedList!!

        listOnFirebase = list.toString().replace("TodoList(dbList=", "").replace(")", "")

        db.collection("ListGroups")
            .document(listOnFirebase)
            .collection(listOnFirebase)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    Log.d(TAG, "${document.id} => ${document.data}")
                    val dataDoneMark = document.data.toString().replace("{done=", "").replace("}", "")
                    var itemFirebase: Items =
                        if (dataDoneMark == "true") {
                            Items(document.id, isChecked = true)
                        } else {
                            Items(document.id, isChecked = false)
                        }
                    ItemClass.instance.addItem(itemFirebase)
                }
            }
            .addOnFailureListener { e -> Log.w(TAG, "Error ", e) }

        binding.listName.text = listOnFirebase

        db.collection("Progress")
            .document(listOnFirebase)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.w(TAG, "Error", e)
                }

                if (snapshot != null && snapshot.exists()) {
                    Log.d(TAG, "Data now: ${snapshot.data}")
                    binding.pbTwo.progress = snapshot.data.toString().replace("{progress=", "").replace("}", "").toInt()
                } else {
                    Log.d(TAG, "No data")
                }
            }

        btnAddItem.setOnClickListener {
            val todoTitle = etNewItem.text.toString()

            if(todoTitle.isNotEmpty()) {
                var todo = Items(todoTitle, false)
                val listOnFirebase = list.toString().replace("TodoList(dbList=", "").replace(")", "")
                val todoy = hashMapOf(
                    "done" to false
                )

                db.collection("ListGroups")
                    .document(listOnFirebase)
                    .collection(listOnFirebase)
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
            val listFromFirebase = list.toString().replace("TodoList(dbList=", "").replace(")", "")

            ItemClass.instance.deleteChecked()

            db.collection("ListGroups")
                .document(listFromFirebase)
                .collection(listFromFirebase)
                .whereEqualTo("done", true)
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        db.collection("Items")
                            .document(listFromFirebase)
                            .collection(listFromFirebase)
                            .document(document.id)
                            .delete()
                            .addOnSuccessListener {
                                Log.d(TAG, "Successfully deleted!")
                                val doc = hashMapOf("progress" to 0)
                                db.collection("Progress")
                                    .document(listOnFirebase)
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