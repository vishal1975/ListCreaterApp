package com.example.list_creater_app.Items

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.list_creater_app.Database.Item
import com.example.list_creater_app.Database.ItemList
import com.example.list_creater_app.Lists.ListFragmentDirections
import com.example.list_creater_app.databinding.AllListitemBinding

import com.example.list_creater_app.databinding.ItemListItemsBinding

class ItemAdapter: ListAdapter<Item, ItemAdapter.ViewHolder>(ItemDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder private constructor(val binding:AllListitemBinding) : RecyclerView.ViewHolder(binding.root){

        fun bind(item: Item) {
            binding.itemname.text=item.itemName
//            binding.listName.setOnClickListener {
//                it.findNavController().navigate(ListFragmentDirections.actionListFragmentToItemFragment().setId(item.listId))
//            }
            // binding.sleep = item
            // binding.clickListener = clickListener
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = AllListitemBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }



}
class ItemDiffCallback : DiffUtil.ItemCallback<Item>() {

    override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean {
        return oldItem.itemId == newItem.itemId
    }

    override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean {
        return oldItem == newItem
    }
}