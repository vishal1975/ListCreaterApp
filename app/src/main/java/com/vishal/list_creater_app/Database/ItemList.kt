package com.vishal.list_creater_app.Database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ItemList(
    @PrimaryKey(autoGenerate = true)
    val listId:Long=0L,
    val listName:String=""

)