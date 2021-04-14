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
        var progressValue: Float = 0.00F
        holder.bind(item)

        holder.itemView.apply {
            tvTodoTitle.text = item.item
            cbDone.isChecked = item.isChecked

            cbDone.setOnCheckedChangeListener { _, isChecked ->
                item.isChecked = !item.isChecked

                // Changes status in Firestore
                // ------------------------------------------------------------------------ //
                db.collection("Lists")
                    .document(receivedListFormatted.replace(")", ""))
                    .collection(receivedListFormatted.replace(")", ""))
                    .document(tvTodoTitle.text as String)
                    .update("done", cbDone.isChecked)
                    .addOnSuccessListener {
                        println("Changed status successfully!")
                    }
                    .addOnFailureListener { e ->
                        println("Failed to change status!")
                    }
                // ------------------------------------------------------------------------ //

                // Calculates and updates progress
                // ------------------------------------------------------------------------ //
                db.collection("Lists")
                    .document(receivedListFormatted.replace(")", ""))
                    .collection(receivedListFormatted.replace(")", ""))
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
                            .document(receivedListFormatted.replace(")", ""))
                            .set(doc)
                            .addOnSuccessListener {
                                println("Changed progress")
                            }
                            .addOnFailureListener {
                                println("Failed to change progress!")
                            }

                        // Set values back to 0
                        countTrue = 0
                        totalItems = 0
                        progressValue = 0.0F
                    }
                    .addOnFailureListener { e ->
                        println("Failed to get progress!")
                    }
                // ------------------------------------------------------------------------ //
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    fun updateItemCollection(newItems:List<Items>){
        items = newItems
        notifyDataSetChanged()
    }

}