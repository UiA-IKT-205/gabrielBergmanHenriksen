package com.example.huskeliste2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.huskeliste2.databinding.ActivityListDetailsBinding
import com.example.huskeliste2.mylists.ItemDepositoryManager
import com.example.huskeliste2.mylists.ListDetailsCollectionAdapter
import com.example.huskeliste2.mylists.data.ListDetails

class ListDetailsActivity : AppCompatActivity() {
    private val TAG:String = "ListProject:ListDetailsActivity"
    private lateinit var binding: ActivityListDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.itemList.layoutManager = LinearLayoutManager(this)
        binding.itemList.adapter = ListDetailsCollectionAdapter(emptyList<ListDetails>())

        val items = ItemDepositoryManager.instance
        items.onItem = {
            (binding.itemList.adapter as ListDetailsCollectionAdapter).updateCollection(it)
        }

        ItemDepositoryManager.instance.loadItems()

        binding.addItemBtn.setOnClickListener {
            val item = binding.newItem.text.toString()

            binding.newItem.setText("")

            if(item.isEmpty())
                Log.e(TAG, "You cannot add an item without a name")

            if(item.isNotEmpty())
                addItem(item)
        }
    }

    private fun addItem(item:String){
        val items = ListDetails(item)
        ItemDepositoryManager.instance.addItem(items)
    }
}