package com.example.list_creater_app.Lists

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.list_creater_app.Database.ListDatabaseDao

class ListViewmodelFactory(val datasource: ListDatabaseDao):ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ListViewModel::class.java)) {
            return ListViewModel( datasource) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}