package com.example.huskeliste1

import com.example.huskeliste1.data.Items

class ItemClass {
    private lateinit var itemsGroup: MutableList<Items>
    var onItems: ((List<Items>) -> Unit)? = null
    fun addItem(item: Items) {
        itemsGroup.add(item)
        onItems?.invoke(itemsGroup)
    }
    fun loadItems() {
        itemsGroup = mutableListOf()
        onItems?.invoke(itemsGroup)
    }
    fun deleteChecked() {
        itemsGroup.removeAll { itemsGroup -> itemsGroup.isChecked }
        onItems?.invoke(itemsGroup)
    }
    companion object {
        val instance = ItemClass()
    }
}