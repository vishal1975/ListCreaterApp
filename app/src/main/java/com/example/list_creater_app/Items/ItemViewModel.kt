package com.example.list_creater_app.Items

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.list_creater_app.Database.Item
import com.example.list_creater_app.Database.ItemList
import com.example.list_creater_app.Database.ListDatabaseDao
import kotlinx.coroutines.launch

class ItemViewModel(val datasource:ListDatabaseDao,val id: Long) : ViewModel() {
    // TODO: Implement the ViewModel
    var _item= MutableLiveData<List<Item>>()
    init {
        getItemList()
    }

    fun insert_list(item: Item){

        viewModelScope.launch {
            datasource.insertItem(item)
            //_item.value=datasource.getAllItem(id)
            getItemList()

        }

    }
    fun getItemList(){

        viewModelScope.launch {
            _item.value=datasource.getAllItem(id)

        }

    }
    fun deleteAllList(){
        viewModelScope.launch {
            datasource.deleteAllList()
//            val query = SimpleSQLiteQuery(
//                    "DROP TABLE ItemList")
//            datasource.deleteAllList(query)
            getItemList()
        }
    }



}