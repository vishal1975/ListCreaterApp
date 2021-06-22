package com.example.list_creater_app.DashBoard

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.list_creater_app.Database.ItemList
import com.example.list_creater_app.Database.ListDatabaseDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DashBoardViewModel(val datasource: ListDatabaseDao) : ViewModel() {
val ok=MutableLiveData<Boolean>(false)
    val _itemList= MutableLiveData<List<ItemList>?>()
   suspend fun insertListName(itemList: ItemList):Job{
       Log.v("hello","insertListname function started")
       return viewModelScope.launch {

            withContext(Dispatchers.IO){
                Log.v("hello","database insertion started")
                datasource.insertItemList(itemList)
                Log.v("hello","database insertion finished")
            }
            Log.v("hello","getting itemlist")
            _itemList.value=getItemList()

        }

    }
    suspend fun getItemList():List<ItemList>{
        //Log.v("hello","database insertion finished")


        return  withContext(Dispatchers.IO) {
            datasource.getAllItemList()
        }

    }
    fun clicked(itemList: ItemList){
       viewModelScope.launch {

           val job:Job= insertListName(itemList)
            job.join()

            ok.value=true
       }
    }
}