package com.example.list_creater_app.DashBoard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.list_creater_app.Database.ListDatabaseDao
import com.example.list_creater_app.Lists.ListViewModel

class DashboardViewModelFactory(val datasource: ListDatabaseDao): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DashBoardViewModel::class.java)) {
            return DashBoardViewModel( datasource) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}