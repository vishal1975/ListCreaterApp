package com.example.list_creater_app.Database

import androidx.room.Embedded
import androidx.room.Relation

data class ListWithItem(
    @Embedded val list:ItemList,
    @Relation(
        parentColumn = "listId",
        entityColumn = "list_id"
    )
    val item:List<Item>
)