package com.vishal.list_creater_app.Items

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.vishal.list_creater_app.Database.ListDatabaseDao

class ItemViewModelFactory(val datasource:ListDatabaseDao,val id:Long):ViewModelProvider.Factory  {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ItemViewModel::class.java)) {
            return ItemViewModel( datasource,id) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}