package com.example.list_creater_app.Lists

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.sqlite.db.SimpleSQLiteQuery
import com.example.list_creater_app.Database.ItemList
import com.example.list_creater_app.Database.ListDatabaseDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class ListViewModel(val datasource: ListDatabaseDao) : ViewModel() {
    var _itemList= MutableLiveData<List<ItemList>?>()
init {
    viewModelScope.launch {
        _itemList.value=getItemList()
    }
}

    fun insert_list(itemList: ItemList){

        viewModelScope.launch{
            withContext(Dispatchers.IO) {
                datasource.insertItemList(itemList)
            }
            _itemList.value=getItemList()

        }

    }
   suspend fun getItemList():List<ItemList>{


      return  withContext(Dispatchers.IO) {
           datasource.getAllItemList()
       }

    }
    fun deleteAllList(){
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                datasource.deleteAllList()
            }

            _itemList.value= getItemList()
        }
    }
    fun deleteList(id:Long){
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                datasource.deleteList(id)
            }
           _itemList.value= getItemList()
        }
    }

    fun updateList(itemList: ItemList){
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                datasource.updateList(itemList)
            }
            _itemList.value= getItemList()
        }
    }
    suspend fun countItem(id:Long):Long{

        return withContext(Dispatchers.IO) {
            datasource.countItem(id )
        }
    }


}