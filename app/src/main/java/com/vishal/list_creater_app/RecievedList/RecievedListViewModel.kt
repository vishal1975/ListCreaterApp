package com.vishal.list_creater_app.RecievedList

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vishal.list_creater_app.Database.ItemList
import com.vishal.list_creater_app.Database.ListDatabaseDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RecievedListViewModel(val datasource: ListDatabaseDao) : ViewModel()  {


    var _itemList= MutableLiveData<List<ItemList>?>()
    val ok= MutableLiveData<Boolean>(false)
    init {
        viewModelScope.launch {
            _itemList.value=getRecievedItemList()
        }
    }

//    suspend fun insert_list(itemList: ItemList): Job {
//
//        return viewModelScope.launch{
//            withContext(Dispatchers.IO) {
//                datasource.insertItemList(itemList)
//            }
//            _itemList.value=getRecievedItemList()
//
//        }
//
//    }
    suspend fun getRecievedItemList():List<ItemList>{


        return  withContext(Dispatchers.IO) {
            datasource.getAllRecievedItemList()
        }

    }
    fun deleteAllList(){
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                datasource.deleteAllList()
            }

            _itemList.value= getRecievedItemList()
        }
    }
    fun deleteList(id:Long){
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                datasource.deleteList(id)
            }
            _itemList.value= getRecievedItemList()
        }
    }

    fun updateList(itemList: ItemList){
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                datasource.updateList(itemList)
            }
            _itemList.value=getRecievedItemList()
        }
    }
    suspend fun countItem(id:Long):Long{

        return withContext(Dispatchers.IO) {
            datasource.countItem(id )
        }
    }
//    fun clicked(itemList: ItemList){
//        viewModelScope.launch {
//
//            val job: Job = insert_list(itemList)
//            job.join()
//
//            ok.value=true
//        }
//    }


}