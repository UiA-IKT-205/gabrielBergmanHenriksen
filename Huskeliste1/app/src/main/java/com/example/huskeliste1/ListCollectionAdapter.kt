package com.example.huskeliste1

import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.ktx.Firebase
import android.view.ViewGroup
import com.example.huskeliste1.databinding.ListLayoutBinding
import kotlinx.android.synthetic.main.list_layout.view.*
import android.view.LayoutInflater
import com.example.huskeliste1.data.TodoList
import com.google.firebase.firestore.ktx.firestore

class ListCollectionAdapter(private var lists:List<TodoList>, private val onListClicked:(TodoList) -> Unit) : RecyclerView.Adapter<ListCollectionAdapter.ViewHolder>(){

    class ViewHolder(private val binding:ListLayoutBinding):RecyclerView.ViewHolder(binding.root) {
        fun bind(list: TodoList, onListClicked:(TodoList) -> Unit) {
            binding.title.text = list.dbList

            val TAG = "ListCollectionAdapter"
            val db = Firebase.firestore

            db.collection("Progress")
                .document(list.dbList)
                .addSnapshotListener { snapshot, e ->
                    if (e != null) {
                        Log.w(TAG, "Listen failed.", e)
                        return@addSnapshotListener
                    }

                    if (snapshot != null && snapshot.exists()) {
                        Log.d(TAG, "Current data: ${snapshot.data}")
                        val progress = snapshot.data.toString().replace("{progress=", "")
                        val formattedProgress = progress.replace("}", "")
                        binding.progressBar.progress = formattedProgress.toInt()
                    } else {
                        Log.d(TAG, "Current data: null")
                    }
                }

            binding.card.setOnClickListener {
                onListClicked(list)
            }
        }
    }

    override fun getItemCount(): Int = lists.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val list = lists[position]
        holder.bind(list,onListClicked)

        holder.itemView.apply {
            title.text = list.dbList

            deleteBt.setOnClickListener {
                val TAG = "ToDoListItems"
                val db = Firebase.firestore
                val doc = hashMapOf(
                    "progress" to 0
                )

                db.collection("ListGroups")
                    .document(title.text as String)
                    .collection(title.text as String)
                    .get()
                    .addOnSuccessListener { documents ->
                        for (document in documents) {
                            db.collection("ListGroups")
                                .document(title.text as String)
                                .collection(title.text as String)
                                .document(document.id)
                                .delete()
                                .addOnSuccessListener {
                                    db.collection("ListGroups")
                                        .document(title.text as String)
                                        .delete()
                                        .addOnSuccessListener {
                                            db.collection("Progress")
                                                .document(title.text as String)
                                                .set(doc)
                                                .addOnSuccessListener {
                                                    db.collection("Progress")
                                                        .document(title.text as String)
                                                        .delete()
                                                        .addOnSuccessListener { Log.d(TAG, "dbList progress deleted!") }
                                                        .addOnFailureListener { e -> Log.w(TAG, "Error deleting dbList progress", e) }
                                                    Log.d(TAG, "dbList progress deleted!")
                                                }
                                                .addOnFailureListener { e -> Log.w(TAG, "Error deleting dbList progress", e) }
                                            Log.d(TAG, "DocumentSnapshot successfully deleted! (list)")
                                        }
                                        .addOnFailureListener { e -> Log.w(TAG, "Error deleting document", e) }
                                    Log.d(TAG, "DocumentSnapshot successfully deleted! (doc)") }
                                .addOnFailureListener { e -> Log.w(TAG, "Error deleting document", e) }
                        }
                        Log.d(TAG, "DocumentSnapshot successfully deleted!")
                    }
                    .addOnFailureListener { e -> Log.w(TAG, "Error deleting document", e) }
                val remove = TodoList(title.text as String)
                ListDepositoryManager.instance.removeList(remove)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ListLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    fun updateCollection(newLists:List<TodoList>){
        lists = newLists
        notifyDataSetChanged()
    }

}