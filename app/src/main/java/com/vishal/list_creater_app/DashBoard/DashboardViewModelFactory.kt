package com.vishal.list_creater_app.DashBoard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.vishal.list_creater_app.Database.ListDatabaseDao

class DashboardViewModelFactory(val datasource: ListDatabaseDao): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DashBoardViewModel::class.java)) {
            return DashBoardViewModel( datasource) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}