package com.example.list_creater_app.Database

import androidx.room.Entity
import androidx.room.ForeignKey

import androidx.room.ForeignKey.CASCADE
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
    val quantity:Float=0F,
    val quantityUnit:String="",
    val amount:Float=0F,
    val itemDescription:String=""
)