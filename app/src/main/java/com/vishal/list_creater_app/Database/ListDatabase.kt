package com.vishal.list_creater_app.Database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [ItemList::class,Item::class],version = 2,exportSchema = false)
abstract class ListDatabase:RoomDatabase() {


    abstract val sleepDatabaseDao: ListDatabaseDao


    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                        "ALTER TABLE ItemList ADD COLUMN type INTEGER NOT NULL DEFAULT(0)")
            }
        }

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
                            .addMigrations(MIGRATION_1_2)
                        .build()

                    INSTANCE = instance
                }


                return instance
            }
        }
    }
}