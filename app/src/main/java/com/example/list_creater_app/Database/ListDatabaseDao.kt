package com.example.list_creater_app.Database

import androidx.room.*
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery


@Dao
interface ListDatabaseDao {



    // insert the item in a list
    @Insert
     suspend fun insertItem(item: Item)


    // to get all the item of a partcular list
    @Query("SELECT * FROM Item WHERE list_id=:list_id")
    suspend fun getAllItem(list_id: Long):List<Item>

    // to update the item
    @Update
    suspend fun updateItem(item: Item)

    // Deleting a item
    @Query("DELETE FROM Item WHERE itemId=:id ")
    suspend fun DeleteItem(id: Long)

    // insert the list
    @Insert
    suspend fun insertItemList(itemList: ItemList)







    // to get all the list
    @Query("SELECT * FROM ItemList")
     suspend fun getAllItemList():List<ItemList>









     @Query("DELETE FROM ItemList")
     suspend fun deleteAllList()







    @Query("DELETE FROM ItemList WHERE listId=:id")
    suspend fun deleteList(id:Long)

    @Update
    suspend fun updateList(itemList: ItemList)



    // count no of item in list
    @Query("SELECT COUNT(*) FROM Item WHERE list_id=:id")
    suspend fun countItem(id:Long):Long


}

