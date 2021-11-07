package com.vishal.list_creater_app.Items

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vishal.list_creater_app.Database.Item
import com.vishal.list_creater_app.Database.ListDatabaseDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.lang.Exception

class ItemViewModel(val datasource:ListDatabaseDao,val id: Long) : ViewModel() {
    // TODO: Implement the ViewModel
    var _item= MutableLiveData<List<Item>?>()
    init {
        viewModelScope.launch {
            _item.value=  getItemList()
        }
    }
 // insert item in database
    fun insert_list(item: Item){

        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                datasource.insertItem(item)
            }
            //_item.value=datasource.getAllItem(id)
            _item.value=getItemList()

        }

    }

    // get all items
   suspend fun getItemList():List<Item>{

       return withContext(Dispatchers.IO) {
            datasource.getAllItem(id)

        }

    }

    // update item
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

   // delete a item
    fun DeleteItem(id:Long){
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                datasource.DeleteItem(id)
            }
            _item.value=getItemList()
        }
    }

     fun createJsonString(itemList: List<Item>, name:String,totalamount:Double,totalitem:Int):LiveData<String>{
         val fileContent=MutableLiveData<String>()
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                val builder=StringBuilder(name)
                builder.append(".json")
                val filename=builder.toString()
                val jsonObject= JSONObject()
                jsonObject.put("listname",name)
                jsonObject.put("totalamount",totalamount)
                jsonObject.put("totalitem",totalitem)
                val jasonarray= JSONArray()
                for(item in itemList){
                    val temp= JSONObject()
                    temp.put("itemName",item.itemName)
                    temp.put("quantity",item.quantity)
                    temp.put("quantityUnit",item.quantityUnit)
                    temp.put("amount",item.amount)
                    temp.put("itemDescription",item.itemDescription)
                    jasonarray.put(temp)
                }
                jsonObject.put("property",jasonarray)
                 fileContent.postValue(jsonObject.toString())



            }
        }
         return fileContent

    }
    fun createJsonStringUtil(fileContent:String){

    }

}