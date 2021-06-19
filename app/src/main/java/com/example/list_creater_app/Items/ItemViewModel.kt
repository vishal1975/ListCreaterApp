package com.example.list_creater_app.Items

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.list_creater_app.Database.Item
import com.example.list_creater_app.Database.ItemList
import com.example.list_creater_app.Database.ListDatabaseDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception

class ItemViewModel(val datasource:ListDatabaseDao,val id: Long) : ViewModel() {
    // TODO: Implement the ViewModel
    var _item= MutableLiveData<List<Item>?>()
    init {
        viewModelScope.launch {
            _item.value=  getItemList()
        }
    }

    fun insert_list(item: Item){

        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                datasource.insertItem(item)
            }
            //_item.value=datasource.getAllItem(id)
            _item.value=getItemList()

        }

    }
   suspend fun getItemList():List<Item>{

       return withContext(Dispatchers.IO) {
            datasource.getAllItem(id)

        }

    }


    fun updateItem(item: Item){
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                try {


                    datasource.updateItem(item)
                }catch (e:Exception){
                    Log.v("hello", "just ${e.message}")
                }
            }
            _item.value=getItemList()
        }
    }

//    fun deleteAllList(){
//        viewModelScope.launch {
//            datasource.deleteAllList()
////            val query = SimpleSQLiteQuery(
////                    "DROP TABLE ItemList")
////            datasource.deleteAllList(query)
//            getItemList()
//        }
 //   }



}