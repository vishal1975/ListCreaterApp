package com.vishal.list_creater_app.Database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ItemList(
    @PrimaryKey(autoGenerate = true)
    val listId:Long=0L,
    val listName:String="",
    val type:Int=0 // 1 for recieved file 2 for sent file 0 for other file

)