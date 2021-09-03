package com.vishal.list_creater_app.util

import android.content.Context
import android.net.Uri
import com.vishal.list_creater_app.Database.Item
import com.vishal.list_creater_app.Database.ItemList
import com.vishal.list_creater_app.Database.ListDatabaseDao
import kotlinx.coroutines.*
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader

class ReadAndWriteFile(val itemListInfo: ItemListInfo) {
    val scope = CoroutineScope(Job() + Dispatchers.Main)
//    public fun get_ItemList(datasource: ListDatabaseDao){
//        scope.launch(Dispatchers.IO) {
//            val defered=   async {
//                datasource.getLastList()
//            }
//            val itemList=defered.await()
//            itemListInfo.getItemList(itemList)
//        }
//
//    }

    public fun read(uri :Uri,context: Context,datasource:ListDatabaseDao){

        scope.launch(Dispatchers.Default) {
            val stringBuilder = StringBuilder()

            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                BufferedReader(InputStreamReader(inputStream)).use { reader ->
                    var line: String? = reader.readLine()
                    while (line != null) {
                        stringBuilder.append(line)
                        line = reader.readLine()
                    }
                }
            }
            val contents = stringBuilder.toString()
            val jsonObject= JSONObject(contents)
            val listname=jsonObject.getString("listname")
            val job1=launch {
                 val itemlist=ItemList(listName = listname,type = 1)
                datasource.insertItemList(itemlist)
            }
            job1.join()
         val defered=   async {
                   datasource.getLastList()
            }
            val itemList=defered.await()
            val item: MutableList<Item> = mutableListOf()
            val jsonArray=jsonObject.getJSONArray("property")
            for(i in 0..jsonArray.length()-1){
                val temp=jsonArray.getJSONObject(i)
                val itemName=temp.getString("itemName")
                val quantity=temp.getDouble("quantity")
                val quantityUnit=temp.getString("quantityUnit")
                val amount =temp.getDouble("amount")
                val itemDescription=temp.getString("itemDescription")
                item.add(Item(list_id = itemList.listId,itemName = itemName,quantity = quantity,quantityUnit = quantityUnit,
                amount = amount,itemDescription = itemDescription))

            }
           val job2= launch(Dispatchers.IO) {
                            datasource.insertAllItem(item)
            }
            job2.join()
            itemListInfo.getItemList(itemList)

        }
    }
}
interface ItemListInfo{
     fun getItemList(itemList: ItemList)

}