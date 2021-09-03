package com.vishal.list_creater_app.RecievedList

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.vishal.list_creater_app.Database.ItemList
import com.vishal.list_creater_app.Lists.ItemListAdapter
import com.vishal.list_creater_app.Lists.ListViewModel
import com.vishal.list_creater_app.databinding.ItemListItemsBinding
import kotlinx.coroutines.launch

class RecievedListAdapter (val viewModel: RecievedListViewModel): ListAdapter<ItemList, RecievedListAdapter.ViewHolder>(ItemListDiffCallback()) {
    lateinit var recieveditemListAdapterClickHandle:RecievedItemListAdapterClickHandle


    fun seTItemListAdapterClickHandle(recieveditemListAdapterClickHandle:RecievedItemListAdapterClickHandle){
        this.recieveditemListAdapterClickHandle=recieveditemListAdapterClickHandle
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemListItemsBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position),viewModel)
    }

    inner class ViewHolder(val binding: ItemListItemsBinding) : RecyclerView.ViewHolder(binding.root){

        fun bind(item: ItemList, viewModel: RecievedListViewModel) {
            binding.listName.text=item.listName

            binding.click.setOnClickListener {

                recieveditemListAdapterClickHandle.layoutClick(item.listId,item.listName)

            }
            binding.delete.setOnClickListener{
                recieveditemListAdapterClickHandle.onDelete(item.listId)

            }
            binding.edit.setOnClickListener(){
                recieveditemListAdapterClickHandle.onEdit(item)
            }

            viewModel.viewModelScope.launch {
                val count=viewModel.countItem(item.listId)
                binding.count.text="$count"
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


interface RecievedItemListAdapterClickHandle{
    fun layoutClick(id:Long,name:String)
    fun onDelete(id:Long)
    fun onEdit(item: ItemList)
    fun onShare(id:Long)
}