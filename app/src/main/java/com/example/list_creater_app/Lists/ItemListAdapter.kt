package com.example.list_creater_app.Lists

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.viewModelScope
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.list_creater_app.Database.ItemList


import com.example.list_creater_app.R
import com.example.list_creater_app.databinding.ItemListItemsBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.launch

class ItemListAdapter(val viewModel: ListViewModel): ListAdapter<ItemList, ItemListAdapter.ViewHolder>(ItemListDiffCallback()) {
    lateinit var itemListAdapterClickHandle:ItemListAdapterClickHandle


    fun seTItemListAdapterClickHandle(itemListAdapterClickHandle:ItemListAdapterClickHandle){
        this.itemListAdapterClickHandle=itemListAdapterClickHandle
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemListItemsBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding)
       //return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position),viewModel)
    }

   inner class ViewHolder(val binding:ItemListItemsBinding ) : RecyclerView.ViewHolder(binding.root){

        fun bind(item: ItemList,viewModel: ListViewModel) {
            binding.listName.text=item.listName

            binding.click.setOnClickListener {

                itemListAdapterClickHandle.layoutClick(item.listId)

            }
            binding.delete.setOnClickListener{
                itemListAdapterClickHandle.onDelete(item.listId)

            }
            binding.edit.setOnClickListener(){
                itemListAdapterClickHandle.onEdit(item)
            }
            binding.share.setOnClickListener(){
                itemListAdapterClickHandle.onShare(item.listId)
            }
            viewModel.viewModelScope.launch {
                val count=viewModel.countItem(item.listId)
                binding.count.text="items $count"
            }

            binding.executePendingBindings()
        }






    }



}
class ItemListDiffCallback : DiffUtil.ItemCallback<ItemList>() {

    override fun areItemsTheSame(oldItem: ItemList, newItem: ItemList): Boolean {
        return oldItem.listId == newItem.listId
    }

    override fun areContentsTheSame(oldItem: ItemList, newItem: ItemList): Boolean {
        return oldItem == newItem
    }
}


interface ItemListAdapterClickHandle{
    fun layoutClick(id:Long)
    fun onDelete(id:Long)
    fun onEdit(item: ItemList)
    fun onShare(id:Long)
}