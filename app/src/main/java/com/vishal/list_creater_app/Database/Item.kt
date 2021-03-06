package com.vishal.list_creater_app.Database

import androidx.room.Entity
import androidx.room.ForeignKey

import androidx.room.PrimaryKey


@Entity(foreignKeys = [ForeignKey(entity = ItemList::class,
        parentColumns = arrayOf("listId"),
        childColumns = arrayOf("list_id"),
        onDelete = ForeignKey.CASCADE)])
data class Item(
    @PrimaryKey(autoGenerate = true)
    val itemId:Long=0L,
    val list_id:Long=0L,
    val itemName:String="",
    val quantity:Double=0.0,
    val quantityUnit:String="",
    val amount:Double=0.0,
    val itemDescription:String=""
)