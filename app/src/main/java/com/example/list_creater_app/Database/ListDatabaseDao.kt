package com.example.list_creater_app.Database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.RawQuery
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery


@Dao
interface ListDatabaseDao {
    // insert the list
    @Insert
    suspend fun insertItemList(itemList: ItemList)

    // insert the item in a list
    @Insert
     suspend fun insertItem(item: Item)

    // to get all the list
    @Query("SELECT * FROM ItemList")
     suspend fun getAllItemList():List<ItemList>

    // to get all the item of a partcular list
    @Query("SELECT * FROM Item WHERE list_id=:list_id")
     suspend fun getAllItem(list_id: Long):List<Item>

     @Query("DELETE FROM ItemList")
     suspend fun deleteAllList()
    @Query("DELETE FROM ItemList WHERE listId=:id")
    suspend fun deleteList(id:Long)
//    @RawQuery
//    suspend fun deleteAllList(query: SupportSQLiteQuery):List<ItemList>



}

