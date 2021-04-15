package com.example.huskeliste1

import android.view.LayoutInflater
import android.view.ViewGroup
import kotlinx.android.synthetic.main.item_layout.view.*
import com.example.huskeliste1.databinding.ItemLayoutBinding
import com.google.firebase.ktx.Firebase
import com.google.firebase.firestore.ktx.firestore
import androidx.recyclerview.widget.RecyclerView
import com.example.huskeliste1.data.Items

var progressItems: Int = 0

class ItemsCollectionAdapter(private var items:List<Items>) : RecyclerView.Adapter<ItemsCollectionAdapter.ViewHolder>() {

    class ViewHolder(private val binding: ItemLayoutBinding):RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Items) {
            binding.tvTodoTitle.text = item.item
        }
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        val db = Firebase.firestore
        var countTrue = 0
        var totalItems: Int
        var progressValue = 0.0F
        holder.bind(item)

        holder.itemView.apply {
            tvTodoTitle.text = item.item
            cbDone.isChecked = item.isChecked

            cbDone.setOnCheckedChangeListener { _, isChecked ->
                item.isChecked = !item.isChecked

                listOnFirebase = replaceSuffix(listOnFirebase)

                db.collection("ListGroups")
                    .document(listOnFirebase)
                    .collection(listOnFirebase)
                    .document(tvTodoTitle.text as String)
                    .update("done", cbDone.isChecked)
                    .addOnSuccessListener { println("Changed status successfully!") }
                    .addOnFailureListener { println("Failed to change status!") }

                db.collection("ListGroups")
                    .document(listOnFirebase)
                    .collection(listOnFirebase)
                    .whereEqualTo("done", true)
                    .get()
                    .addOnSuccessListener { documents ->
                        for (document in documents) {
                            countTrue++
                        }
                        totalItems = items.size
                        progressValue = (countTrue.toFloat() / totalItems.toFloat()) * 100

                        progressItems = progressValue.toInt()

                        println("Progress: $progressItems")

                        val doc = hashMapOf(
                            "progress" to progressItems
                        )

                        db.collection("Progress")
                            .document(listOnFirebase)
                            .set(doc)
                            .addOnSuccessListener { println("Progress changed") }
                            .addOnFailureListener { println("Failed changing progress") }
                        progressValue = 0.0F
                        countTrue = 0
                        totalItems = 0
                    }
                    .addOnFailureListener { println("Failed getting progress") }
            }
        }
    }

    fun replaceSuffix(str: String): String {
        return str.replace(")", "")
    }

    fun updateItemCollection(newItems:List<Items>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }
}