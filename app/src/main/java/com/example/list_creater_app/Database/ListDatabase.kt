package com.example.list_creater_app.Database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
@Database(entities = [ItemList::class,Item::class],version = 1,exportSchema = false)
abstract class ListDatabase:RoomDatabase() {


    abstract val sleepDatabaseDao: ListDatabaseDao


    companion object {

        @Volatile
        private var INSTANCE: ListDatabase? = null


        fun getInstance(context: Context): ListDatabase {

            synchronized(this) {


                var instance = INSTANCE


                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        ListDatabase::class.java,
                        "list_of_items_database"
                    )

                        .fallbackToDestructiveMigration()
                        .build()

                    INSTANCE = instance
                }


                return instance
            }
        }
    }
}