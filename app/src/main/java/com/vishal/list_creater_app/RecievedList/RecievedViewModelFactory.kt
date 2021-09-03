package com.vishal.list_creater_app.RecievedList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.vishal.list_creater_app.Database.ListDatabaseDao
import com.vishal.list_creater_app.Lists.ListViewModel

class RecievedViewModelFactory(val datasource: ListDatabaseDao): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RecievedListViewModel::class.java)) {
            return RecievedListViewModel( datasource) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}